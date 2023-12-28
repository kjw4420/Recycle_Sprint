package com.zolup5.service.Impl;

import com.zolup5.domain.role.Role;
import com.zolup5.domain.user.Member;
import com.zolup5.DTO.UserFormDTO;
import com.zolup5.repository.UserRepository;
import com.zolup5.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }
    public Long join(UserFormDTO userFormDTO) {
        if (userFormDTO.getPoint() == null) {
            userFormDTO.setPoint("0");
        }
        userFormDTO.setPassword(passwordEncoder.encode(userFormDTO.getPassword()));

        Member user = Member.builder()
                .username(userFormDTO.getUsername())
                .email(userFormDTO.getEmail())
                .password(userFormDTO.getPassword())
                .area(userFormDTO.getArea())
                .point(userFormDTO.getPoint())
                .role(Role.USER)
                .build();

        return userRepository.save(user).getPid();
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void updateUserArea(String username, String newArea) {
        userRepository.updateAreaByUsername(username, newArea);
    }

    public boolean changePassword(String username, String currentPassword, String newPassword, String confirmPassword){
        Optional<Member> optionalMember = userRepository.findByUsername(username);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            // 현재 비밀번호가 올바른지 확인
            if (passwordEncoder.matches(currentPassword, member.getPassword())) {
                // 새로운 비밀번호와 비밀번호 확인이 일치하는지 확인
                if (newPassword.equals(confirmPassword)) {
                    String encodedNewPassword = passwordEncoder.encode(newPassword);
                    member.setPassword(encodedNewPassword);
                    userRepository.save(member);
                    return true; // 변경 성공
                }
            }
        }
        return false; // 변경 실패
    }

    public void emailchangePassword(String username, String temporaryPassword){
        Optional<Member> optionalMember = userRepository.findByUsername(username);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.setPassword(passwordEncoder.encode(temporaryPassword));
            userRepository.save(member);
        }

    }

    @Override
    public void updateUserPoint(String username, String score) {
        userRepository.updatePointByUsername(username, score);
    }


}