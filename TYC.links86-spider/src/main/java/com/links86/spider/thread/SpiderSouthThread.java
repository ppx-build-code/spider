package com.links86.spider.thread;

import com.links86.spider.domain.constant.QueueEnum;
import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanySouthDO;
import com.links86.spider.domain.dao.CompanySouthTempDO;
import com.links86.spider.manager.CompanyDataManager;
import com.links86.spider.service.CompaniesService;
import com.links86.spider.service.CompanySouthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

@Slf4j
@AllArgsConstructor
public class SpiderSouthThread implements Runnable{

    private final CompanyDataManager companyDataManager;
    private final CompaniesService companiesService;
    private final CompanySouthService companySouthService;

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

            CompanyDO companyDO = companiesService.getTyc(companySouthDO.getId(), companySouthDO.getName());

            int flag = 2;
            if (companyDO == null || StringUtils.isBlank(companyDO.getAddress())) {
                flag = 1;
            } else {
                CompanySouthTempDO companySouthTempDO = new CompanySouthTempDO();
                BeanUtils.copyProperties(companyDO, companySouthTempDO);
                companySouthService.addST(companySouthTempDO);
            }
            companySouthService.upd(companySouthDO, flag);
        }
    }
}
