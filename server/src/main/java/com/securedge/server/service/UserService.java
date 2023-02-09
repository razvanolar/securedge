package com.securedge.server.service;

import com.securedge.server.model.User;

import java.util.List;

public interface UserService extends BaseService<User> {

    List<User> findAll(String sortOption);
}
