package com.ocrapi.repository;

import com.ocrapi.model.SystemUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemUserRepository extends MongoRepository<SystemUser, String> {

    SystemUser findByUsername(String username);
}
