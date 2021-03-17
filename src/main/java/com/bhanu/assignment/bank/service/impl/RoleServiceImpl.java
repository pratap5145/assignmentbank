package com.bhanu.assignment.bank.service.impl;

import com.bhanu.assignment.bank.dao.RoleDao;
import com.bhanu.assignment.bank.model.Role;
import com.bhanu.assignment.bank.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public Role findByName(String name) {
        Role role = roleDao.findRoleByName(name);
        return role;
    }
}
