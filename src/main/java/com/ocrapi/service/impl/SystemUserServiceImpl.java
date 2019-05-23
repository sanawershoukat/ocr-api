package com.ocrapi.service.impl;

import com.ocrapi.model.SystemUser;
import com.ocrapi.repository.SystemUserRepository;
import com.ocrapi.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SystemUserServiceImpl implements SystemUserService {

    @Autowired
    SystemUserRepository repository;

    @Override
    public SystemUser findById(String id) {
        return repository.findById(id).get();
    }

    @Override
    public SystemUser findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public List<SystemUser> findAllSystemUsers() {
        return repository.findAll();
    }
}
