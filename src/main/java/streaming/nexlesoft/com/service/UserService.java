package streaming.nexlesoft.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import streaming.nexlesoft.com.entity.User;
import streaming.nexlesoft.com.repository.UserRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    public ResponseEntity<Map<String, Object>> signUp(String email, String password, String firstName, String lastName) {
        // Validation
        if (!isEmailValid(email) || !isPasswordValid(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Validation error"));
        }

        // Check if the email is available
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Email is already in use"));
        }

        // Create user object
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        // Encrypt password
        String hashedPassword = bcryptEncrypt(password);
        user.setHash(hashedPassword);

        // Save user to the database
        User savedUser = userRepository.save(user);

        // Create response object
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("firstName", savedUser.getFirstName());
        response.put("lastName", savedUser.getLastName());
        response.put("email", savedUser.getEmail());
        response.put("displayName", savedUser.getFirstName() + " " + savedUser.getLastName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<Map<String, Object>> signIn(String email, String password) {
        // Validation
        if (!isEmailValid(email) || !isPasswordValid(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Validation error"));
        }

        // Authenticate user
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (bcryptMatches(password, user.getHash())) {
                // Generate tokens
                Map<String, Object> tokens = tokenService.generateTokens(user);

                // Create response object
                Map<String, Object> response = new HashMap<>();
                response.put("user", getUserInfoMap(user));
                response.put("token", tokens.get("token"));
                response.put("refreshToken", tokens.get("refreshToken"));

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Internal error"));
    }

    public ResponseEntity<Void> signOut(User user) {
        tokenService.invalidateRefreshTokens(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        // Implement password validation logic
        return password.length() >= 8 && password.length() <= 20;
    }

    private String bcryptEncrypt(String password) {
        // Implement bcrypt encryption logic
        // Return the encrypted password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private boolean bcryptMatches(String rawPassword, String hashedPassword) {
        // Implement bcrypt password matching logic
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, hashedPassword);
    }

    private Map<String, Object> getUserInfoMap(User user) {
        // Create and return a map with user information
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        userInfo.put("email", user.getEmail());
        userInfo.put("displayName", user.getFirstName() + " " + user.getLastName());
        return userInfo;
    }

}
