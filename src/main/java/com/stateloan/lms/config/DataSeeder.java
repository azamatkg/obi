package com.stateloan.lms.config;

import com.stateloan.lms.entity.Permission;
import com.stateloan.lms.entity.Role;
import com.stateloan.lms.entity.User;
import com.stateloan.lms.service.PermissionService;
import com.stateloan.lms.service.RoleService;
import com.stateloan.lms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            seedPermissions();
            seedRoles();
            seedUsers();

            logger.info("Seeding initial data completed successfully");
        } catch (Exception e) {
            logger.error("Error during data seeding: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void seedPermissions() {
        logger.info("Seeding permissions...");
        
        List<String> permissionNames = Arrays.asList(
            // User management permissions
            "USER:CREATE", "USER:READ", "USER:UPDATE", "USER:DELETE",
            // Role management permissions
            "ROLE:CREATE", "ROLE:READ", "ROLE:UPDATE", "ROLE:DELETE",
            // Permission management permissions
            "PERMISSION:CREATE", "PERMISSION:READ", "PERMISSION:UPDATE", "PERMISSION:DELETE",
            // Loan management permissions (for future development)
            "LOAN:CREATE", "LOAN:READ", "LOAN:UPDATE", "LOAN:DELETE",
            "LOAN:APPROVE", "LOAN:REJECT", "LOAN:PROCESS",
            // Application management permissions
            "APPLICATION:CREATE", "APPLICATION:READ", "APPLICATION:UPDATE", "APPLICATION:DELETE",
            "APPLICATION:SUBMIT", "APPLICATION:REVIEW", "APPLICATION:APPROVE",
            // Report permissions
            "REPORT:READ", "REPORT:GENERATE", "REPORT:EXPORT",
            // System administration permissions
            "SYSTEM:ADMIN", "SYSTEM:CONFIG", "SYSTEM:AUDIT"
        );
        
        for (String permissionName : permissionNames) {
            if (!permissionService.existsByName(permissionName)) {
                Permission permission = new Permission();
                permission.setName(permissionName);
                permission.setDescription("Permission to " + getPermissionDescription(permissionName));
                permissionService.createPermission(permission);
                logger.info("Created permission: {}", permissionName);
            }
        }
    }
    
    private void seedRoles() {
        logger.info("Seeding roles...");
        
        // Create ADMIN role
        if (!roleService.existsByName("ADMIN")) {
            Role adminRole = new Role("ADMIN", "System Administrator with full access");
            adminRole = roleService.createRole(adminRole);
            // Refresh the role to ensure we have the correct ID
            adminRole = roleService.findByName("ADMIN").orElse(adminRole);
            
            // Assign all permissions to ADMIN
            List<Permission> allPermissions = permissionService.findAll();
            for (Permission permission : allPermissions) {
                roleService.addPermissionToRole(adminRole.getId(), permission.getId());
            }
            logger.info("Created ADMIN role with all permissions");
        }
        
        // Create USER role
        if (!roleService.existsByName("USER")) {
            Role userRole = new Role("USER", "Regular user with limited access");
            userRole = roleService.createRole(userRole);
            // Refresh the role to ensure we have the correct ID
            userRole = roleService.findByName("USER").orElse(userRole);
            
            // Assign basic permissions to USER
            List<String> userPermissions = Arrays.asList(
                "USER:READ", "APPLICATION:CREATE", "APPLICATION:READ", 
                "APPLICATION:UPDATE", "LOAN:READ"
            );
            
            for (String permissionName : userPermissions) {
                Permission permission = permissionService.findByName(permissionName).orElse(null);
                if (permission != null) {
                    roleService.addPermissionToRole(userRole.getId(), permission.getId());
                }
            }
            logger.info("Created USER role with basic permissions");
        }
        
        // Create LOAN_OFFICER role
        if (!roleService.existsByName("LOAN_OFFICER")) {
            Role loanOfficerRole = new Role("LOAN_OFFICER", "Loan Officer with loan processing capabilities");
            loanOfficerRole = roleService.createRole(loanOfficerRole);
            // Refresh the role to ensure we have the correct ID
            loanOfficerRole = roleService.findByName("LOAN_OFFICER").orElse(loanOfficerRole);
            
            // Assign loan processing permissions
            List<String> loanOfficerPermissions = Arrays.asList(
                "USER:READ", "LOAN:CREATE", "LOAN:READ", "LOAN:UPDATE", "LOAN:PROCESS",
                "APPLICATION:READ", "APPLICATION:UPDATE", "APPLICATION:REVIEW",
                "REPORT:READ", "REPORT:GENERATE"
            );
            
            for (String permissionName : loanOfficerPermissions) {
                Permission permission = permissionService.findByName(permissionName).orElse(null);
                if (permission != null) {
                    roleService.addPermissionToRole(loanOfficerRole.getId(), permission.getId());
                }
            }
            logger.info("Created LOAN_OFFICER role with loan processing permissions");
        }
        
        // Create MANAGER role
        if (!roleService.existsByName("MANAGER")) {
            Role managerRole = new Role("MANAGER", "Manager with approval and oversight capabilities");
            managerRole = roleService.createRole(managerRole);
            // Refresh the role to ensure we have the correct ID
            managerRole = roleService.findByName("MANAGER").orElse(managerRole);
            
            // Assign management permissions
            List<String> managerPermissions = Arrays.asList(
                "USER:READ", "LOAN:READ", "LOAN:APPROVE", "LOAN:REJECT",
                "APPLICATION:READ", "APPLICATION:APPROVE", "REPORT:READ",
                "REPORT:GENERATE", "REPORT:EXPORT"
            );
            
            for (String permissionName : managerPermissions) {
                Permission permission = permissionService.findByName(permissionName).orElse(null);
                if (permission != null) {
                    roleService.addPermissionToRole(managerRole.getId(), permission.getId());
                }
            }
            logger.info("Created MANAGER role with management permissions");
        }
    }
    
    private void seedUsers() {
        logger.info("Seeding users...");
        
        // Create default admin user
        if (!userService.existsByUsername("admin")) {
            User adminUser = new User("admin", "admin@stateloan.com", "admin123");
            adminUser = userService.createUser(adminUser);
            
            // Assign ADMIN role
            Role adminRole = roleService.findByName("ADMIN").orElse(null);
            if (adminRole != null) {
                userService.addRoleToUser(adminUser.getId(), adminRole.getId());
            }
            logger.info("Created default admin user");
        }
        
        // Create sample loan officer user
        if (!userService.existsByUsername("loanofficer")) {
            User loanOfficerUser = new User("loanofficer", "officer@stateloan.com", "officer123");
            loanOfficerUser = userService.createUser(loanOfficerUser);
            
            // Assign LOAN_OFFICER role
            Role loanOfficerRole = roleService.findByName("LOAN_OFFICER").orElse(null);
            if (loanOfficerRole != null) {
                userService.addRoleToUser(loanOfficerUser.getId(), loanOfficerRole.getId());
            }
            logger.info("Created sample loan officer user");
        }
        
        // Create sample manager user
        if (!userService.existsByUsername("manager")) {
            User managerUser = new User("manager", "manager@stateloan.com", "manager123");
            managerUser = userService.createUser(managerUser);
            
            // Assign MANAGER role
            Role managerRole = roleService.findByName("MANAGER").orElse(null);
            if (managerRole != null) {
                userService.addRoleToUser(managerUser.getId(), managerRole.getId());
            }
            logger.info("Created sample manager user");
        }
    }
    
    private String getPermissionDescription(String permissionName) {
        String[] parts = permissionName.split(":");
        String resource = parts[0].toLowerCase();
        String action = parts[1].toLowerCase();
        
        return switch (action) {
            case "create" -> "create " + resource + " records";
            case "read" -> "read " + resource + " records";
            case "update" -> "update " + resource + " records";
            case "delete" -> "delete " + resource + " records";
            case "approve" -> "approve " + resource + " records";
            case "reject" -> "reject " + resource + " records";
            case "process" -> "process " + resource + " records";
            case "submit" -> "submit " + resource + " records";
            case "review" -> "review " + resource + " records";
            case "generate" -> "generate " + resource + " records";
            case "export" -> "export " + resource + " records";
            case "admin" -> "administer " + resource;
            case "config" -> "configure " + resource;
            case "audit" -> "audit " + resource;
            default -> "perform " + action + " action on " + resource;
        };
    }
}