package com.juyoung.restapiwithspring.init;

import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountService;
import com.juyoung.restapiwithspring.accounts.RoleType;
import com.juyoung.restapiwithspring.configs.AppSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DefaultUsersInitializer implements ApplicationRunner {

    @Autowired
    AccountService accountService;
    @Autowired
    AppSecurityProperties appSecurityProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Set<RoleType> roles = Arrays.stream(RoleType.values()).collect(Collectors.toSet());

        Account admin = Account.builder()
                .email(appSecurityProperties.getAdminUsername())
                .password(appSecurityProperties.getAdminPassword())
                .roles(roles)
                .build();

        Account user = Account.builder()
                .email(appSecurityProperties.getUserUsername())
                .password(appSecurityProperties.getUserPassword())
                .roles(new HashSet<>(Arrays.asList(RoleType.USER)))
                .build();

        accountService.saveAccount(admin);
        accountService.saveAccount(user);
    }

}
