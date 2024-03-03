package com.app.core.security;

import java.io.Serializable;
import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 13:35
 * @description: 同spring security的UserDetailsService
 */
public interface ResourceOwnerService {
    /**
     * 根据id查询数据库里用户
     * @param userName
     * @return
     */
    Principal loadResourceOwner(String userName);
    /**
     * 根据id查询数据库里用户权限
     * @param identity
     * @return 权限code集合
     */
    List<String> getPermissionList(Serializable identity);
    /**
     * 根据id查询数据库里用户角色
     * @param identity
     * @return 角色code集合
     */
    List<String> getRoleList(Serializable identity);
}
