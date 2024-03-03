package com.app.demo.controller;

import com.app.core.mvc.result.Result;
import com.app.demo.entity.User;
import com.app.demo.pojo.dto.UserDto;
import com.app.demo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote
 */
@Api(tags = "用户管理")
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @ApiOperation(value = "新增用户")
    @PostMapping()
    public Result<User> save(@RequestBody UserDto userDto) {
        return Result.success(service.insert(userDto));
    }

    @ApiOperation(value = "编辑用户")
    @PutMapping()
    public Result<User> edit(@RequestBody UserDto userDto) {
        return Result.success(service.edit(userDto));
    }
    
    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.success();
    }

    @ApiOperation(value = "批量删除用户")
    @DeleteMapping("/list/{ids}")
    public Result<Void> deleteList(@PathVariable List<Long> ids) {
        service.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "查询单个用户")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = service.getById(id);
        log.info("查询用户成功：{}", user);
        return Result.success(user);
    }

    @ApiOperation(value = "用户分页查询")
    @PostMapping("/page/{pageSize}/{pageNumber}")
    public Result<List<User>> pageList(@RequestBody User user, @PathVariable Long pageSize, @PathVariable Long pageNumber) {
        return Result.success(service.pageList(user, pageSize, pageNumber));
    }
}
