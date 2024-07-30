package kr.co.tetrips.userservice.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.tetrips.userservice.user.domain.model.QUserModel;
import kr.co.tetrips.userservice.user.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class UserQueryDSLImpl implements UserQueryDSL{
  private final JPAQueryFactory factory;


  @Override
  public Optional<UserModel> findUserByEmail(String email) {
    QUserModel qUser = QUserModel.userModel;
    return Optional.ofNullable(factory.selectFrom(qUser)
            .where(qUser.email.eq(email))
            .fetchFirst());
  }

  @Override
  public boolean existsByEmail(String email) {
    QUserModel qUser = QUserModel.userModel;
    return factory.selectFrom(QUserModel.userModel)
            .where(qUser.email.eq(email))
            .fetchFirst() != null;
  }

  @Override
  public boolean existsByNickname(String nickname) {
    QUserModel qUser = QUserModel.userModel;
    return factory.selectFrom(QUserModel.userModel)
            .where(qUser.nickname.eq(nickname))
            .fetchFirst() != null;
  }

  @Transactional
  @Override
  public boolean updateUserInfo(UserModel updateUser) {
    QUserModel qUser = QUserModel.userModel;
    return factory.update(qUser)
            .where(qUser.email.eq(updateUser.getEmail()))
            .set(qUser.nickname, updateUser.getNickname())
            .set(qUser.birthDate, updateUser.getBirthDate())
            .execute() >= 0L;

  }

  @Override
  public Long getUserIdByEmail(String email) {
    QUserModel qUser = QUserModel.userModel;
    return Objects.requireNonNull(factory.selectFrom(qUser)
            .where(qUser.email.eq(email))
            .fetchFirst())
            .getId();
  }
}
