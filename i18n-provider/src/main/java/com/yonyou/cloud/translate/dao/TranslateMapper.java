package com.yonyou.cloud.translate.dao;
import com.yonyou.cloud.translate.entity.Translate;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericExMapper;
import com.yonyou.iuap.mybatis.anotation.MyBatisRepository;
import java.util.List;


@MyBatisRepository
public interface TranslateMapper extends GenericExMapper<Translate> {
        List selectListByExcelData(List list);
}

