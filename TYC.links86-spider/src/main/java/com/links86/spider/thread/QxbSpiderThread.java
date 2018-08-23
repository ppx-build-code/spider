package com.links86.spider.thread;

import com.links86.spider.domain.constant.QueueEnum;
import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanySouthDO;
import com.links86.spider.domain.dao.CompanyTyDO;
import com.links86.spider.manager.CompanyDataManager;
import com.links86.spider.service.CompaniesService;
import com.links86.spider.service.CompanySouthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dyu
 * @date 2018/08/22
 */
@Slf4j
@AllArgsConstructor
public class QxbSpiderThread implements Runnable {
    private final CompanyDataManager companyDataManager;
    private final CompanySouthService companySouthService;
    private final CompaniesService companiesService;

    @Override
    public void run() {
        while (true) {
            CompanySouthDO companySouthDO = companyDataManager.getOne(QueueEnum.SOUTH);

            if (companySouthDO == null) {
                try {
                    Thread.sleep((long) (Math.random() * 2000));
                    continue;
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                    continue;
                }
            }

            CompanyDO companyDO = companiesService.getQxb(companySouthDO.getId(), companySouthDO.getName());

            companySouthService.upd(companySouthDO);
            companiesService.saveNew(Stream.of(companyDO).collect(Collectors.toList()));
        }
    }
}
