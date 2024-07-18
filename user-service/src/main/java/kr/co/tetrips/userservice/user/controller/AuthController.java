package kr.co.tetrips.userservice.user.controller;

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
  public ResponseEntity<Role> localLogin(@RequestBody UserDTO dto) {
    log.info(">>> local login con 진입: {} ", dto);
    return ResponseEntity.ok(userService.localLogin(dto));
  }

  @PostMapping("/join")
  public ResponseEntity<MessengerDTO> join(@RequestBody UserDTO dto) {
    log.info(">>> join con 진입: {}", dto);
    return ResponseEntity.ok(userService.save(dto));
  }

  @PostMapping("/oauth2/{registration}")
  // public ResponseEntity<Messenger> oauthLogin(@RequestBody UserDto dto) {
  public Boolean oauthLogin(@RequestBody Map<String, Object> dto) {
    log.info(">>> oauthJoin con 진입: {}", dto);
    return true;
    // return ResponseEntity.ok(service.save(dto));
  }
}
