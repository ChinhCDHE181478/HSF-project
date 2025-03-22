package com.hsf302.jpa.supermarket;

import com.hsf302.jpa.supermarket.model.Account;
import com.hsf302.jpa.supermarket.model.Role;
import com.hsf302.jpa.supermarket.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@SpringBootApplication
@EnableAsync
public class SupermarketApplication implements CommandLineRunner {
    @Autowired
    private AccountService accountService;

    public static void main(String[] args) {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        try {
            ObjectName mbeanName = new ObjectName("org.springframework.boot:type=Admin,name=SpringApplication");
            if (mbs.isRegistered(mbeanName)) {
                mbs.unregisterMBean(mbeanName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SpringApplication.run(SupermarketApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Account admin = accountService.getAccount("admin@gmail.com");
        if(admin != null) {
            return;
        }
        admin = new Account();
        admin.setFullname("User");
        admin.setEmail("admin@gmail.com");
        admin.setPhone("0312345689");
        admin.setPassword("admin");
        admin.setRole(Role.ROLE_ADMIN);
        accountService.register(admin);
    }

}
