package kr.co.tetrips.userservice.user;

import kr.co.tetrips.userservice.user.domain.model.UserModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserQueryDSL {
  Optional<UserModel> findUserByEmail(String email);
  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);
  Long getUserIdByEmail(String email);
}
