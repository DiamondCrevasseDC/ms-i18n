package com.yonyou.cloud.translate.controller;

import cn.hutool.core.util.StrUtil;
import com.yonyou.cloud.translate.entity.Translate;
import com.yonyou.cloud.translate.service.TranslateService;
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
@RequestMapping(value = "/translate")
public class TranslateController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(TranslateController.class);

    private TranslateService translateService;

    @Autowired
    public void setTranslateService(TranslateService translateService) {
        this.translateService = translateService;
    }


    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(PageRequest pageRequest, SearchParams searchParams) {
        Page<Translate> page = this.translateService.selectAllByPage(pageRequest, searchParams);
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
            Translate entity = this.translateService.findById(id);
            return this.buildSuccess(entity);
        }
    }


    @RequestMapping(value = "/save")
    @ResponseBody
    public Object save(@RequestBody Translate entity) {
        JsonResponse jsonResp;
        try {
            this.translateService.save(entity);
            jsonResp = this.buildSuccess(entity);
        } catch (Exception exp) {
            jsonResp = this.buildError("msg", exp.getMessage(), RequestStatusEnum.FAIL_FIELD);
        }
        return jsonResp;
    }

    @RequestMapping(value = "/saveBatch")
    @ResponseBody
    public Object saveBatch(@RequestBody List<Translate> listData) {
        this.translateService.saveBatch(listData);
        return this.buildSuccess();
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestBody Translate entity, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.translateService.delete(entity);
        return super.buildSuccess();
    }

    @RequestMapping(value = "/deleteBatch")
    @ResponseBody
    public Object deleteBatch(@RequestBody List<Translate> listData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.translateService.deleteBatch(listData);
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

    /**
     * 根据自动的导入功能进行更改，同时处理insert以及update操作
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/toImportExcel", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> importExcel(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();
        try {

            List<Translate> list = new ArrayList<Translate>();

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
                            list = ExcelExportImportor.loadExcel(mult.getInputStream(), getImportHeadInfo(), Translate.class);
                            if (list == null || list.size() == 0) {
                                throw new Exception("导入数据异常！");
                            }

                            List<Translate> list1;
                            // 针对Excel解析的数据进行逻辑判断处理
                            for (Translate translate : list) {

                                // 该部分有一个前提是所有的code都是唯一的
                                list1 = translateService.queryList("propertyCode", translate.getPropertyCode());

                                if (list1 == null || list1.size() == 0) {
                                    translateService.save(translate);
                                } else if (list1.size() == 1) {
                                    list1.get(0).setEnglish(translate.getEnglish());
                                    translateService.update(list1.get(0));
                                } else {
                                    logger.info("****数据库资源CODE存在问题，请排查：" + translate.getPropertyCode());
                                }
                            }
                        }
                    }
                }
            }
//            translateService.saveBatch(list);
            result.put("status", "success");
            result.put("msg", "Excel导入成功");
        } catch (Exception e) {
            logger.error("Excel导入失败", e);
            result.put("status", "failed");
            result.put("msg", e.getMessage() != null ? e.getMessage() : "Excel导入失败");
        }
        return result;
    }

    /**
     * 自动生成的导入功能：只处理简单的insert操作
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/toImportExcel-frush", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> importExcelFrush(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();
        try {

            List<Translate> list = new ArrayList<Translate>();
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
                            list = ExcelExportImportor.loadExcel(mult.getInputStream(), getImportHeadInfo(), Translate.class);
                            if (list == null || list.size() == 0) {
                                throw new Exception("导入数据异常！");
                            }
                        }
                    }
                }
            }
            translateService.saveBatch(list);
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
                              @FrontModelExchange(modelType = Translate.class) SearchParams searchParams, HttpServletResponse response, @RequestBody List<Translate> dataList) {

        Map<String, String> result = new HashMap<String, String>();
        try {
            List idsList = new ArrayList();
            for (Translate entity : dataList) {
                idsList.add(entity.getId());
            }
            List list = translateService.selectListByExcelData(idsList);
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
                                 @FrontModelExchange(modelType = Translate.class) SearchParams searchParams, HttpServletResponse response) {

        Map<String, String> result = new HashMap<String, String>();
        try {
            Page<Translate> page = translateService.selectAllByPage(pageRequest, searchParams);
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
        String values = "{'chinese':'中文','traditional':'中文繁体','propertyCode':'资源编码','english':'英文','french':'法文',}";
        return getMapInfo(values);
    }

    private Map<String, String> getImportHeadInfo() {
        String values = "{'chinese':'中文','traditional':'中文繁体','propertyCode':'资源编码','english':'英文','french':'法文',}";
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