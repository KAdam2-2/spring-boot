package com.amigoscode.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");

        // Manual Map: Find the user in YOUR database by email
        User dbUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new OAuth2AuthenticationException("Email not found in system_usr table"));

        // Map the DB role to a Spring Security Authority
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("SCOPE_" + dbUser.getRole()));

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
    }
}
