package com.yonyou.cloud.i18n.service;

import com.yonyou.cloud.i18n.dao.I18nMapper;
import com.yonyou.cloud.i18n.entity.I18n;
import com.yonyou.iuap.baseservice.intg.service.GenericIntegrateService;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.yonyou.iuap.baseservice.intg.support.ServiceFeature.ATTACHMENT;
import static com.yonyou.iuap.baseservice.intg.support.ServiceFeature.REFERENCE;

/**
 * 执行自身的对象操作使用该类
 */
@Service
public class I18nService extends GenericIntegrateService<I18n> {


    private I18nMapper i18nMapper;


    @Autowired
    public void settI18nMapper(I18nMapper i18nMapper) {
        this.i18nMapper = i18nMapper;

        super.setGenericMapper(i18nMapper, "i18nEnumService");
    }

    @Override
    protected ServiceFeature[] getFeats() {
        return new ServiceFeature[]{REFERENCE, ATTACHMENT};
    }
}