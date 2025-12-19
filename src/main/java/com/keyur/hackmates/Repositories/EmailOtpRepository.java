package com.keyur.hackmates.Repositories;

import com.keyur.hackmates.Entities.EmailOtp;
import com.keyur.hackmates.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findByUser(User user);
    Optional<EmailOtp> findByUser_IdAndIsVerified(int userId, boolean isVerified);
}
