package prj.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomUserRepository extends CrudRepository<String, Long> {

//
//    @Query("SELECT user_id FROM users WHERE :username like 'petar@peric.com'")
//    String findIdByUsername(@Param("username") String username);



}
