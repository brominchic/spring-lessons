package com.example.spring.component;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NumGetterPrototype {
    int num;

    public NumGetterPrototype() {
        this.num = 0;
        log.info("start");
    }

    public int getNum() {
        num++;
        return num + 1;
    }
}
