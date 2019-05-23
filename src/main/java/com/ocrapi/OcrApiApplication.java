package com.ocrapi;

import com.ocrapi.model.SystemUser;
import com.ocrapi.repository.SystemUserRepository;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootApplication
public class OcrApiApplication implements CommandLineRunner {

    @Autowired
    SystemUserRepository systemUserRepository;

    public static void main(String[] args) {
        SpringApplication.run(OcrApiApplication.class, args);
    }


    @Override
    public void run(String... arg0) throws Exception {
//        Ocr.setUp();
        SystemUser systemUser = null;
        Optional<SystemUser> optional = systemUserRepository.findById("cc7cd37c-b7b2-4ac7-a1b1");
        if (!optional.isPresent()) {
            if (systemUser == null) {
                systemUser = new SystemUser();
                systemUser.setId("cc7cd37c-b7b2-4ac7-a1b1");
                systemUser.setUsername("root");
                systemUser.setPassword("password");
                systemUser.setEnabled(true);
                systemUser.setRole("root");
                systemUser.setCreatedAt(LocalDateTime.now());
                systemUser.setUpdatedAt(LocalDateTime.now());
                systemUserRepository.save(systemUser);
            }
        } else {
            systemUser = optional.get();
            if (systemUser != null) {
                System.out.println(systemUser.getUsername());
            }
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TomcatServletWebServerFactory tomcatEmbedded() {

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
                //-1 means unlimited
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            }
        });

        return tomcat;

    }
}
