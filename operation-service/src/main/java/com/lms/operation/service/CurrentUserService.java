package com.lms.operation.service;

import com.lms.operation.domain.Employee;
import com.lms.operation.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Who is calling: the user id from the JWT (set as the authentication details by the JWT filter),
 * whether they are an admin, and their employee record. Used to scope lists to an officer's own work.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final EmployeeRepository employeeRepository;

    public Integer userId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token
                && token.getDetails() instanceof Integer userId) {
            return userId;
        }
        return null;
    }

    public boolean isAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    public Optional<Employee> employee() {
        Integer userId = userId();
        return userId == null ? Optional.empty() : employeeRepository.findByUserId(userId);
    }
}
