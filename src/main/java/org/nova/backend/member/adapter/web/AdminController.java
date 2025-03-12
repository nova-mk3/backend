package org.nova.backend.member.adapter.web;

import org.nova.backend.shared.model.ApiResponse;
import org.nova.backend.shared.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @PostMapping("/api/v1/admin")
    public ApiResponse<String> admin() {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = SecurityUtils.getRole(authentication);

        return ApiResponse.success(name + role);
    }
}
