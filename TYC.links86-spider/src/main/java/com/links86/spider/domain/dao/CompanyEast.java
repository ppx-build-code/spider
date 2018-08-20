package com.links86.spider.domain.dao;

import com.links86.spider.domain.constant.CompanyStatusEnum;
import com.links86.spider.domain.constant.ReqUrlEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity(name = "company_east")
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {
            "id"
    })
})
public class CompanyEast {
    @Id
    private String id;
    private String name;
    private String contact;
    private String capitalInvested;
    private String address;
    private String email;
    private String website;
    private Integer status;
    private String logo;
    private String city;
    private String capital;
    private String industry;
    private String legalPerson;
    private String startTime;
    private String businessScope;
    private String size;
    private String brief;
    private String registerNumber;
    private String orgCode;
    private String creditCode;
    private String taxCode;
    private String type;
    private String approveDate;
    private String registerAuthority;
    private String englishName;
    private String terms;
    private Date createTime;
    private Date updateTime;
    @Column(length = 1)
    @ColumnDefault(value = "3")
    private Integer flag;

    public CompanyEast upd(ReqUrlEnum reqUrlEnum) {
        if (reqUrlEnum.getNum() == 1) {
            flag = 2;
        } else if (reqUrlEnum.getNum() == 2) {
            flag = 1;
        }
        return this;
    }
}
