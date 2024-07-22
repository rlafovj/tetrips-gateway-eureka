package kr.co.tetrips.userservice.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.tetrips.userservice.user.domain.model.QUserModel;
import kr.co.tetrips.userservice.user.domain.model.UserModel;
import lombok.RequiredArgsConstructor;

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
  public Long getUserIdByEmail(String email) {
    QUserModel qUser = QUserModel.userModel;
    return Objects.requireNonNull(factory.selectFrom(qUser)
            .where(qUser.email.eq(email))
            .fetchFirst())
            .getId();
  }
}
