package org.booking.hotelbooking.Config;

import jakarta.annotation.PostConstruct;
import org.booking.hotelbooking.Entity.Role;
import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Repository.UserRepository;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class SystemUserInitializer {

    private final UserRepository userRepository;

    @Autowired
    public SystemUserInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        if(userRepository.findByUsername("system-bot").isEmpty()){
            User user = new User();
            user.setUsername("system-bot");
            user.setEmail("system-bot@gmail.com");
            user.setPassword("password");
            user.setRoles(Collections.singleton(Role.ROLE_ADMIN));
            userRepository.save(user);

        }
    }

    @PostConstruct
    public void initAdmin() {
        if(userRepository.findByUsername("system-admin").isEmpty()){
            User user = new User();
            user.setUsername("system-admin");
            user.setEmail("system-admin@gmail.com");
            user.setPassword("password");
            user.setRoles(Collections.singleton(Role.ROLE_ADMIN));
            userRepository.save(user);
        }
    }
}
