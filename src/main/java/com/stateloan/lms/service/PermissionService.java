package com.stateloan.lms.service;

import com.stateloan.lms.entity.Permission;
import com.stateloan.lms.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PermissionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }
    
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }
    
    public Optional<Permission> findByName(String name) {
        return permissionRepository.findByName(name);
    }
    
    public List<Permission> findByResource(String resource) {
        return permissionRepository.findByResource(resource + ":");
    }
    
    public List<Permission> findByAction(String action) {
        return permissionRepository.findByAction(":" + action);
    }
    
    public Set<Permission> findByNames(Set<String> names) {
        return permissionRepository.findByNameIn(names);
    }
    
    public Permission createPermission(Permission permission) {
        if (permissionRepository.existsByName(permission.getName())) {
            throw new RuntimeException("Permission name is already taken!");
        }
        
        validatePermissionFormat(permission.getName());
        
        logger.info("Creating permission: {}", permission.getName());
        return permissionRepository.save(permission);
    }
    
    public Permission updatePermission(Long id, Permission permissionDetails) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        
        if (!permission.getName().equals(permissionDetails.getName()) && 
            permissionRepository.existsByName(permissionDetails.getName())) {
            throw new RuntimeException("Permission name is already taken!");
        }
        
        validatePermissionFormat(permissionDetails.getName());
        
        permission.setName(permissionDetails.getName());
        permission.setDescription(permissionDetails.getDescription());
        
        logger.info("Updating permission: {}", permission.getName());
        return permissionRepository.save(permission);
    }
    
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        
        logger.info("Deleting permission: {}", permission.getName());
        permissionRepository.delete(permission);
    }
    
    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
    
    private void validatePermissionFormat(String permissionName) {
        if (!permissionName.matches("^[A-Z_]+:[A-Z_]+$")) {
            throw new RuntimeException("Permission name must be in format RESOURCE:ACTION (e.g., LOAN:CREATE)");
        }
    }
    
    public List<String> getAllResources() {
        return permissionRepository.findAll().stream()
                .map(Permission::getResource)
                .distinct()
                .toList();
    }
    
    public List<String> getAllActions() {
        return permissionRepository.findAll().stream()
                .map(Permission::getAction)
                .distinct()
                .toList();
    }
}