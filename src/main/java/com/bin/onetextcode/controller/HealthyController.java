package com.bin.onetextcode.controller;

import com.bin.onetextcode.common.BaseResponse;
import com.bin.onetextcode.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthy")
public class HealthyController {

    @GetMapping("/")
    public BaseResponse<String> healthy() {
        return ResultUtils.success("healthy");
    }
}
