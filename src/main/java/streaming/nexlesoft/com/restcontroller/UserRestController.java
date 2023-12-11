package streaming.nexlesoft.com.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import streaming.nexlesoft.com.entity.User;
import streaming.nexlesoft.com.service.UserService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    private UserService userService;

    /**
     * Handles the HTTP POST request for user registration (Sign-up).
     *
     * @param userRequest A map containing user registration details (email, password, firstName, lastName).
     * @return ResponseEntity containing the result of the sign-up operation.
     */
    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody Map<String, String> userRequest) {
        try {
            // Delegates the sign-up request to the userService.
            return userService.signUp(userRequest.get("email"), userRequest.get("password"),
                    userRequest.get("firstName"), userRequest.get("lastName"));
        } catch (Exception e) {
            // Handles exceptions by returning a 500 Internal Server Error response.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Internal error"));
        }
    }

    /**
     * Handles the HTTP POST request for user authentication (Sign-in).
     *
     * @param credentials A map containing user credentials (email, password).
     * @return ResponseEntity containing the result of the sign-in operation.
     */
    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, String> credentials) {
        try {
            // Delegates the sign-in request to the userService.
            return userService.signIn(credentials.get("email"), credentials.get("password"));
        } catch (Exception e) {
            // Handles exceptions by returning a 500 Internal Server Error response.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Internal error"));
        }
    }

    /**
     * Handles the HTTP POST request for user logout (Sign-out).
     *
     * @return ResponseEntity representing the result of the sign-out operation.
     */
    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut() {
        try {
            // Get the currently authenticated user from the security context.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                // Extract user information from authentication object.
                User currentUser = (User) authentication.getPrincipal();

                // Call the signOut method in the userService.
                return userService.signOut(currentUser);
            } else {
                // If the user is not authenticated, return a 401 Unauthorized status.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            // Handles exceptions by returning a 500 Internal Server Error response.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

