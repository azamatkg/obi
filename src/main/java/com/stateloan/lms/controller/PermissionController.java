package com.stateloan.lms.controller;

import com.stateloan.lms.dto.MessageResponse;
import com.stateloan.lms.entity.Permission;
import com.stateloan.lms.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permission Management", description = "Permission management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all permissions")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.findAll();
        return ResponseEntity.ok(permissions);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get permission by ID")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
        try {
            Permission permission = permissionService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
            return ResponseEntity.ok(permission);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/resources")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all unique resources")
    public ResponseEntity<List<String>> getAllResources() {
        List<String> resources = permissionService.getAllResources();
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/actions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all unique actions")
    public ResponseEntity<List<String>> getAllActions() {
        List<String> actions = permissionService.getAllActions();
        return ResponseEntity.ok(actions);
    }
    
    @GetMapping("/by-resource/{resource}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get permissions by resource")
    public ResponseEntity<List<Permission>> getPermissionsByResource(@PathVariable String resource) {
        List<Permission> permissions = permissionService.findByResource(resource);
        return ResponseEntity.ok(permissions);
    }
    
    @GetMapping("/by-action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get permissions by action")
    public ResponseEntity<List<Permission>> getPermissionsByAction(@PathVariable String action) {
        List<Permission> permissions = permissionService.findByAction(action);
        return ResponseEntity.ok(permissions);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new permission")
    public ResponseEntity<?> createPermission(@Valid @RequestBody Permission permission) {
        try {
            Permission createdPermission = permissionService.createPermission(permission);
            return ResponseEntity.ok(createdPermission);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update permission")
    public ResponseEntity<?> updatePermission(@PathVariable Long id, @Valid @RequestBody Permission permissionRequest) {
        try {
            Permission updatedPermission = permissionService.updatePermission(id, permissionRequest);
            return ResponseEntity.ok(updatedPermission);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete permission")
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.ok(new MessageResponse("Permission deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}