package com.darwgom.tradibankapi.application.usecases.implement;

import com.darwgom.tradibankapi.application.dto.UserDTO;
import com.darwgom.tradibankapi.application.dto.LoginDTO;
import com.darwgom.tradibankapi.application.dto.UserInputDTO;
import com.darwgom.tradibankapi.application.dto.TokenDTO;
import com.darwgom.tradibankapi.application.dto.MessageDTO;
import com.darwgom.tradibankapi.application.usecases.IUserUseCase;
import com.darwgom.tradibankapi.domain.entities.User;
import com.darwgom.tradibankapi.domain.exceptions.EntityNotFoundException;
import com.darwgom.tradibankapi.domain.exceptions.IllegalParamException;
import com.darwgom.tradibankapi.domain.exceptions.UsernameAlreadyExistsException;
import com.darwgom.tradibankapi.domain.exceptions.UsernameNotFoundException;
import com.darwgom.tradibankapi.domain.repositories.UserRepository;
import com.darwgom.tradibankapi.infrastructure.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class UserUseCase implements IUserUseCase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    private static final Map<String, String> typeNormalizationMap = Map.of(
            "ROLE_ADMIN", "ROLE_ADMIN",
            "ROLEADMIN", "ROLE_ADMIN",
            "ADMIN", "ROLE_ADMIN",
            "ROLE_USER", "ROLE_USER",
            "ROLEUSER", "ROLE_USER",
            "USER", "ROLE_USER"
    );

    @Override
    public UserDTO registerUser(UserInputDTO userInputDTO) {
        Optional<User> existingUser = userRepository.findByUsername(userInputDTO.getUsername());
        String normalizedType = normalizeRoleType(userInputDTO.getRoleType());
        userInputDTO.setRoleType(normalizedType);
        if (existingUser.isPresent()) {
            throw new UsernameAlreadyExistsException("User already exists!");
        } else {
            userInputDTO.setPassword(passwordEncoder.encode(userInputDTO.getPassword()));
            User user = modelMapper.map(userInputDTO, User.class);
            User savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserDTO.class);
        }
    }


    @Override
    public TokenDTO loginUser(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + loginDTO.getUsername()));

        String jwt = jwtTokenProvider.createToken(user.getUsername(), user.getRoleType().name());
        user.setLastLogin(LocalDateTime.now());

        return new TokenDTO(jwt);
    }


    @Override
    public MessageDTO deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        userRepository.delete(user);
        return new MessageDTO("User deleted successfully.");
    }

    @Override
    public UserDTO getCurrentUser(HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        if (jwt == null || jwt.isEmpty()) {
            throw new UsernameNotFoundException("No JWT token found in request headers");
        }
        String username = jwtTokenProvider.getUsernameFromToken(jwt);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));

        return modelMapper.map(user, UserDTO.class);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    private String normalizeRoleType(String type) {
        if (type == null) {
            throw new IllegalParamException("Role type cannot be null");
        }
        String normalizedType = typeNormalizationMap.get(type.toUpperCase());
        if (normalizedType == null) {
            throw new IllegalParamException("Invalid role type: " + type);
        }
        return normalizedType;
    }

}

