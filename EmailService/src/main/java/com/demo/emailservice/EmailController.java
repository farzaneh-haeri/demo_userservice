package com.demo.emailservice;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/email")
public class EmailController {
    private SimpleEmailService simpleEmailService;

    @PostMapping("/simple/send")
    public void sendTextEmail(@Valid @RequestBody EmailDto emailDto) {
        simpleEmailService.sendMail(emailDto);
    }

}
