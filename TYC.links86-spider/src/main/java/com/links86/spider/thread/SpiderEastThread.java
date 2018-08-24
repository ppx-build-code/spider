package com.links86.spider.thread;

import com.links86.spider.domain.constant.QueueEnum;
import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanyEast;
import com.links86.spider.domain.dao.CompanyTyDO;
import com.links86.spider.manager.CompanyDataManager;
import com.links86.spider.service.CompaniesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Slf4j
public class SpiderEastThread implements Runnable{

    private final CompanyDataManager companyDataManager;
    private final CompaniesService companiesService;

    @Override
    public void run() {
        while(true){

            CompanyEast companyEast = companyDataManager.getOne(QueueEnum.EAST);
            if (companyEast == null) {
                try {
                    Thread.sleep((long) (Math.random() * 2000));
                    continue;
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
            CompanyDO companyDO = companiesService.getQxb(companyEast.getId(), companyEast.getName());

            int flag = 2;
            if (StringUtils.isBlank(companyDO.getAddress()) && StringUtils.isBlank(companyDO.getScore())) {
                flag = 1;
            }
            companyEast.setFlag(flag);
            companiesService.save(Stream.of(companyEast).collect(Collectors.toList()));
            companiesService.saveNew(Stream.of(companyDO).collect(Collectors.toList()));

        }
    }
}
