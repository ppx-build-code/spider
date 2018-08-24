package com.links86.spider.thread;

import com.links86.spider.domain.constant.QueueEnum;
import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanyTyDO;
import com.links86.spider.manager.CompanyDataManager;
import com.links86.spider.service.CompaniesService;
import com.links86.spider.service.CompanySouthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Slf4j
public class SpiderTyThread implements Runnable{

    private final CompanyDataManager companyDataManager;
    private final CompaniesService companiesService;

    @Override
    public void run() {
        while(true){

            CompanyTyDO tyDO = companyDataManager.getOne(QueueEnum.TY);
            if (tyDO == null) {
                try {
                    Thread.sleep((long) (Math.random() * 2000));
                    continue;
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
            CompanyDO companyDO = companiesService.getTycDirectly(tyDO.getId().toString(), tyDO.getComName(), tyDO.getTyUrl().replace("www", "m"));
            if (StringUtils.isBlank(companyDO.getAddress()) && StringUtils.isBlank(companyDO.getScore())) {
                continue;
            }
            companiesService.updTy(tyDO);
            companiesService.saveNew(Stream.of(companyDO).collect(Collectors.toList()));

        }
    }
}
