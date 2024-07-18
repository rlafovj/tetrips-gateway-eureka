package kr.co.tetrips.userservice.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.tetrips.userservice.user.domain.model.UserModel;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class UserQueryDSLImpl implements UserQueryDSL{
  private final JPAQueryFactory factory;

  @Override
  public Optional<UserModel> findUserByEmail(String email) {
    QUser qUser = QUser.user;
    return Optional.ofNullable(factory.selectFrom(qUser)
            .where(qUser.email.eq(email))
            .fetchFirst());
  }

  @Override
  public boolean existsByEmail(String email) {
    QUser qUser = QUser.user;
    return factory.selectFrom(QUser.user)
            .where(qUser.email.eq(email))
            .fetchFirst() != null;
  }

  @Override
  public Long getUserIdByEmail(String email) {
    QUser qUser = QUser.user;
    return Objects.requireNonNull(factory.selectFrom(qUser)
            .where(qUser.email.eq(email))
            .fetchFirst())
            .getId();
  }
}
