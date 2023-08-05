package com.hoangdp.todo.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {    
    private final String JWT_SECRET_KEY = "xitC0ritK1D3vxitC0ritK1D3vxitC0ritK1D3vxitC0ritK1D3v";
    private final Long EXPIRY_PERIOD = 6000000L;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails user){
        return generateToken(new HashMap<>(), user);
    }

    public boolean isTokenValid(String token, UserDetails user){
        final String userName = extractUsername(token);
        return userName.equals(user.getUsername()) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {        
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails user) {
        return Jwts.builder().setClaims(extraClaims).setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY_PERIOD))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

}
