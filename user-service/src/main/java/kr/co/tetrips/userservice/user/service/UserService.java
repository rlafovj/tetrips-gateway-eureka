package kr.co.tetrips.userservice.user.service;

import kr.co.tetrips.userservice.user.domain.dto.LoginResultDTO;
import kr.co.tetrips.userservice.user.domain.model.UserModel;
import kr.co.tetrips.userservice.user.domain.dto.UserDTO;
import kr.co.tetrips.userservice.user.domain.dto.MessengerDTO;

public interface UserService {

  default UserDTO entityToDTO(UserModel userModel) {
    return UserDTO.builder()
      .id(userModel.getId())
      .email(userModel.getEmail())
      .password(userModel.getPassword())
      .nickname(userModel.getNickname())
      .gender(userModel.isGender())
      .birthDate(userModel.getBirthDate())
      .build();
  }
  default UserModel dtoToEntity(UserDTO userDTO) {
    return UserModel.builder()
            .id(userDTO.getId())
            .email(userDTO.getEmail())
            .password(userDTO.getPassword())
            .nickname(userDTO.getNickname())
            .gender(userDTO.isGender())
            .birthDate(userDTO.getBirthDate())
            .build();
  }
  MessengerDTO signup(UserDTO param);
  LoginResultDTO login(UserDTO param);

  MessengerDTO existsEmail(String email);

  String getNickname(UserDTO dtoOnlyEmail);

  MessengerDTO existsNickname(String nickname);

  UserDTO getUserInfo(String email);
}
