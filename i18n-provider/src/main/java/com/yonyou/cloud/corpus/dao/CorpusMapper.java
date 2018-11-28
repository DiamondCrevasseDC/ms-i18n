package com.yonyou.cloud.corpus.dao;
import com.yonyou.cloud.corpus.entity.Corpus;
import com.yonyou.iuap.baseservice.persistence.mybatis.mapper.GenericExMapper;
import com.yonyou.iuap.mybatis.anotation.MyBatisRepository;
import java.util.List;


@MyBatisRepository
public interface CorpusMapper extends GenericExMapper<Corpus> {
        List selectListByExcelData(List list);
}

