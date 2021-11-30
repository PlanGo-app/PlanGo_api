package com.plango.api.controller;

import com.plango.api.dto.user.UserDto;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.dto.travel.UserTravelsDto;
import com.plango.api.dto.user.UserUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/user")
public interface UserController {

    @GetMapping(path = "")
    ResponseEntity<UserDto> getCurrentUser() throws UserNotFoundException;

    @PostMapping(path = "", consumes="application/json")
    ResponseEntity<String> createUser(@RequestBody UserDto userDto);

    @PutMapping(path = "")
    ResponseEntity<String> updateCurrentUser(@RequestBody UserUpdateDto userUpdateDto);

    @DeleteMapping(path = "")
    ResponseEntity<String> deleteCurrentUser();

    @GetMapping("/travels")
    ResponseEntity<UserTravelsDto> getTravelsOfCurrentUser();
}
