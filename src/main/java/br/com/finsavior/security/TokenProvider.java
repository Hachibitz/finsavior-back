package br.com.finsavior.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${app.jwtExpirationMsForRememberMe}")
    private Long jwtExpirationMsForRememberMe;
    
    @Autowired
    UserSecurityDetails userSecurityDetails;

    public String generateToken(Authentication authentication, boolean rememberMe) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userSecurityDetails.loadUserByUsername(authentication.getPrincipal().toString());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        if (rememberMe) {
            expiryDate = new Date(now.getTime() + jwtExpirationMsForRememberMe);
        }

        return Jwts.builder()
        		.claim("username", customUserDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("username", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}