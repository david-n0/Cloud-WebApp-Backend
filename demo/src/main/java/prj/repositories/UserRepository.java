package prj.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import prj.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String email);

//    @Query("SELECT u.userId FROM User u WHERE u.username like (:username)")
//    Long findIdByUsername(@Param("username") String username);

//    @Query(value = "SELECT permissions FROM users.user_permissions WHERE user_user_id = :userId", nativeQuery = true)
//    List<String> getPermissionsForUserId(Long userId);

    // void deleteUserByUserId(Long id);
}
