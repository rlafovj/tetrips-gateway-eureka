package kr.co.tetrips.userservice.user.controller;

import kr.co.tetrips.userservice.user.domain.dto.LoginResultDTO;
import kr.co.tetrips.userservice.user.domain.dto.UserDTO;
import kr.co.tetrips.userservice.user.domain.dto.MessengerDTO;
import kr.co.tetrips.userservice.user.domain.vo.Role;
import kr.co.tetrips.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;

  @PostMapping("/login/local")
  public ResponseEntity<LoginResultDTO> localLogin(@RequestBody UserDTO dto) {
    log.info(">>> local login con 진입: {} ", dto);
    return ResponseEntity.ok(userService.login(dto));
  }

  @PostMapping("/getNickname")
  public ResponseEntity<String> getNickname(@RequestBody UserDTO dtoOnlyEmail) {
    log.info(">>> getNickname con 진입: {}", dtoOnlyEmail);
    return ResponseEntity.ok(userService.getNickname(dtoOnlyEmail));
  }

  @PostMapping("/signup")
  public ResponseEntity<MessengerDTO> signup(@RequestBody UserDTO dto) {
    log.info(">>> join con 진입: {}", dto);
    return ResponseEntity.ok(userService.signup(dto));
  }

  @PostMapping("/oauth2/{registration}")
  // public ResponseEntity<Messenger> oauthLogin(@RequestBody UserDto dto) {
  public Boolean oauthLogin(@RequestBody Map<String, Object> dto) {
    log.info(">>> oauthJoin con 진입: {}", dto);
    return true;
    // return ResponseEntity.ok(service.save(dto));
  }
}
