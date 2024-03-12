package com.darwgom.tradibankapi.application.usecases;

import com.darwgom.tradibankapi.application.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

public interface IUserUseCase {
    UserDTO registerUser(UserInputDTO userInputDTO);
    TokenDTO loginUser(LoginDTO loginDTO);
    MessageDTO deleteUser(Long userId);
    UserDTO getCurrentUser(HttpServletRequest request);
}
