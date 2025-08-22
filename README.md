# State Loan Management System

A comprehensive Spring Boot application for managing state loans with role-based authentication and authorization.

## Features

- **JWT Authentication**: Secure token-based authentication
- **Role-Based Authorization**: Dynamic roles and permissions stored in database
- **User Management**: Complete CRUD operations for users
- **Role Management**: Dynamic role creation and permission assignment
- **Permission Management**: Granular permissions with RESOURCE:ACTION format
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Data Seeding**: Automatic creation of initial roles, permissions, and users
- **Global Exception Handling**: Comprehensive error handling and validation
- **PostgreSQL Integration**: Robust database with BIGSERIAL primary keys

## Technology Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.3.4** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL 16** - Database
- **JWT (JJWT)** - Token-based authentication
- **Swagger/OpenAPI** - API documentation
- **Maven** - Build tool

## Project Structure

```
src/
├── main/
│   ├── java/com/stateloan/lms/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Exception handling
│   │   ├── repository/      # Data repositories
│   │   ├── security/        # Security configuration and JWT utilities
│   │   └── service/         # Business logic services
│   └── resources/
│       └── application.yml  # Application configuration
```

## Database Schema

### Core Entities

1. **users** - User accounts with authentication details
2. **roles** - Dynamic roles (ADMIN, USER, LOAN_OFFICER, MANAGER)
3. **permissions** - Granular permissions (LOAN:CREATE, USER:READ, etc.)
4. **user_roles** - Many-to-many relationship between users and roles
5. **role_permissions** - Many-to-many relationship between roles and permissions

## Default Users

The application seeds the following default users:

| Username | Password | Role | Email |
|----------|----------|------|-------|
| admin | admin123 | ADMIN | admin@stateloan.com |
| loanofficer | officer123 | LOAN_OFFICER | officer@stateloan.com |
| manager | manager123 | MANAGER | manager@stateloan.com |

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user info

### User Management (Admin only)
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `POST /api/users/{userId}/roles` - Assign roles to user
- `POST /api/users/{userId}/roles/{roleId}` - Add role to user
- `DELETE /api/users/{userId}/roles/{roleId}` - Remove role from user

### Role Management (Admin only)
- `GET /api/roles` - Get all roles
- `GET /api/roles/{id}` - Get role by ID
- `GET /api/roles/{id}/permissions` - Get role with permissions
- `POST /api/roles` - Create new role
- `PUT /api/roles/{id}` - Update role
- `DELETE /api/roles/{id}` - Delete role
- `POST /api/roles/{roleId}/permissions` - Assign permissions to role
- `POST /api/roles/{roleId}/permissions/{permissionId}` - Add permission to role
- `DELETE /api/roles/{roleId}/permissions/{permissionId}` - Remove permission from role

### Permission Management (Admin only)
- `GET /api/permissions` - Get all permissions
- `GET /api/permissions/{id}` - Get permission by ID
- `GET /api/permissions/resources` - Get all unique resources
- `GET /api/permissions/actions` - Get all unique actions
- `GET /api/permissions/by-resource/{resource}` - Get permissions by resource
- `GET /api/permissions/by-action/{action}` - Get permissions by action
- `POST /api/permissions` - Create new permission
- `PUT /api/permissions/{id}` - Update permission
- `DELETE /api/permissions/{id}` - Delete permission

### Health Check
- `GET /api/health` - Application health status

## Getting Started

### Prerequisites
- Java 21
- Maven 3.6+
- PostgreSQL 16

### Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE obi;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE obi TO postgres;
```

2. Update `application.yml` with your database credentials if different.

### Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### API Documentation

Access the interactive API documentation at:
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/api-docs`

### Testing the API

1. **Login** to get JWT token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

2. **Use the token** in subsequent requests:
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Default Roles and Permissions

### ADMIN
- Full system access with all permissions

### LOAN_OFFICER
- `USER:READ`, `LOAN:CREATE`, `LOAN:READ`, `LOAN:UPDATE`, `LOAN:PROCESS`
- `APPLICATION:READ`, `APPLICATION:UPDATE`, `APPLICATION:REVIEW`
- `REPORT:READ`, `REPORT:GENERATE`

### MANAGER
- `USER:READ`, `LOAN:READ`, `LOAN:APPROVE`, `LOAN:REJECT`
- `APPLICATION:READ`, `APPLICATION:APPROVE`
- `REPORT:READ`, `REPORT:GENERATE`, `REPORT:EXPORT`

### USER
- `USER:READ`, `APPLICATION:CREATE`, `APPLICATION:READ`, `APPLICATION:UPDATE`, `LOAN:READ`

## Security Features

- **JWT Token Authentication** with configurable expiration
- **BCrypt Password Encoding** for secure password storage
- **Method-Level Security** with `@PreAuthorize` annotations
- **CORS Configuration** for cross-origin requests
- **Global Exception Handling** with proper HTTP status codes
- **Input Validation** with Bean Validation annotations

## Future Enhancements

This system provides a solid foundation for a loan management system. Future enhancements could include:

- Loan application entities and workflows
- Document management system
- Audit logging for compliance
- Email notifications
- Advanced reporting features
- Dashboard and analytics
- File upload/download capabilities
- Integration with external credit agencies

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.