package streaming.nexlesoft.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import streaming.nexlesoft.com.entity.Token;
import streaming.nexlesoft.com.entity.User;
import streaming.nexlesoft.com.repository.TokenRepository;

import java.util.*;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public Map<String, Object> generateTokens(User user) {
        // Generate tokens
        String token = generateJwtToken(user);
        String refreshToken = generateRefreshToken(user);

        // Save refresh token to the database
        saveRefreshToken(user, refreshToken);

        // Create response object
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("token", token);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    public ResponseEntity<Map<String, String>> refreshToken(String refreshToken) {
        // Validate refresh token
        Optional<Token> optionalToken = tokenRepository.findByRefreshToken(refreshToken);
        if (optionalToken.isPresent()) {
            Token existingToken = optionalToken.get();

            // Invalidate old refresh token
            invalidateRefreshToken(existingToken);

            // Generate new tokens
            User user = existingToken.getUser();
            Map<String, Object> newTokens = generateTokens(user);

            // Create response object
            Map<String, String> response = new HashMap<>();
            response.put("token", (String) newTokens.get("token"));
            response.put("refreshToken", (String) newTokens.get("refreshToken"));

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Refresh token not found"));
    }

    public void invalidateRefreshTokens(User user) {
        // Remove all refresh tokens for the user
        tokenRepository.deleteByUser(user);
    }

    private String generateJwtToken(User user) {
        // Implement JWT token generation logic
        return "jwt_token";
    }

    private String generateRefreshToken(User user) {
        return UUID.randomUUID().toString();
    }

    private void saveRefreshToken(User user, String refreshToken) {
        // Save refresh token to the database
        Token token = new Token();
        token.setUser(user);
        token.setRefreshToken(refreshToken);
        token.setExpiresIn("30 days");
        tokenRepository.save(token);
    }

    private void invalidateRefreshToken(Token token) {
        // Remove refresh token from the database
        tokenRepository.delete(token);
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        // Validate if the refreshToken exists in the database
        return tokenRepository.findByRefreshToken(refreshToken).isPresent();
    }

}
