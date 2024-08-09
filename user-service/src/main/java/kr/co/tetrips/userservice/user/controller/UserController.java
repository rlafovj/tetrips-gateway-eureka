package kr.co.tetrips.userservice.user.controller;

import kr.co.tetrips.userservice.user.domain.dto.LoginResultDTO;
import kr.co.tetrips.userservice.user.domain.dto.UserDTO;
import kr.co.tetrips.userservice.user.domain.dto.MessengerDTO;
import kr.co.tetrips.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Slf4j
@RestController
@CrossOrigin(value = "*", allowedHeaders = "*")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

//    @PostMapping("/signup")
//    public ResponseEntity<MessengerDTO> signup(@RequestBody UserDTO param) {
//        log.info("signup: {}", param);
//        return ResponseEntity.ok(userService.signup(param));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<MessengerDTO> login(@RequestBody LoginResultDTO param) {
//        log.info("login: {}", param);
//        return ResponseEntity.ok(userService.login(param));
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<MessengerDTO> logout(@RequestHeader("Authorization") String token) {
//        log.info("logout: {}", token);
//        return ResponseEntity.ok(userService.logout(token));
//    }
    @GetMapping("/exists-email")
    public ResponseEntity<MessengerDTO> existsEmail(@RequestParam String email) {
        log.info("existsEmail: {}", email);
        MessengerDTO result = userService.existsEmail(email);
        if(result.getStatus() == 200) {return ResponseEntity.ok(userService.existsEmail(email));}
        else {return ResponseEntity.status(409).body(result);}
    }

    @GetMapping("/exists-nickname")
    public ResponseEntity<MessengerDTO> existsNickname(@RequestParam String nickname) {
        try {
            String decodedNickname = URLDecoder.decode(nickname, "UTF-8");
            log.info("existsNickname: {}", decodedNickname);
            MessengerDTO result = userService.existsNickname(decodedNickname);
            if(result.getStatus() == 200) {return ResponseEntity.ok(userService.existsNickname(decodedNickname));}
            else {return ResponseEntity.status(409).body(result);}
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(MessengerDTO.builder().message("URL Decode Error").status(400).build());
        }
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<UserDTO> getUserInfo(@RequestBody UserDTO dtoOnlyEmail) {
        log.info(">>> getUserInfo con 진입: {}", dtoOnlyEmail);
        return ResponseEntity.ok(userService.getUserInfo(dtoOnlyEmail.getEmail()));
    }

    @PostMapping("/updateUserInfo")
    public ResponseEntity<UserDTO> updateUserInfo(@RequestBody UserDTO dto) {
        log.info("updateUserInfo: {}", dto);
        return ResponseEntity.ok(userService.updateUserInfo(dto));
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<MessengerDTO> deleteUser(@RequestParam String email) {
        log.info("deleteUser: {}", email);
        return ResponseEntity.ok(userService.deleteUser(email));
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<MessengerDTO> heartbeat() {
        return ResponseEntity.ok(MessengerDTO.builder().message("SUCCESS").status(200).build());
    }

}
