package com.netcracker.coctail.controllers;

import com.netcracker.coctail.dao.UserDao;
import com.netcracker.coctail.dto.UserDto;
import com.netcracker.coctail.model.FriendUser;
import com.netcracker.coctail.model.User;
import com.netcracker.coctail.model.UserInfo;
import com.netcracker.coctail.security.jwt.JwtTokenProvider;
import com.netcracker.coctail.service.FriendlistService;
import com.netcracker.coctail.service.UserService;
import javax.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * REST controller user connected requests.
 */

@RestController
@RequestMapping(value = "/api/users/")
@CrossOrigin(origins = "*")
@Data
public class UserRestController {
  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDao userDao;
  private final FriendlistService friendlistService;
  private final BCryptPasswordEncoder passwordEncoder;

  @GetMapping(value = "{id}")
  public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") Long id) {
    User user = userService.getUserById(id);

    if (user == null) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    UserDto result = UserDto.fromUser(user);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping(value = "info")
  public ResponseEntity<UserInfo> seeMyPersonalData(HttpServletRequest request) {
    String email = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    return new ResponseEntity<>(userDao.myInfo(email), HttpStatus.OK);
  }

  @PatchMapping(value = "edit")
  public ResponseEntity editMyPersonalData(HttpServletRequest request,
                                           @RequestBody UserInfo user) {
    String email = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    return new ResponseEntity(userDao.editInfo(email, user), HttpStatus.OK);
  }

  @PostMapping("add/{friendid}")
  public void addFriend(
      @PathVariable(name = "friendid") long friendid,
      HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    friendlistService.addFriend(ownerEmail, friendid);
  }

  @GetMapping("find")
  public ResponseEntity<List<FriendUser>> getUserByNickname(@RequestParam String nickname,
                                                            HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    List<FriendUser> users = friendlistService.getUserByNickname(ownerEmail, nickname);
    if (users.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @PatchMapping("accept/{friendid}")
  public void acceptFriend(
      @PathVariable(name = "friendid") long friendid,
      HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    friendlistService.acceptFriendRequest(ownerEmail, friendid);
  }

  @PatchMapping("decline/{friendid}")
  public void declineFriend(
      @PathVariable(name = "friendid") long friendid,
      HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    friendlistService.declineFriendRequest(ownerEmail, friendid);
  }

  @PatchMapping("subscribe/{friendid}")
  public void subcribeTo(
      @PathVariable(name = "friendid") long friendid,
      HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    friendlistService.subscribeToFriend(ownerEmail, friendid);
  }

  @DeleteMapping("remove/{friendid}")
  public void removeFromFriends(
      @PathVariable(name = "friendid") long friendid,
      HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    friendlistService.removeFriend(ownerEmail, friendid);
  }
}
