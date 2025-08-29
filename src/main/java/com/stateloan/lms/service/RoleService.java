package com.stateloan.lms.service;

import com.stateloan.lms.entity.Permission;
import com.stateloan.lms.entity.Role;
import com.stateloan.lms.repository.PermissionRepository;
import com.stateloan.lms.repository.RoleRepository;
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
public class RoleService {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
    
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }
    
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
    
    public Optional<Role> findByIdWithPermissions(Long id) {
        return roleRepository.findByIdWithPermissions(id);
    }
    
    public Optional<Role> findByNameWithPermissions(String name) {
        return roleRepository.findByNameWithPermissions(name);
    }
    
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role name is already taken!");
        }
        
        logger.info("Creating role: {}", role.getName());
        return roleRepository.save(role);
    }
    
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        if (!role.getName().equals(roleDetails.getName()) && 
            roleRepository.existsByName(roleDetails.getName())) {
            throw new RuntimeException("Role name is already taken!");
        }
        
        role.setName(roleDetails.getName());
        role.setDescription(roleDetails.getDescription());
        
        logger.info("Updating role: {}", role.getName());
        return roleRepository.save(role);
    }
    
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        logger.info("Deleting role: {}", role.getName());
        roleRepository.delete(role);
    }
    
    public Role assignPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        Set<Permission> permissions = permissionRepository.findByIdIn(permissionIds);
        
        if (permissions.size() != permissionIds.size()) {
            throw new RuntimeException("Some permissions were not found");
        }
        
        role.getPermissions().clear();
        permissions.forEach(role::addPermission);
        
        logger.info("Assigning permissions {} to role: {}", permissionIds, role.getName());
        return roleRepository.save(role);
    }
    
    public Role addPermissionToRole(Long roleId, Long permissionId) {
        logger.debug("Attempting to add permission {} to role {}", permissionId, roleId);
        
        Optional<Role> roleOpt = roleRepository.findByIdWithPermissions(roleId);
        if (!roleOpt.isPresent()) {
            logger.error("Role not found with id: {}", roleId);
            throw new RuntimeException("Role not found with id: " + roleId);
        }
        Role role = roleOpt.get();
        
        Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
        if (!permissionOpt.isPresent()) {
            logger.error("Permission not found with id: {}", permissionId);
            throw new RuntimeException("Permission not found with id: " + permissionId);
        }
        Permission permission = permissionOpt.get();
        
        role.addPermission(permission);
        
        logger.info("Adding permission {} to role: {}", permission.getName(), role.getName());
        return roleRepository.save(role);
    }
    
    public Role removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));
        
        role.removePermission(permission);
        
        logger.info("Removing permission {} from role: {}", permission.getName(), role.getName());
        return roleRepository.save(role);
    }
    
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}