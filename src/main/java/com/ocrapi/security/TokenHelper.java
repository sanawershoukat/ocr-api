package com.ocrapi.security;

import com.ocrapi.model.SystemUser;
import com.ocrapi.util.TimeProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenHelper {

    static Logger log = LoggerFactory.getLogger(TokenHelper.class);

    @Value("${app.name}")
    String APP_NAME;

    @Value("${jwt.secret}")
    public String SECRET;

    @Value("${jwt.expires_in}")
    long EXPIRES_IN;

    @Value("${jwt.mobile_expires_in}")
    long MOBILE_EXPIRES_IN;

    @Value("${jwt.header}")
    String AUTH_HEADER;

    static final String AUDIENCE_UNKNOWN = "unknown";
    static final String AUDIENCE_WEB = "web";
    static final String AUDIENCE_MOBILE = "mobile";
    static final String AUDIENCE_TABLET = "tablet";

    @Autowired
    TimeProvider timeProvider;

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            audience = claims.getAudience();
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    public String refreshToken(String token, Device device) {
        String refreshedToken;
        Date a = timeProvider.now();
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            claims.setIssuedAt(a);
            refreshedToken = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(generateExpirationDate(device))
                    .signWith(SIGNATURE_ALGORITHM, SECRET)
                    .compact();
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public String generateToken(String username, Device device) {
        String audience = generateAudience(device);
        return Jwts.builder()
                .setIssuer(APP_NAME)
                .setId(username)
                .setSubject(username)
                .setAudience(audience)
                .setIssuedAt(timeProvider.now())
                .setExpiration(generateExpirationDate(device))
                .signWith(SIGNATURE_ALGORITHM, SECRET)
                .compact();
    }

    private String generateAudience(Device device) {
        String audience = AUDIENCE_WEB;
        if (device != null) {
            if (device.isNormal()) {
                audience = AUDIENCE_WEB;
            } else if (device.isTablet()) {
                audience = AUDIENCE_TABLET;
            } else if (device.isMobile()) {
                audience = AUDIENCE_MOBILE;
            }
        }
        return audience;
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Date generateExpirationDate(Device device) {
        long expiresIn = device != null && (device.isTablet() || device.isMobile()) ? MOBILE_EXPIRES_IN : EXPIRES_IN;

        if (expiresIn == 0) {
            return null;
        }
        return new Date(timeProvider.now().getTime() + expiresIn * 1000);
    }

    public long getExpiredIn(Device device) {
        return device != null && (device.isMobile() || device.isTablet()) ? MOBILE_EXPIRES_IN : EXPIRES_IN;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        SystemUser user = (SystemUser) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        return (
                username != null &&
                        username.equals(userDetails.getUsername()) &&
                        !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())
        );
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    public String getToken(HttpServletRequest request) {
        /**
         *  Getting the token from Authentication header
         *  e.g Bearer your_token
         */
        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public String getUserIdFromToken(HttpServletRequest request) {
        String userId = "";

        try {
            String token = this.getToken(request);
            final Claims claims = this.getAllClaimsFromToken(token);
            userId = claims.getId();
        } catch (Exception e) {
//            userId = getTenantIdFromURL(request);
        }
        log.debug("Resolved User: " + userId);
        return userId;
    }


    public String getTenantIdFromURL(HttpServletRequest request) {
        String tenantId = null;
        try {

            String[] uriElements = request.getRequestURI().split("/");
            if ("newLeads".equals(uriElements[uriElements.length - 2])) {
                tenantId = uriElements[uriElements.length - 1];
            }

            if ("smsReply".equals(uriElements[uriElements.length - 2])) {
                tenantId = uriElements[uriElements.length - 1];
            }

            if ("tracking_image".equals(uriElements[uriElements.length - 2])) {
                tenantId = uriElements[uriElements.length - 1];
            }

            if ("status".equals(uriElements[uriElements.length - 2])) {
                tenantId = uriElements[uriElements.length - 1];
            }

            if ("unsubscribe".equals(uriElements[uriElements.length - 2])) {
                tenantId = uriElements[uriElements.length - 1];
            }
            //TODO what if invalid tenant set??

        } catch (Exception e) {
            tenantId = null;
        }

        return tenantId;
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

}
