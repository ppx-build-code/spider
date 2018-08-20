package com.links86.spider.scheduled;

import com.links86.spider.domain.constant.ReqUrlEnum;
import com.links86.spider.domain.dao.CompanyDO;
import com.links86.spider.domain.dao.CompanyEast;
import com.links86.spider.service.CompaniesService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dyu
 * @date 2018/08/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    @NonNull
    private CompaniesService companiesService;

    @Scheduled(fixedRate = 30000)
    public void writeDataFromTyc() {
        log.debug("begin get data ...");
        // 从数据库中获取需完善信息的企业列表

        List<CompanyEast> ts = companiesService.listsByEast(3, 5);

        // 爬取数据填充列表
        if (ts == null || ts.size() == 0) {
            return;
        }

        List<CompanyDO> companyDOs = ts.parallelStream().map(t -> companiesService.getTyc(t.getId(), t.getName())).collect(Collectors.toList());


        log.debug(companyDOs.toString());

        // 入库
        companiesService.save(ts.parallelStream().map(t -> t.upd(ReqUrlEnum.TYC)).collect(Collectors.toList()));
        companiesService.saveNew(companyDOs);
    }

//    @Scheduled(fixedRate = 100000)

    public void writeDataFromQcc() {
        List<CompanyDO> ts = companiesService.listsByNew(2, 5);

        if (ts == null || ts.size() == 0) {
            return;
        }

        List<CompanyDO> companyDOs = ts.parallelStream().map(t -> companiesService.getQcc(t)).collect(Collectors.toList());

        companiesService.saveNew(companyDOs);

    }
}
