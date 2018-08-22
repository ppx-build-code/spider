package com.links86.spider.domain.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author dyu
 * @date 2018/08/22
 */
@Data
@Entity(name = "company_ty_url")
@NoArgsConstructor
public class CompanyTyDO implements Serializable {
    @Id
    private Integer id;
    private String comName;
    private String tyUrl;
    private String comId;
    private Integer flag;
}
