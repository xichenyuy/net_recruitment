package org.netmen.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.netmen.common.response.Result;
import org.netmen.dao.po.College;
import org.netmen.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/college")
@Validated
@Tag(name = "college")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;


    @PostMapping()
    // @Operation(summary = "用户注册", description = "传入账号密码注册账号")
    public Result add(String name){
        College college = new College();
        college.setName(name);
        collegeService.save(college);
        return Result.success("学院新增成功");
    }
}
