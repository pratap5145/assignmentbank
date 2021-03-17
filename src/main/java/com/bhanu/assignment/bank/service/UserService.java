package com.bhanu.assignment.bank.service;

import com.bhanu.assignment.bank.model.User;
import com.bhanu.assignment.bank.model.UserDto;

import java.util.List;

public interface UserService {
    User save(UserDto user);
    List<User> findAll();
    User findOne(String username);
}
