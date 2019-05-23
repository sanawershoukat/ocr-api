package com.ocrapi.controller;

import com.ocrapi.model.SystemUser;
import com.ocrapi.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class SystemUserController {

    @Autowired
    SystemUserService systemUserService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<SystemUser> getUser(@PathVariable String id) {
        Map<String, String> result = new HashMap<>();
        SystemUser systemUser = null;
        try {
            systemUser = systemUserService.findById(id);
            result.put("result", "success");
        } catch (Exception ex) {
            result.put("result", "failed");
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<SystemUser>(systemUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/al", method = RequestMethod.GET)
    public List<SystemUser> getAllUsers() {
        List<SystemUser> list = systemUserService.findAllSystemUsers();
        System.out.println("Get All users");
        return list;
    }
}
