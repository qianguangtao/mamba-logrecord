package com.app.demo.mapper;

import com.app.demo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {
    User selectByCellphone(@Param("cellphone") String cellphone);
}
