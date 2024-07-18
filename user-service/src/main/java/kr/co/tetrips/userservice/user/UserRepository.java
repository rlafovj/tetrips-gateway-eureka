package kr.co.tetrips.userservice.user;

import kr.co.tetrips.userservice.user.domain.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long>, UserQueryDSL {

}
