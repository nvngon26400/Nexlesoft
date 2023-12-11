package streaming.nexlesoft.com.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import streaming.nexlesoft.com.entity.User;
import streaming.nexlesoft.com.restcontroller.UserRestController;
import streaming.nexlesoft.com.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

public class UserControllerTest {

    // Mocking the UserService dependency
    @Mock
    private UserService userService;

    // Injecting mock dependencies into the UserController
    @InjectMocks
    private UserRestController userController;

    // Constructor to initialize mocks and annotations
    public UserControllerTest() {
        MockitoAnnotations.initMocks(this);
    }

    // Test case for the signUp method with valid input
    @Test
    void signUp_ValidInput_Success() {
        // Mock input data
        Map<String, String> userRequest = new HashMap<>();
        userRequest.put("email", "test@example.com");
        userRequest.put("password", "password123");
        userRequest.put("firstName", "John");
        userRequest.put("lastName", "Doe");

        // Mock service response
        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("test@example.com");
        savedUser.setHash("hashedPassword");

        // Mocking the signUp method of the userService
        // Returning a ResponseEntity with CREATED status and a user response map
        when(userService.signUp(any(), any(), any(), any())).thenReturn(
                ResponseEntity.status(HttpStatus.CREATED).body(getUserResponseMap(savedUser))
        );

        // Perform the test by calling the signUp method on the userController
        ResponseEntity<Map<String, Object>> response = userController.signUp(userRequest);

        // Verify the result
        // Asserting that the response status is HttpStatus.CREATED
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        // Asserting that the response body matches the expected user response map
        Assertions.assertEquals(getUserResponseMap(savedUser), response.getBody());

        // Verify that the service method was called with the correct parameters
        verify(userService, times(1)).signUp(
                eq("test@example.com"), eq("password123"), eq("John"), eq("Doe")
        );
    }

    // Helper method to create a user response map
    private Map<String, Object> getUserResponseMap(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("email", user.getEmail());
        response.put("displayName", user.getFirstName() + " " + user.getLastName());
        return response;
    }
}
