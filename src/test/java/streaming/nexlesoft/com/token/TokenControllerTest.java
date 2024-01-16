package streaming.nexlesoft.com.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import streaming.nexlesoft.com.restcontroller.TokenRestController;
import streaming.nexlesoft.com.service.TokenService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TokenControllerTest {

    // Mock the TokenService dependency
    @Mock
    private TokenService tokenService;

    // Inject mocks into the TokenRestController
    @InjectMocks
    private TokenRestController tokenController;

    // Initialize the mocks before each test
    public TokenControllerTest() {
        MockitoAnnotations.initMocks(this);
    }

    // Test for the refreshToken method with valid input, expecting success
    @Test
    void refreshToken_ValidInput_Success() {
        // Mock input data
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", "validRefreshToken");

        // Mock service response
        Map<String, String> refreshedTokens = new HashMap<>();
        refreshedTokens.put("token", "newJwtToken");
        refreshedTokens.put("refreshToken", "newRefreshToken");

        // Mock service re-response
        refreshedTokens.put("re-token", "re-newJwtToken");
        refreshedTokens.put("tokenCore-refreshToken", "tokenCore-reNewRefrshToken");

        // Mock service legalon-tech and sonar-link/legalforce-loc-app
        refreshedTokens.put("legalon_technologies", "LOCD11992");
        refreshedTokens.put("sonar-link", "legalforce/loc-app");

        // Meck breakcrumb-token service (https://github.com/nvngon26400/ggtstore/issues/2)
        refreshedTokens.put("breakcrumb", "token");
        refreshedTokens.put("re-breakcrumb", "re-token");

        // Mock the behavior of the tokenService.refreshToken method
        when(tokenService.refreshToken(eq("validRefreshToken"))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(refreshedTokens)
        );

        // Perform the test by calling the refreshToken method
        ResponseEntity<Map<String, String>> response = tokenController.refreshToken(request);

        // Verify the result of the test
        // Assert that the HTTP status code in the response is OK (200)
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        // Assert that the response body matches the expected refreshedTokens map
        Assertions.assertEquals(refreshedTokens, response.getBody());

        // Verify that the tokenService.refreshToken method was called exactly once
        // with the expected refresh token parameter
        verify(tokenService, times(1)).refreshToken(eq("validRefreshToken"));
    }

    // Additional test cases and methods can be added here for more comprehensive testing
}
