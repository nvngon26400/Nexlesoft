package streaming.nexlesoft.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import streaming.nexlesoft.com.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}