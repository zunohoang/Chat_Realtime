package org.example.chatwebsocket.repositorys;

import org.example.chatwebsocket.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status WHERE u.username = :username")
    void setStatus(String username, String status);

    @Query("SELECT u.username FROM User u WHERE u.status = 'ON'")
    List<String> getOnlineUsers();
}
