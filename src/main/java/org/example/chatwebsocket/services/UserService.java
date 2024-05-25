package org.example.chatwebsocket.services;

import lombok.AllArgsConstructor;
import org.example.chatwebsocket.models.User;
import org.example.chatwebsocket.repositorys.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public User loginWithUsernameAndPassword(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if(Objects.equals(user.getPassword(), password)) {
            return user;
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
    public void setStatus(String username, String status) {
        userRepository.setStatus(username, status);
    }

    public List<String> getOnlineUsers() {
        return userRepository.getOnlineUsers();
    }
}
