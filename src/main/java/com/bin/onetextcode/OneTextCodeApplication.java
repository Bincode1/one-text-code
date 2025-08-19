package com.bin.onetextcode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.bin.onetextcode.mapper")
@SpringBootApplication
public class OneTextCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneTextCodeApplication.class, args);
    }

}
