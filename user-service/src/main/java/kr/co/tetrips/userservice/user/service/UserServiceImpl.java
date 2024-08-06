package kr.co.tetrips.userservice.user.service;

import jakarta.transaction.Transactional;
import kr.co.tetrips.userservice.user.domain.dto.LoginResultDTO;
import kr.co.tetrips.userservice.user.domain.model.RoleModel;
import kr.co.tetrips.userservice.user.domain.model.UserModel;
import kr.co.tetrips.userservice.user.domain.dto.UserDTO;
import kr.co.tetrips.userservice.user.UserRepository;
import kr.co.tetrips.userservice.user.domain.dto.MessengerDTO;
import kr.co.tetrips.userservice.user.domain.vo.Registration;
import kr.co.tetrips.userservice.user.domain.vo.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public MessengerDTO signup(UserDTO dto) {
        return Stream.of(dto)
                .filter(i -> !userRepository.existsByEmail(i.getEmail()))
                .filter(i -> !userRepository.existsByNickname(i.getNickname()))
                .map(i -> {
                    UserModel userModel = UserModel.builder()
                            .email(i.getEmail())
                            .password(passwordEncoder.encode(i.getPassword()))
                            .nickname(i.getNickname())
                            .gender(i.isGender())
                            .birthDate(i.getBirthDate())
                            .registration(Registration.LOCAL)
                            .build();
                    RoleModel roleModel = RoleModel.builder()
                            .role(Role.USER)
                            .userModel(userModel)
                            .build();
                    userModel.setRoleId(List.of(roleModel));
                    return userRepository.save(userModel);
                })
                .map(i -> MessengerDTO.builder()
                        .message("SIGN UP SUCCESS")
                        .status(200)
                        .build())
                .findAny()
                .orElseGet(() -> MessengerDTO.builder()
                        .message("Duplicate Email or Nickname")
                        .status(409)
                        .build());
    }

    @Transactional
    @Override
    public LoginResultDTO login(UserDTO param) {
        log.info(">>> localLogin Impl 진입: {} ", param);
        UserModel userModel = userRepository.findUserByEmail(param.getEmail()).orElseGet(() -> UserModel.builder().build());
        boolean flag = passwordEncoder.matches(param.getPassword(), userModel.getPassword());
        return flag ? LoginResultDTO.builder()
                .user(UserDTO.builder()
                        .email(userModel.getEmail())
                        .role(userModel.getRoleId().stream().map(i -> Role.getRole(i.getRole().ordinal())).toList())
                        .build())
                .build()
                :
                LoginResultDTO.builder()
                        .user(UserDTO.builder()
                                .email("Login Fail")
                                .role(null)
                                .build())
                        .build();
    }
    //for MVC
//    @Override
//    public MessengerDTO login(LoginDTO param) {
//        try {
//            UserModel userModel = userRepository.findUserByEmail(param.getEmail()).orElseGet(() -> UserModel.builder().build());
//            boolean flag = securityConfig.passwordEncoder().matches(param.getPassword(), userModel.getPassword());
//            String accessToken = null;
//            String refreshToken = null;
//            if (flag) {
//                accessToken = jwtProvider.createAccessToken(entityToDTO(userModel));
//                refreshToken = jwtProvider.createRefreshToken(entityToDTO(userModel));
//
//                jwtProvider.printPayload(accessToken);
//                jwtProvider.printPayload(refreshToken);
//
//                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//                UserDetails userDetails = userDetailsService.loadUserByUsername(userModel.getEmail());
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, param.getPassword(), userDetails.getAuthorities());
//                Authentication authentication = authenticationManager.authenticate(authenticationToken);
//                securityContext.setAuthentication(authentication);
//                if(authentication.isAuthenticated()){
//                    log.info("@@@@@@@@@@@@인증된유저임@@@@@@@@@@@@User is authenticated@@@@@@@@@@@@@@");
//                }
//                SecurityContextHolder.setContext(securityContext);
//                Token token = Token.builder()
//                        .userId(userModel)
//                        .expDate(jwtProvider.getRefreshExpired())
//                        .refreshToken(refreshToken)
//                        .build();
//                tokenRepository.save(token);
//            }
//
//            return MessengerDTO.builder()
//                    .message(flag ? "SUCCESS" : "FAIL")
//                    .accessToken(flag ? accessToken : null)
//                    .refreshToken(flag ? refreshToken : null)
//                    .status(flag ? 200 : 401)
//                    .build();
//        }catch (Exception e) {
//            log.info("로그인 실패 : " + e.getMessage());
//            return MessengerDTO.builder()
//                    .message("FAIL")
//                    .status(500)
//                    .build();
//        }
//    }

//    @Override
//    @Transactional
//    public UserModel deleteToken(UserModel userModel) {
//        return Stream.of(userModel)
//                .filter(i -> i.getToken() != null)
//                .peek(i -> tokenRepository.deleteById(i.getToken().getId()))
//                .peek(i -> i.setToken(null))
//                .map(i -> userRepository.save(i))
//                .findFirst()
//                .get();
//    }

    @Override
    public MessengerDTO existsEmail(String email) {
        return userRepository.existsByEmail(email) ?
                MessengerDTO.builder()
                        .message("EXIST")
                        .status(409)
                        .build() :
                MessengerDTO.builder()
                        .message("POSSIBLE")
                        .status(200)
                        .build();
    }

    @Override
    public String getNickname(UserDTO dtoOnlyEmail) {
        UserModel userModel = userRepository.findUserByEmail(dtoOnlyEmail.getEmail()).orElseGet(() -> UserModel.builder().build());
        return userModel.getNickname();
    }

    @Override
    public MessengerDTO existsNickname(String nickname) {
        return userRepository.existsByNickname(nickname) ?
                MessengerDTO.builder()
                        .message("EXIST")
                        .status(409)
                        .build() :
                MessengerDTO.builder()
                        .message("POSSIBLE")
                        .status(200)
                        .build();
    }

    @Override
    public UserDTO getUserInfo(String email) {
        UserModel userModel = userRepository.findUserByEmail(email).orElseGet(() -> UserModel.builder().build());
        List<Role> role = userModel.getRoleId().stream().map(roleModel -> roleModel.getRole()).toList();
        return UserDTO.builder()
                .email(userModel.getEmail())
                .nickname(userModel.getNickname())
                .gender(userModel.isGender())
                .birthDate(userModel.getBirthDate())
                .registration(userModel.getRegistration())
                .role(role)
                .build();
    }

    @Override
    public UserDTO updateUserInfo(UserDTO dto) {
        UserModel updateUser = dtoToEntity(dto);
        return userRepository.updateUserInfo(updateUser)? getUserInfo(dto.getEmail()) : null;
    }

    @Override
    public MessengerDTO deleteUser(String email) {
        UserModel userModel = userRepository.findUserByEmail(email).orElseGet(() -> UserModel.builder().build());
        if (userModel.getEmail() == null || userModel.getEmail().isEmpty()) {
            return MessengerDTO.builder()
                    .message("Already Deleted User or Not Available User")
                    .status(404)
                    .build();
        } else {
            userRepository.delete(userModel);
            return MessengerDTO.builder()
                    .message("Delete Success")
                    .status(200)
                    .build();
        }
    }

}
