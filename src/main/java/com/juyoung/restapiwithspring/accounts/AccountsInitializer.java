package com.juyoung.restapiwithspring.accounts;

import com.juyoung.restapiwithspring.accounts.password.Password;
import com.juyoung.restapiwithspring.configs.AppSecurityProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Slf4j
@Component
@AllArgsConstructor
public class AccountsInitializer implements ApplicationRunner {

    final AccountService accountService;

    final AppSecurityProperties appSecurityProperties;

    @Override
    public void run(ApplicationArguments args) {
        Account admin = Account.builder()
                .email(appSecurityProperties.getAdminUsername())
                .password(Password.of(appSecurityProperties.getAdminPassword()))
                .roles(new HashSet<>(Collections.singletonList(RoleType.ADMIN)))
                .build();

        Account user = Account.builder()
                .email(appSecurityProperties.getUserUsername())
                .password(Password.of(appSecurityProperties.getUserPassword()))
                .roles(new HashSet<>(Collections.singletonList(RoleType.USER)))
                .build();

        accountService.signIn(admin);
        log.debug("Register ADMIN : {}", admin);

        accountService.signIn(user);
        log.debug("Register USER : {}", user);
    }
}
