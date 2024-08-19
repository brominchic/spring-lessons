package com.example.spring.component;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class NumGetterPrototype {
    int num = 1;

    public int getNum() {
        num++;
        return num + 1;
    }
}
