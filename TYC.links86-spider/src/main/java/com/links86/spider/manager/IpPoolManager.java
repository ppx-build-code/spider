package com.links86.spider.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class IpPoolManager {

    private static final String IP_POOL_URL = "http://dev.kdlapi.com/api/getproxy/?orderid=983447234578095&num=500&b_pcchrome=1&b_pcie=1&b_pcff=1&protocol=1&method=2&an_an=1&an_ha=1&sp1=1&sp2=1&sep=1";
    public static final String TYC_IP_POOL_KEY = "tyc_spider_key";
    public static final String QCC_IP_POOL_KEY = "qcc_spider_key";

    @Resource
    private RedisTemplate redisTemplate;

    private void addIps(String name) {

        ResponseEntity<String> result = new RestTemplate().getForEntity(IP_POOL_URL, String.class);

        if (result.getStatusCode() == HttpStatus.OK) {
            List<String> ips = Optional.ofNullable(result.getBody())
                    .map(body -> Stream.of(body.split("\\n")).filter(i -> !i.equals("null")).collect(Collectors.toList()))
                    .orElse(null);
            redisTemplate.opsForList().leftPushAll(name, ips);
        } else {
            addIps(name);
        }
    }

    private String getOne(String name) {
        Long count = redisTemplate.opsForList().size(name);
        if (count == 0) {
            addIps(name);
            return getOne(name);
        }
        log.debug((String) redisTemplate.opsForList().index(name, count - 1));
        return (String) redisTemplate.opsForList().index(name, count - 1);
    }

    public boolean delOne(String name) {
        redisTemplate.opsForList().rightPop(name);
        return false;
    }

    public Object [] getIpAndPort(String name) {
        String hostAndPort = getOne(name);
        return new Object [] {hostAndPort.split(":")[0], Integer.valueOf(hostAndPort.split(":")[1].trim())};
    }
}
