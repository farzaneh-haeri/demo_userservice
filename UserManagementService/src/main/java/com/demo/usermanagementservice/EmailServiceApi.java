package com.demo.usermanagementservice;

import com.demo.usermanagementservice.dto.EmailDto;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="EmailService")
public interface EmailServiceApi {
    @PostMapping("/api/email/simple/send")
    public void sendTextEmail(@RequestBody EmailDto emailDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);
}
