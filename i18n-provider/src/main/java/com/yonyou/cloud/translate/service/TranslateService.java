package com.yonyou.cloud.translate.service;

import com.yonyou.cloud.translate.dao.TranslateMapper;
import com.yonyou.cloud.translate.entity.Translate;
import com.yonyou.iuap.baseservice.intg.service.GenericIntegrateService;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeature;
import com.yonyou.iuap.baseservice.ref.service.RefCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.yonyou.iuap.baseservice.intg.support.ServiceFeature.REFERENCE;


/**
 * Translate CRUD 核心服务,提供逻辑删除/乐观锁
 */
@Service
public class TranslateService extends GenericIntegrateService<Translate> {


    private static final Logger logger = LoggerFactory.getLogger(TranslateService.class);

    private TranslateMapper translateMapper;

    @Autowired
    public void setTranslateMapper(TranslateMapper translateMapper) {
        this.translateMapper = translateMapper;
        super.setGenericMapper(translateMapper);
    }

    @Autowired
    private RefCommonService refService;

    public List selectListByExcelData(List idsList) {
        List list = translateMapper.selectListByExcelData(idsList);
        list = refService.fillListWithRef(list);
        return list;
    }

    public Translate findByCode(String codeValue) {
        return this.findUnique("propertyCode", codeValue);
    }

    public List<Translate> findByCode(Properties properties) {

        List<Translate> listData = new ArrayList<Translate>();
        for (String key : properties.stringPropertyNames()) {
            try {
                listData.add(this.findUnique("propertyCode", key));
            } catch (Exception e) {
                logger.error("获取写入的资源异常，code值为：" + key + "，异常原因：" + e);
            }
        }
        return listData;
    }


    /**
     * @return 向父类 GenericIntegrateService 提供可插拔的特性声明
     * @CAU 可插拔设计
     */
    @Override
    protected ServiceFeature[] getFeats() {
        return new ServiceFeature[]{REFERENCE};
    }
}