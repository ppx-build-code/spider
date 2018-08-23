package com.links86.spider.domain.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author dyu
 * @date 2018/08/23
 */
@Data
@Entity(name = "company_south_west")
@NoArgsConstructor
public class CompanySouthWestDO {
    @Id
    private String id;
    private String name;
    private Integer flag;
}
