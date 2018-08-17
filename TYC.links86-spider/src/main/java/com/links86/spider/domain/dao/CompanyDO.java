package com.links86.spider.domain.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyDO {
    private String id;
    private String name;
    /**
     * 注册时间
     */
    private String registryTime;

    /**
     * 注册资本
     */
    private String registryMoney;

    /**
     * 状态
     */
    private String status;

    /**
     * 行业
     */
    private String category;

    /**
     * 企业类型
     */
    private String type;

    /**
     * 工商注册号
     */
    private String registryNo;

    /**
     * 组织结构代码
     */
    private String organizationCode;

    /**
     * 统一信用代码
     */
    private String unifiedCredit;

    /**
     * 纳税人识别号
     */
    private String TaxNo;

    /**
     * 经营期限
     */
    private String operatingPeriod;

    /**
     * 核准日期
     */
    private String approvalDate;

    /**
     * 登记机关
     */
    private String registration;

    /**
     * 注册地址
     */
    private String address;

    /**
     * 经营范围
     */
    private String scope;

}
