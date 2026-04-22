package com.foodorder.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.foodorder.entity.User;
import com.foodorder.exception.BusinessException;
import com.foodorder.repository.UserRepository;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User requireCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("Unauthenticated request");
        }

        return userRepository.findByUserId(authentication.getName())
            .orElseThrow(() -> new BusinessException("User not found"));
    }
}
