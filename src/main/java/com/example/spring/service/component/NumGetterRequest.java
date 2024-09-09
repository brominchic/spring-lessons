package com.example.spring.service.component;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
@Slf4j
public class NumGetterRequest {
    int num;

    public NumGetterRequest() {
        this.num = 0;
        log.info("start");
    }

    public int getNum() {
        num++;
        return num + 1;
    }
}
