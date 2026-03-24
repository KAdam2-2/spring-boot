package com.amigoscode.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //Asks which company issued this (e.g., "google", "github", "azure")
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = null;

        //Extract the email based on whom the provider is
        if ("azure".equalsIgnoreCase(provider) || "microsoft".equalsIgnoreCase(provider)) {
            // Microsoft usually puts the email in "mail"
            email = oAuth2User.getAttribute("mail");

            // If "mail" is empty, fallback to their Microsoft login name
            if (email == null) {
                email = oAuth2User.getAttribute("userPrincipalName");
            }
        } else {
            // The standard way for Google, GitHub, etc.
            email = oAuth2User.getAttribute("email");
        }
        // --- YOUR CUSTOM LOGIC GOES HERE ---
        // Example: If email is missing, throw an error
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // Example: Check database and save if new...
        // userRepository.findByEmail(email)...

        return oAuth2User;
    }
}
