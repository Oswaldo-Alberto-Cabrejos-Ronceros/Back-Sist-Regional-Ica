package com.clinicaregional.clinica.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    //obtenemos la clsve secrete de aplication properties
    @Value("${jwt.secret}")
    private String secretKey;
    //generamos la clave para firmar
    //declaramos key
    private Key key;
    //inicializamos key

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${jwt.refresh}")
    private int refreshExpirationMs;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    //para el token de acceso
    public String generateAccessToken(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return Jwts.builder().setSubject(email).setIssuedAt(new Date()).claim("authorities", authorities).setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)).signWith(key).compact();
    }

    //para generar el token de refresco
    public String generateRefreshToken(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return Jwts.builder().setSubject(email).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs)).signWith(key).compact();
    }


    //para validar token
    public boolean validateToken(String token) {
        try {
            //para verificar, si falla el token es invalido
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            //para verificar si el token expiro
            boolean isExpired = claims.getExpiration().before(new Date());
            String emailToken = claims.getSubject();

            //no tiene que estar espirado y el email debe ser valido
            return !isExpired && emailToken != null && !emailToken.trim().isEmpty();
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //para obtener el email del token
    public String getEmailFromJwt(String token) {
        return getClaimsIfValid(token).getSubject();
    }

    //para obetener las autoridades del token
    public List<GrantedAuthority> getAuthoritiesFromJwt(String token) {
        String authorities = getClaimsIfValid(token).get("authorities", String.class);
        return List.of(authorities.split(",")).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    //para verificar si el token esta expirado

    public boolean isTokenExpired(String token) {
        try {
            //obtenemos la fecha de expiracion del token
            Date expirateDate = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
            return expirateDate.before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    //para obtener los claims si es validad
    private Claims getClaimsIfValid(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            if (claims.getExpiration().before(new Date())) {
                throw new JwtException("Token expired");
            }
            return claims;
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage());
        }
    }


}
