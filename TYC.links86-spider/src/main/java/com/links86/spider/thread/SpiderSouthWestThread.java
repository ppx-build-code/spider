package com.links86.spider.thread;

import com.links86.spider.domain.constant.QueueEnum;
import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanySouthWestDO;
import com.links86.spider.domain.dao.CompanySouthWestTempDO;
import com.links86.spider.manager.CompanyDataManager;
import com.links86.spider.service.CompaniesService;
import com.links86.spider.service.CompanySouthWestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

@Slf4j
@AllArgsConstructor
public class SpiderSouthWestThread implements Runnable{
    private final CompanyDataManager companyDataManager;
    private final CompaniesService companiesService;
    private final CompanySouthWestService companySouthWestService;

    @Override
    public void run() {
        while (true) {
            CompanySouthWestDO companySouthWestDO = companyDataManager.getOne(QueueEnum.SW);

            if (companySouthWestDO == null) {
                try {
                    Thread.sleep((long) (Math.random() * 2000));
                    continue;
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                    continue;
                }
            }

            CompanyDO companyDO = companiesService.getQxb(companySouthWestDO.getId(), companySouthWestDO.getName());

            int flag = 2;
            if (companyDO == null || StringUtils.isBlank(companyDO.getAddress())) {
                flag = 1;
            } else {
                CompanySouthWestTempDO companySouthWestTempDO = new CompanySouthWestTempDO();
                BeanUtils.copyProperties(companyDO, companySouthWestTempDO);
                companySouthWestService.addSWT(companySouthWestTempDO);
            }
            companySouthWestService.upd(companySouthWestDO, flag);
        }
    }
}
