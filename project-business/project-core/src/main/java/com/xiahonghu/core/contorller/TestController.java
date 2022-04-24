package com.xiahonghu.core.contorller;


import com.xiahonghu.core.utils.aspect.PlainRes;
import com.xiahonghu.core.utils.aspect.SysLog;
import com.xiahonghu.core.utils.exception.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@PlainRes
public class TestController {


    @SysLog("进入test")
    @GetMapping(value = "/test")
    public R list()
    {

        return R.ok("2121212");
    }
}
