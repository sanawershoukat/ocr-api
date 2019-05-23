package com.ocrapi.controller;

import com.ocrapi.common.DeviceProvider;
import com.ocrapi.model.SystemUser;
import com.ocrapi.security.TokenHelper;
import com.ocrapi.service.impl.CustomUserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    TokenHelper tokenHelper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    @Autowired
    private DeviceProvider deviceProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> generateAuthenticationToken(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletResponse response
    ) throws AuthenticationException, IOException {
        // Perform the security
        SystemUser user = null;
        String username;
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );
            // Inject into security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
            username = loggedInUser.getName();
//            user = (SystemUser) authentication.getPrincipal();
            // token creation


        } catch (BadCredentialsException e) {
            log.error(e.getMessage());
            Map<String, String> result = new HashMap<>();
            result.put("result", "Invalid Username or Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }

        Device device = null;
        String jws = tokenHelper.generateToken(username, device);
        long expiresIn = tokenHelper.getExpiredIn(device);
        // Return the token

//        if (device.isMobile()) {
//            userService.updateMobileLogin(user);
//        }

        String[] roles = {"ROLE_USER"};


        return ResponseEntity.ok(new UserTokenState(jws, expiresIn, roles, username));
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthenticationRequest {

        private String username;
        private String password;

    }

    @Data
    public static class UserTokenState {
        private String access_token;
        private long expires_in;
        String[] roles;
        String userId;

        public UserTokenState() {
        }

        public UserTokenState(String access_token, long expires_in, String[] roles, String userId) {
            this.access_token = access_token;
            this.expires_in = expires_in;
            this.roles = roles;
            this.userId = userId;

        }

        public UserTokenState(String access_token, long expires_in) {
            this.access_token = access_token;
            this.expires_in = expires_in;
        }
    }
}
