package org.booking.hotelbooking.Service;

import org.booking.hotelbooking.Entity.Role;
import org.booking.hotelbooking.Entity.RoleRequest;
import org.booking.hotelbooking.Entity.User;
import org.booking.hotelbooking.Repository.RoleRequestRepository;
import org.booking.hotelbooking.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRequestRepository roleRequestRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRequestRepository roleRequestRepository) {
        this.userRepository = userRepository;
        this.roleRequestRepository = roleRequestRepository;
    }

    public User registerNewUser(User user) {

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword("{noop}" + user.getPassword());
        newUser.setPhone(user.getPhone());
        newUser.setRoles(Collections.singleton(Role.ROLE_USER));

        return userRepository.save(newUser);
    }

    public User loginUser(String email, String password) {

       User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Wrong password");
        }
        return user;
    }

    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found"));
    }

    public User getUserWithBookings(Long userId) {
        return userRepository.findByIdWithBookings(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void requestManagerRole(Long userId){
        User user = getUserById(userId);
        RoleRequest request = new RoleRequest();
        request.setUser(user);
        request.setRequestRole(Role.ROLE_MANAGER);
        request.setRequestDate(LocalDateTime.now());
        roleRequestRepository.save(request);
    }

    public void approveRequest(Long requestId){
        RoleRequest request = roleRequestRepository.findById(requestId)
                .orElseThrow(()-> new RuntimeException("Request not found"));
        User user = request.getUser();
        user.getRoles().add(Role.ROLE_MANAGER);
        userRepository.save(user);
        request.setApproved(true);
        roleRequestRepository.save(request);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
    }


    public void grantAdminRole(Long userId) {
        User user = getUserById(userId);
        user.getRoles().add(Role.ROLE_ADMIN);
        userRepository.save(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
