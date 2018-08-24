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
public class CompanyEast {
    @Id
    private String id;
    private String name;
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
