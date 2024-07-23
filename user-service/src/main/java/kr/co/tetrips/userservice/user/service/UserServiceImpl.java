package kr.co.tetrips.userservice.user.service;

import jakarta.transaction.Transactional;
import kr.co.tetrips.userservice.user.domain.dto.LoginResultDTO;
import kr.co.tetrips.userservice.user.domain.model.UserModel;
import kr.co.tetrips.userservice.user.domain.dto.UserDTO;
import kr.co.tetrips.userservice.user.UserRepository;
import kr.co.tetrips.userservice.user.domain.dto.MessengerDTO;
import kr.co.tetrips.userservice.user.domain.vo.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public MessengerDTO signup(UserDTO param) {
        return Stream.of(param)
                .filter(i -> !userRepository.existsByEmail(i.getEmail()))
                .map(i -> userRepository.save(UserModel.builder()
                        .email(i.getEmail())
                        .password(passwordEncoder.encode(i.getPassword()))
                        .nickname(i.getNickname())
                        .gender(i.isGender())
                        .birthDate(i.getBirthDate())
                        .build()))
                .map(i -> MessengerDTO.builder()
                        .message("SUCCESS")
                        .status(200)
                        .build())
                .findAny()
                .orElseGet(() -> MessengerDTO.builder()
                        .message("FAIL")
                        .status(409)//duplicate email
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
                        .message("SUCCESS")
                        .status(200)
                        .build() :
                MessengerDTO.builder()
                        .message("FAIL")
                        .status(409)//duplicate email
                        .build();
    }
}