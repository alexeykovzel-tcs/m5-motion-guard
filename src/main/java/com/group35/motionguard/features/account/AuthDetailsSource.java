package com.group35.motionguard.features.account;

import lombok.Getter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new AuthDetails(context);
    }

    @Getter
    static class AuthDetails extends WebAuthenticationDetails {
        private final String verificationCode;

        public AuthDetails(HttpServletRequest request, String verificationCode) {
            super(request);
            this.verificationCode = verificationCode;
        }

        public AuthDetails(HttpServletRequest request) {
            super(request);
            verificationCode = request.getParameter("code");
        }
    }
}