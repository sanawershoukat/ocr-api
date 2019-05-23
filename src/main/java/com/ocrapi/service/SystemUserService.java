package com.ocrapi.service;

import com.ocrapi.model.SystemUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SystemUserService {

    SystemUser findById(String id);

    SystemUser findByUsername(String username);

    List<SystemUser> findAllSystemUsers();
}
