package com.app.demo.security;

import com.app.core.security.Principal;
import com.app.core.security.ResourceOwnerService;
import com.app.demo.entity.User;
import com.app.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 15:27
 * @description:当前登录人实现类
 */
@Service
@RequiredArgsConstructor
public class ResourceOwnerServiceImpl implements ResourceOwnerService {

    private final UserService userService;

    @Override
    public Principal loadResourceOwner(String userName) {
        User user = userService.lambdaQuery().eq(User::getUsername, userName).one();
        List<String> permissionList = this.getPermissionList(user.getId());
        List<String> roleList = this.getRoleList(user.getId());
        Principal principal = new Principal();
        principal.setIdentity(user.getId());
        principal.setPrimaryKey(user.getId());
        principal.setName(user.getUsername());
        principal.setPermissionList(permissionList);
        principal.setRoleList(roleList);
        return principal;
    }

    @Override
    public List<String> getPermissionList(Serializable identity) {
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<String>();
        list.add("101");
        list.add("user.add");
        list.add("user.update");
        list.add("user.get");
        // list.add("user.delete");
        list.add("art.*");
        return list;
    }

    @Override
    public List<String> getRoleList(Serializable identity) {
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        List<String> list = new ArrayList<String>();
        list.add("admin");
        list.add("super-admin");
        return list;
    }
}
