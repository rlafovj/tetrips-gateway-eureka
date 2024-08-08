package kr.co.tetrips.userservice.user;

import kr.co.tetrips.userservice.user.domain.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Long> {
  void deleteByUserModel_Id(Long userId);
}