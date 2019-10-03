package com.juyoung.restapiwithspring.accounts;

import com.juyoung.restapiwithspring.accounts.password.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("계정 등록 성공")
    @Test
    void findByUsername_success() {
        String username = "juyoung@email.com";
        String pass = "pass1";

        Account account = Account.builder()
                .email(username)
                .password(Password.of(pass))
                .roles(Arrays.stream(RoleType.values()).collect(Collectors.toSet()))
                .build();

        accountService.signIn(account);

        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertThat(passwordEncoder.matches(pass, userDetails.getPassword())).isTrue();
    }

    @DisplayName("없는 계정일 경우 exception")
    @Test
    void findByUserName_whenNotExistUser_thenException() {
        String username = "nonEmail@email.com";

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> accountService.loadUserByUsername(username));
    }
}
