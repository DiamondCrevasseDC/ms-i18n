package com.yonyou.cloud.i18n.controller;

import com.yonyou.cloud.i18n.entity.I18n;
import com.yonyou.cloud.i18n.service.I18nToolsService;
import com.yonyou.cloud.i18n.service.I18nService;
import com.yonyou.i18n.constants.I18nConstants;
import com.yonyou.iuap.baseservice.controller.GenericController;
import com.yonyou.iuap.mvc.annotation.FrontModelExchange;
import com.yonyou.iuap.mvc.type.SearchParams;
import com.yonyou.iuap.utils.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 说明：国际化 基础Controller——提供数据增、删、改、查、导入导出等rest接口
 *
 * @date 2018-9-28 14:32:11
 */
@Controller
@RequestMapping(value = "/i18n")
public class I18nController extends GenericController<I18n> {

    private Logger logger = LoggerFactory.getLogger(I18nController.class);

    private I18nService i18nService;

    @Autowired
    public void settI18nService(I18nService i18nService) {
        this.i18nService = i18nService;
        super.setService(i18nService);
    }

    private I18nToolsService i18nToolsService;

//    public I18nServiceImpl getI18nServiceImpl() {
//        return i18nServiceImpl;
//    }

    @Autowired
    public void setI18nServiceImpl(I18nToolsService i18nToolsService) {
        this.i18nToolsService = i18nToolsService;
    }

    @Override
    public Object list(PageRequest pageRequest,
                       @FrontModelExchange(modelType = I18n.class) SearchParams searchParams) {

        Page<I18n> page = this.i18nService.selectAllByPage(pageRequest, searchParams);

        Map<String, Object> map = new HashMap();
        map.put("data", page);
        return this.buildMapSuccess(map);

    }

    @Override
    public Object save(@RequestBody I18n entity) {
//        JsonResponse jsonResp;
//        try {
//            this.service.save(entity);
//            jsonResp = this.buildSuccess(entity);
//        } catch (Exception var4) {
//            jsonResp = this.buildError("msg", var4.getMessage(), RequestStatusEnum.FAIL_FIELD);
//        }

        return super.save(entity);
    }


    /**
     * 该部分执行下载（国际化后的文件）
     *
     * @param listData
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/download"})
    @ResponseBody
    public Object download(@RequestBody List<I18n> listData, HttpServletRequest request, HttpServletResponse response) {

        System.out.println("++++++++++");
        return super.buildSuccess();
    }


    /**
     * 该部分执行国际化的整体逻辑
     *
     * @param listData
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({"/operation"})
    @ResponseBody
    public Object operation(@RequestBody List<I18n> listData, HttpServletRequest request, HttpServletResponse response) {

        long s = System.currentTimeMillis();
        logger.info("项目工程执行开始时间：" + s);

        try {

            I18n i18n = this.i18nService.findById(listData.get(0).getId());

//            // 先赋空，然后再写入
//            i18n.setAttachId("");
//            this.i18nService.save(i18n);

            // 远程调用时传递过去的是绝对路径(即磁盘路径)，确保服务可以正常访问
            String path = PropertyUtil.getPropertyByKey("storeDir") + File.separator + i18n.getAttachment().get(0).getFileName();

            String zipPath = path.substring(0, path.lastIndexOf(".")) + "_" + System.currentTimeMillis();


            // 1: JQuery   2: React
            // projectType=JQuery
            String projectType = "React";
            if ("1".equalsIgnoreCase(i18n.getProjectType())) {
                projectType = "JQuery";
            } else if ("2".equalsIgnoreCase(i18n.getProjectType())) {
                projectType = "React";
            } else if ("3".equalsIgnoreCase(i18n.getProjectType())) {
                projectType = "Properties";
            }

            this.i18nToolsService.operation(path, zipPath, projectType);

//            String zipFile = this.i18nToolsService.operation(path, projectType);
//
            zipPath = zipPath.substring(zipPath.lastIndexOf("/") + 1) + I18nConstants.FILE_ZIP_POSTFIX;

            // 保存时存放是相对的可以直接下载的路径（）
            String f = i18n.getAttachment().get(0).getAccessAddress();
            f = f.substring(0, f.lastIndexOf("/")) + File.separator +  zipPath;

            i18n.setAttachId(f);

            this.i18nService.save(i18n);

        } catch (Exception e) {
            e.printStackTrace();
        }

        long e = System.currentTimeMillis();
        logger.info("项目工程执行结束时间：" + e + " , 共耗时： " + (e - s) / 1000);
        return super.buildSuccess();
    }

}