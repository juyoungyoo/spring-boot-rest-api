package com.juyoung.restapiwithspring.init;

import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.accounts.Password;
import com.juyoung.restapiwithspring.accounts.RoleType;
import com.juyoung.restapiwithspring.configs.AppSecurityProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultUsersInitializer implements ApplicationRunner {

    final AccountService accountService;
    final AppSecurityProperties appSecurityProperties;

    @Override
    public void run(ApplicationArguments args) {
        Account admin = Account.builder()
                .email(appSecurityProperties.getAdminUsername())
                .password(appSecurityProperties.getAdminPassword())
                .roles(new HashSet<>(Collections.singletonList(RoleType.ADMIN)))
                .build();

        Account user = Account.builder()
                .email(appSecurityProperties.getUserUsername())
                .password(appSecurityProperties.getUserPassword())
                .roles(new HashSet<>(Collections.singletonList(RoleType.USER)))
                .build();

        accountService.signIn(admin);
        log.debug("Register ADMIN : {}", admin);

        accountService.signIn(user);
        log.debug("Register USER : {}", user);
    }
}
