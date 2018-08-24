package com.links86.spider.domain.dao;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "company_east_temp")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "id"
        })
})
public class CompanyDO {
    @Id
    private String id;
    private String tycId;
    private String score;
    private String email;
    private String website;
    private String name;
    private String legal;
    @Column(length = 1)
    @ColumnDefault(value = "2")
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
     * @return
     */
    private String infoJson;

    /**
     * 变更json
     * @return
     */
    private String changeJson;

    private String tel;

    @Override
    public String toString() {
        return "CompanyDO{" +
                "id='" + id + '\'' +
                ", tycId='" + tycId + '\'' +
                ", score='" + score + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", name='" + name + '\'' +
                ", legal='" + legal + '\'' +
                ", startTime='" + startTime + '\'' +
                ", capitalInvested='" + capitalInvested + '\'' +
                ", status='" + status + '\'' +
                ", industry='" + industry + '\'' +
                ", type='" + type + '\'' +
                ", registerNumber='" + registerNumber + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", creditCode='" + creditCode + '\'' +
                ", taxCode='" + taxCode + '\'' +
                ", terms='" + terms + '\'' +
                ", registerAuthority='" + registerAuthority + '\'' +
                ", address='" + address + '\'' +
                ", businessScope='" + businessScope + '\'' +
                '}';
    }
}
