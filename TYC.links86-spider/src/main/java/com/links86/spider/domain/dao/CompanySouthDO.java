package com.links86.spider.domain.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author dyu
 * @date 2018/08/22
 */
@Data
@Entity(name = "company_south")
@NoArgsConstructor
public class CompanySouthDO {
    @Id
    private String id;
    private String name;
    private Integer flag;
}
