package kr.co.tetrips.userservice.user.controller;

import kr.co.tetrips.userservice.user.domain.dto.LoginDTO;
import kr.co.tetrips.userservice.user.domain.dto.UserDTO;
import kr.co.tetrips.userservice.user.domain.dto.MessengerDTO;
import kr.co.tetrips.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<MessengerDTO> signup(@RequestBody UserDTO param) {
        log.info("signup: {}", param);
        return ResponseEntity.ok(userService.signup(param));
    }

    @PostMapping("/login")
    public ResponseEntity<MessengerDTO> login(@RequestBody LoginDTO param) {
        log.info("login: {}", param);
        return ResponseEntity.ok(userService.login(param));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessengerDTO> logout(@RequestHeader("Authorization") String token) {
        log.info("logout: {}", token);
        return ResponseEntity.ok(userService.logout(token));
    }
    @GetMapping("/exists-email")
    public ResponseEntity<MessengerDTO> existsEmail(@RequestParam String email) {
        log.info("existsEmail: {}", email);
        return ResponseEntity.ok(userService.existsEmail(email));
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<MessengerDTO> heartbeat() {
        return ResponseEntity.ok(MessengerDTO.builder().message("SUCCESS").status(200).build());
    }

}
