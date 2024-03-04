package com.app.demo.service;

import com.app.demo.entity.User;
import com.app.demo.pojo.dto.UserDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/5 11:11
 * @description: 用户service
 */
public interface UserService extends IService<User> {
    User insert(UserDto userDto);

    void delete(Long id);

    void delete(List<Long> idList);

    User edit(UserDto userDto);

    User getById(Long id);

    Page<User> pageList(User user, Long pageSize, Long pageNumber);
}
