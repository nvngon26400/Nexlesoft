package streaming.nexlesoft.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import streaming.nexlesoft.com.entity.Token;
import streaming.nexlesoft.com.entity.User;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByRefreshToken(String refreshToken);
    void deleteByUser(User user);

}
