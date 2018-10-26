package com.yonyou.cloud.translate.entity;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yonyou.iuap.baseservice.entity.AbsDrModel;
import com.yonyou.iuap.baseservice.entity.annotation.Reference;

import com.yonyou.iuap.baseservice.support.condition.Condition;
import com.yonyou.iuap.baseservice.support.condition.Match;
import com.yonyou.iuap.baseservice.support.generator.GeneratedValue;
import com.yonyou.iuap.baseservice.support.generator.Strategy;
import com.yonyou.iuap.baseservice.entity.annotation.CodingEntity;
import com.yonyou.iuap.baseservice.entity.annotation.CodingField;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.math.BigDecimal;

/**
 * 资源翻译
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "translate")

@CodingEntity(codingField="")
public class Translate extends AbsDrModel implements Serializable
{
    @Id
    @GeneratedValue
    @Condition
    protected String id;//ID
    @Override
    public String getId() {
        return id;
    }
    @Override
    public void setId(Serializable id){
        this.id= id.toString();
        super.id = id;
    }
    public void setId(String id) {
        this.id = id;
    }
    


    @Condition(match=Match.LIKE)
    @Column(name="CHINESE")
    private String chinese;        //中文

    public void setChinese(String chinese){
        this.chinese = chinese;
    }
    public String getChinese(){
        return this.chinese;
    }
    

    @Condition(match=Match.LIKE)
    @Column(name="TRADITIONAL")
    private String traditional;        //中文繁体

    public void setTraditional(String traditional){
        this.traditional = traditional;
    }
    public String getTraditional(){
        return this.traditional;
    }
    

    @Condition(match=Match.LIKE)
    @Column(name="PROPERTY_CODE")
    private String propertyCode;        //资源编码

    public void setPropertyCode(String propertyCode){
        this.propertyCode = propertyCode;
    }
    public String getPropertyCode(){
        return this.propertyCode;
    }
    

    @Condition(match=Match.LIKE)
    @Column(name="ENGLISH")
    private String english;        //英文

    public void setEnglish(String english){
        this.english = english;
    }
    public String getEnglish(){
        return this.english;
    }
    

    @Condition
    @Column(name="RESERVE1")
    private String reserve1;        //预留1

    public void setReserve1(String reserve1){
        this.reserve1 = reserve1;
    }
    public String getReserve1(){
        return this.reserve1;
    }
    

    @Condition
    @Column(name="RESERVE2")
    private String reserve2;        //预留

    public void setReserve2(String reserve2){
        this.reserve2 = reserve2;
    }
    public String getReserve2(){
        return this.reserve2;
    }
    

    @Condition(match=Match.LIKE)
    @Column(name="FRENCH")
    private String french;        //法文

    public void setFrench(String french){
        this.french = french;
    }
    public String getFrench(){
        return this.french;
    }
    





}




