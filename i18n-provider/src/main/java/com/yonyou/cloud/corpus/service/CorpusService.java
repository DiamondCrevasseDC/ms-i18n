package com.yonyou.cloud.corpus.service;

import com.yonyou.cloud.corpus.dao.CorpusMapper;
import com.yonyou.cloud.corpus.entity.Corpus;
import com.yonyou.i18n.model.OrderedProperties;
import com.yonyou.iuap.baseservice.intg.service.GenericIntegrateService;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeature;
import com.yonyou.iuap.baseservice.ref.service.RefCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yonyou.iuap.baseservice.intg.support.ServiceFeature.REFERENCE;


/**
 * CorpusTraditional CRUD 核心服务,提供逻辑删除/乐观锁
 */
@Service
public class CorpusService extends GenericIntegrateService<Corpus> {


    private static final Logger logger = LoggerFactory.getLogger(CorpusService.class);

    private CorpusMapper corpusMapper;

    @Autowired
    public void setCorpusMapper(CorpusMapper corpusMapper) {
        this.corpusMapper = corpusMapper;
        super.setGenericMapper(corpusMapper);
    }

    @Autowired
    private RefCommonService refService;

    public List selectListByExcelData(List idsList) {
        List list = corpusMapper.selectListByExcelData(idsList);
        list = refService.fillListWithRef(list);
        return list;
    }

    /**
     * 获取中英文语料库：key为中文，value为英文
     *
     * @return
     */
    public OrderedProperties getEnglishCorpus() {

        OrderedProperties op = new OrderedProperties();

        List<Corpus> ec = this.findAll();

        logger.info("系统直接检索出来的语料库条数（包含很多重复的数据)：" + ec.size() );
        for (Corpus c : ec) {

            op.put(c.getChinese(), c.getEnglish());
        }

        return op;

    }


//    public Corpus findByCode(String codeValue) {
//        return this.findUnique("propertyCode", codeValue);
//    }

//    public List<Corpus> findByCode(Properties properties) {
//
//        List<Corpus> listData = new ArrayList<Corpus>();
//        for (String key : properties.stringPropertyNames()) {
//            try {
//                listData.add(this.findUnique("propertyCode", key));
//            } catch (Exception e) {
//                logger.error("获取写入的资源异常，code值为：" + key + "，异常原因：" + e);
//            }
//        }
//        return listData;
//    }


    /**
     * @return 向父类 GenericIntegrateService 提供可插拔的特性声明
     * @CAU 可插拔设计
     */
    @Override
    protected ServiceFeature[] getFeats() {
        return new ServiceFeature[]{REFERENCE};
    }
}