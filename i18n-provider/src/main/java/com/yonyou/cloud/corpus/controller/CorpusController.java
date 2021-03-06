package com.yonyou.cloud.corpus.controller;

import cn.hutool.core.util.StrUtil;
import com.yonyou.cloud.corpus.entity.Corpus;
import com.yonyou.cloud.corpus.service.CorpusService;
import com.yonyou.i18n.model.OrderedProperties;
import com.yonyou.i18n.utils.Helper;
import com.yonyou.i18n.utils.ResourceFileUtil;
import com.yonyou.iuap.base.web.BaseController;
import com.yonyou.iuap.common.utils.ExcelExportImportor;
import com.yonyou.iuap.mvc.annotation.FrontModelExchange;
import com.yonyou.iuap.mvc.constants.RequestStatusEnum;
import com.yonyou.iuap.mvc.type.JsonResponse;
import com.yonyou.iuap.mvc.type.SearchParams;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 说明：资源翻译 基础Controller——提供数据增、删、改、查、导入导出等rest接口
 *
 * @date 2018-10-11 14:58:51
 */
@Controller
@RequestMapping(value = "/corpus")
public class CorpusController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(CorpusController.class);

    private CorpusService corpusService;

    @Autowired
    public void setCorpusService(CorpusService corpusService) {
        this.corpusService = corpusService;
    }


    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(PageRequest pageRequest, SearchParams searchParams) {
        Page<Corpus> page = this.corpusService.selectAllByPage(pageRequest, searchParams);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", page);
        return this.buildMapSuccess(map);
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public Object get(PageRequest pageRequest, SearchParams searchParams) {
        String id = MapUtils.getString(searchParams.getSearchMap(), "id");
        if (id == null) {
            return this.buildSuccess();//前端约定传空id则拿到空对象
        }
        if (StrUtil.isBlank(id)) {
            return this.buildError("msg", "主键id参数为空!", RequestStatusEnum.FAIL_FIELD);
        } else {
            Corpus entity = this.corpusService.findById(id);
            return this.buildSuccess(entity);
        }
    }

    /**
     * 该方法主要是为了初始化语料库而存在。
     */
    @Deprecated
    @RequestMapping(value = "/saveall")
    @ResponseBody
    public void saveAll() {

        String path = "/Users/yanyong/Downloads/corpus/";

        ResourceFileUtil rf = new ResourceFileUtil();
        rf.init(path, "zh_CN.properties");

        OrderedProperties properties = rf.getPropsFromFiles();


        ResourceFileUtil rf1 = new ResourceFileUtil();
        rf1.init(path, "en_US.properties");

        OrderedProperties properties1 = rf1.getPropsFromFiles();

        List<String> errorList = new ArrayList<String>();
        Corpus c;

        int i = 0;
        int j = 0;
        for (String key : properties.stringPropertyNames()) {

            c = new Corpus();

            c.setChinese(Helper.unwindEscapeChars(properties.getProperty(key)));
            c.setEnglish(Helper.unwindEscapeChars(properties1.getProperty(key)));

            try {
                this.corpusService.save(c);
                i++;
            } catch (Exception e) {
                errorList.add(key);
                j++;
            }
        }

        logger.info("***执行资源写入数据库完成！总条数为：" + (i + j) + "***保存条数为：" + i + "***异常条数为：" + j + "***异常数据的key为：" + errorList);


    }


    @RequestMapping(value = "/save")
    @ResponseBody
    public Object save(@RequestBody Corpus entity) {
        JsonResponse jsonResp;
        try {
            this.corpusService.save(entity);
            jsonResp = this.buildSuccess(entity);
        } catch (Exception exp) {
            jsonResp = this.buildError("msg", exp.getMessage(), RequestStatusEnum.FAIL_FIELD);
        }
        return jsonResp;
    }

    @RequestMapping(value = "/saveBatch")
    @ResponseBody
    public Object saveBatch(@RequestBody List<Corpus> listData) {
        this.corpusService.saveBatch(listData);
        return this.buildSuccess();
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestBody Corpus entity, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.corpusService.delete(entity);
        return super.buildSuccess();
    }

    @RequestMapping(value = "/deleteBatch")
    @ResponseBody
    public Object deleteBatch(@RequestBody List<Corpus> listData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.corpusService.deleteBatch(listData);
        return super.buildSuccess();
    }


    @RequestMapping(value = "/excelTemplateDownload", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Map<String, String> excelTemplateDownload(HttpServletRequest request,
                                                     HttpServletResponse response) {
        Map<String, String> result = new HashMap<String, String>();

        try {
            ExcelExportImportor.downloadExcelTemplate(response, getImportHeadInfo(), "资源翻译", "资源翻译模板");
            result.put("status", "success");
            result.put("msg", "Excel模版下载成功");
        } catch (Exception e) {
            logger.error("Excel模版下载失败", e);
            result.put("status", "failed");
            result.put("msg", "Excel模版下载失败");
        }
        return result;
    }

    @RequestMapping(value = "/toImportExcel", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> importExcel(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();
        try {

            List<Corpus> list = new ArrayList<Corpus>();
            CommonsMultipartResolver resolver = new CommonsMultipartResolver();
            if (resolver.isMultipart(request)) {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                int size = multipartRequest.getMultiFileMap().size();
                MultiValueMap<String, MultipartFile> multiValueMap = multipartRequest.getMultiFileMap();
                if (multiValueMap != null && size > 0) {
                    for (MultiValueMap.Entry<String, List<MultipartFile>> me : multiValueMap.entrySet()) {
                        List<MultipartFile> multipartFile = me.getValue();
                        for (MultipartFile mult : multipartFile) {
                            String multName = mult.getOriginalFilename().toString();
                            String multTypeName = multName.substring(multName.lastIndexOf(".") + 1, multName.length());
                            if ((multTypeName != "xlsx" && !"xlsx".equals(multTypeName)) && (multTypeName != "xls" && !"xls".equals(multTypeName))) {
                                throw new Exception("导入数据格式异常！");
                            }
                            list = ExcelExportImportor.loadExcel(mult.getInputStream(), getImportHeadInfo(), Corpus.class);
                            if (list == null || list.size() == 0) {
                                throw new Exception("导入数据异常！");
                            }
                        }
                    }
                }
            }
            corpusService.saveBatch(list);
            result.put("status", "success");
            result.put("msg", "Excel导入成功");
        } catch (Exception e) {
            logger.error("Excel导入失败", e);
            result.put("status", "failed");
            result.put("msg", e.getMessage() != null ? e.getMessage() : "Excel导入失败");
        }
        return result;
    }

    @RequestMapping(value = "/toExportExcel", method = RequestMethod.POST)
    @ResponseBody
    public Object exportExcel(PageRequest pageRequest,
                              @FrontModelExchange(modelType = Corpus.class) SearchParams searchParams, HttpServletResponse response, @RequestBody List<Corpus> dataList) {

        Map<String, String> result = new HashMap<String, String>();
        try {
            List idsList = new ArrayList();
            for (Corpus entity : dataList) {
                idsList.add(entity.getId());
            }
            List list = corpusService.selectListByExcelData(idsList);
            ExcelExportImportor.writeExcel(response, list, getExportHeadInfo(), "资源翻译", "资源翻译");
            result.put("status", "success");
            result.put("msg", "信息导出成功");
            result.put("fileName", "资源翻译");
        } catch (Exception e) {
            logger.error("Excel下载失败", e);
            result.put("status", "failed");
            result.put("msg", "Excel下载失败");
        }
        return result;
    }

    @RequestMapping(value = "/toExportExcelAll", method = RequestMethod.GET)
    @ResponseBody
    public Object exportExcelAll(PageRequest pageRequest,
                                 @FrontModelExchange(modelType = Corpus.class) SearchParams searchParams, HttpServletResponse response) {

        Map<String, String> result = new HashMap<String, String>();
        try {
            Page<Corpus> page = corpusService.selectAllByPage(pageRequest, searchParams);
            List list = page.getContent();
            if (list == null || list.size() == 0) {
                throw new Exception("没有导出数据！");
            }
            ExcelExportImportor.writeExcel(response, list, getExportHeadInfo(), "资源翻译", "资源翻译");
            result.put("status", "success");
            result.put("msg", "信息导出成功");
        } catch (Exception e) {
            logger.error("Excel下载失败", e);
            result.put("status", "failed");
            result.put("msg", "Excel下载失败");
        }
        return result;
    }

    private Map<String, String> getExportHeadInfo() {
        String values = "{'chinese':'中文','traditional':'中文繁体','english':'英文','french':'法文',}";
        return getMapInfo(values);
    }

    private Map<String, String> getImportHeadInfo() {
        String values = "{'chinese':'中文','traditional':'中文繁体','english':'英文','french':'法文',}";
        return getMapInfo(values);
    }

    private Map<String, String> getMapInfo(String values) {
        String values_new = values.substring(0, values.length() - 1);
        if (values_new.endsWith(",")) {
            values = values_new.substring(0, values_new.length() - 1) + "}";
        }
        Map<String, String> headInfo = null;
        //if (headInfo == null) {
        JSONObject json = JSONObject.fromObject(values);
        headInfo = (Map<String, String>) json;
        //}
        return headInfo;
    }


}