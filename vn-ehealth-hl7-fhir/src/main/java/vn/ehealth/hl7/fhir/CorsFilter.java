package vn.ehealth.hl7.fhir;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
       // log.info(req.getContentType());
       // log.info(req.getRemoteAddr());
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HISTORY");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "X-FHIR-Starter,authorization,Prefer,Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers");
       // incomingRequestPostProcessed(request, response);
        if (!"OPTIONS".equals(request.getMethod())) {
            chain.doFilter(req, res);
        }
        
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
//    public boolean incomingRequestPostProcessed(HttpServletRequest theRequest, HttpServletResponse theResponse) {
//        
//        authenticate(theRequest);
//        
//        return true;
//    }
//    public void authenticate(HttpServletRequest theRequest) throws AuthenticationException {
//        String token = theRequest.getHeader("Authorization");
//        if (token == null) {
//            throw new AuthenticationException("Not authorized (no authorization header found in request)");
//        }
//        if (!token.startsWith(Constants.HEADER_AUTHORIZATION_VALPREFIX_BEARER)) {
//            throw new AuthenticationException("Not authorized (authorization header does not contain a bearer token)");
//        }

//        token = token.substring(Constants.HEADER_AUTHORIZATION_VALPREFIX_BEARER.length());
//
//        SignedJWT idToken;
//        try {
//            idToken = SignedJWT.parse(token);
//        } catch (ParseException e) {
//            throw new AuthenticationException("Not authorized (bearer token could not be validated)", e);
//        }
//
//        // validate our ID Token over a number of tests
//        ReadOnlyJWTClaimsSet idClaims;
//        try {
//            idClaims = idToken.getJWTClaimsSet();
//        } catch (ParseException e) {
//            throw new AuthenticationException("Not authorized (bearer token could not be validated)", e);
//        }
//
//        String issuer = idClaims.getIssuer();
//
//        ServerConfiguration serverConfig = myServerConfigurationService.getServerConfiguration(issuer);
//        if (serverConfig == null) {
//            ourLog.error("No server configuration found for issuer: " + issuer);
//            throw new AuthenticationException("Not authorized (no server configuration found for issuer " + issuer + ")");
//        }
//
//        RegisteredClient clientConfig = myClientConfigurationService.getClientConfiguration(serverConfig);
//        if (clientConfig == null) {
//            ourLog.error("No client configuration found for issuer: " + issuer);
//            throw new AuthenticationException("Not authorized (no client configuration found for issuer " + issuer + ")");
//        }
//
//        // check the signature
//        JwtSigningAndValidationService jwtValidator = null;
//
//        JWSAlgorithm alg = idToken.getHeader().getAlgorithm();
//        if (alg.equals(JWSAlgorithm.HS256) || alg.equals(JWSAlgorithm.HS384) || alg.equals(JWSAlgorithm.HS512)) {
//
//            // generate one based on client secret
//            jwtValidator = mySymmetricCacheService.getSymmetricValidtor(clientConfig.getClient());
//        } else {
//            // otherwise load from the server's public key
//            jwtValidator = myValidationServices.getValidator(serverConfig.getJwksUri());
//        }
//
//        if (jwtValidator != null) {
//            if (!jwtValidator.validateSignature(idToken)) {
//                throw new AuthenticationException("Not authorized (signature validation failed)");
//            }
//        } else {
//            ourLog.error("No validation service found. Skipping signature validation");
//            throw new AuthenticationException("Not authorized (can't determine signature validator)");
//        }
//
//        // check expiration
//        if (idClaims.getExpirationTime() == null) {
//            throw new AuthenticationException("Id Token does not have required expiration claim");
//        } else {
//            // it's not null, see if it's expired
//            Date minAllowableExpirationTime = new Date(System.currentTimeMillis() - (myTimeSkewAllowance * 1000L));
//            Date expirationTime = idClaims.getExpirationTime();
//            if (!expirationTime.after(minAllowableExpirationTime)) {
//                throw new AuthenticationException("Id Token is expired: " + idClaims.getExpirationTime());
//            }
//        }
//
//        // check not before
//        if (idClaims.getNotBeforeTime() != null) {
//            Date now = new Date(System.currentTimeMillis() + (myTimeSkewAllowance * 1000));
//            if (now.before(idClaims.getNotBeforeTime())) {
//                throw new AuthenticationException("Id Token not valid untill: " + idClaims.getNotBeforeTime());
//            }
//        }
//
//        // check issued at
//        if (idClaims.getIssueTime() == null) {
//            throw new AuthenticationException("Id Token does not have required issued-at claim");
//        } else {
//            // since it's not null, see if it was issued in the future
//            Date now = new Date(System.currentTimeMillis() + (myTimeSkewAllowance * 1000));
//            if (now.before(idClaims.getIssueTime())) {
//                throw new AuthenticationException("Id Token was issued in the future: " + idClaims.getIssueTime());
//            }
//        }

//    }
}
