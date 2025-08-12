package com.bin.onetextcode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bin.onetextcode.mapper")
public class OneTextCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneTextCodeApplication.class, args);
    }

}
