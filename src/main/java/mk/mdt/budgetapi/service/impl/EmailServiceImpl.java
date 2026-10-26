package mk.mdt.budgetapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import mk.mdt.budgetapi.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String link = frontendUrl + "/reset-password?token=" + token;
        // TODO BAPP-19: zameni so vistinsko isprakjanje na email
        log.info("Password reset link for {}: {}", to, link);
    }
}