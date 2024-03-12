package com.darwgom.tradibankapi.infrastructure.controllers;

import com.darwgom.tradibankapi.application.dto.UserInputDTO;
import com.darwgom.tradibankapi.application.dto.UserDTO;
import com.darwgom.tradibankapi.application.dto.TokenDTO;
import com.darwgom.tradibankapi.application.dto.MessageDTO;
import com.darwgom.tradibankapi.application.dto.LoginDTO;
import com.darwgom.tradibankapi.application.usecases.IUserUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private IUserUseCase userUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserInputDTO userInputDTO) {
        UserDTO userDTO = userUseCase.registerUser(userInputDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> loginUser(@RequestBody LoginDTO loginDTO) {
        TokenDTO tokenDTO = userUseCase.loginUser(loginDTO);
        return new ResponseEntity<>(tokenDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MessageDTO> deleteUser(@PathVariable Long userId) {
        MessageDTO messageDTO = userUseCase.deleteUser(userId);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        UserDTO userDTO = userUseCase.getCurrentUser(request);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
}


