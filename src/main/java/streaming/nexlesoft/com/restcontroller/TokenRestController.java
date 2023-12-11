package streaming.nexlesoft.com.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import streaming.nexlesoft.com.service.TokenService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/token")
public class TokenRestController {

    @Autowired
    private TokenService tokenService;

    /**
     * Handles the POST request for refreshing the access token using a refresh token.
     *
     * @param request The request body containing the refresh token.
     * @return ResponseEntity<Map<String, String>> containing the new access and refresh tokens.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            // Validate if refreshToken exists
            if (!tokenService.isRefreshTokenValid(request.get("refreshToken"))) {
                // If refresh token is not valid, return a 404 Not Found response with an error message.
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Refresh token not found"));
            }

            // Refresh the token
            Map<String, String> refreshedTokens = (Map<String, String>) tokenService.refreshToken(request.get("refreshToken"));

            // Return the response with the new tokens
            return ResponseEntity.status(HttpStatus.OK).body(refreshedTokens);

        } catch (Exception e) {
            // If an exception occurs during token refresh, return a 500 Internal Server Error response with an error message.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Internal error"));
        }
    }
}
