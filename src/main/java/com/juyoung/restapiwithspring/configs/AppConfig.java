package com.juyoung.restapiwithspring.configs;

import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountRepository;
import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.accounts.RoleType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;
            @Override
            public void run(ApplicationArguments args) throws Exception {
                Set<RoleType> roles = Arrays.stream(RoleType.values()).collect(Collectors.toSet());

                Account admin = Account.builder()
                        .email("admin@email.com")
                        .password("admin")
                        .roles(roles)
                        .build();

                Account user = Account.builder()
                        .email("user@email.com")
                        .password("user")
                        .roles(roles)
                        .build();

                accountService.saveAccount(admin);
                accountService.saveAccount(user);
            }
        };
    }
}
