package com.links86.spider.domain.dao;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author dyu
 * @date 2018/08/23
 */
@Data
@Entity(name = "company_south_west_temp")
@NoArgsConstructor
public class CompanySouthWestTempDO {

    @Id
    private String id;
    private String tycId;
    private String score;
    private String email;
    private String website;
    private String name;
    private String legal;
    private Integer flag;
    /**
     * 注册时间
     */
    private String startTime;

    /**
     * 注册资本
     */
    private String capitalInvested;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 行业
     */
    private String industry;

    /**
     * 企业类型
     */
    private String type;

    /**
     * 工商注册号
     */
    private String registerNumber;

    /**
     * 组织结构代码
     */
    private String orgCode;

    /**
     * 统一信用代码
     */
    private String creditCode;

    /**
     * 纳税人识别号
     */
    private String taxCode;

    /**
     * 经营期限
     */
    private String terms;

    /**
     * 登记机关
     */
    private String registerAuthority;

    /**
     * 注册地址
     */
    private String address;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 详情json
     */
    private String infoJson;

    /**
     * 变更json
     */
    private String changeJson;
}
