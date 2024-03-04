package com.app.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.app.demo.entity.User;
import com.app.demo.mapper.UserMapper;
import com.app.demo.pojo.dto.UserDto;
import com.app.demo.service.UserService;
import com.app.logrecord.annotation.LogRecord;
import com.app.logrecord.annotation.LogRecordModel;
import com.app.logrecord.enums.LogOperate;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @LogRecord(key = "#result.id",
            desc = "新增用户",
            operateType = LogOperate.Type.SAVE,
            oldObjClass = User.class)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public User insert(@LogRecordModel("userDto") UserDto userDto) {
        User user = BeanUtil.toBean(userDto, User.class);
        user.setAddress(JSON.toJSONString(userDto.getAddress()));
        this.save(user);
        return user;
    }

    @LogRecord(key = "#userDto.id",
            desc = "更新用户",
            operateType = LogOperate.Type.UPDATE,
            method = "@userMapper.selectById(#root)",
            oldObjClass = User.class)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public User edit(@LogRecordModel("userDto") UserDto userDto) {
        User user = BeanUtil.toBean(userDto, User.class);
        user.setAddress(JSON.toJSONString(userDto.getAddress()));
        this.updateById(user);
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long id) {
        this.removeById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Long> idList) {
        for (Long id : idList) {
            this.delete(id);
        }
    }

    @Override
    public User getById(Long id) {
        return this.lambdaQuery().eq(User::getId, id).one();
    }

    @Override
    public Page<User> pageList(User user, Long pageSize, Long pageNumber) {
        Page<User> page = new Page<User>(pageNumber, pageSize);
        return this.lambdaQuery().eq(StrUtil.isNotEmpty(user.getUsername()), User::getUsername, user.getUsername()).page(page);
    }

}
