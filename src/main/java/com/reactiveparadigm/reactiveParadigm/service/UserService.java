package com.reactiveparadigm.reactiveParadigm.service;

import com.reactiveparadigm.reactiveParadigm.converter.UserConverter;
import com.reactiveparadigm.reactiveParadigm.dto.UserDto;
import com.reactiveparadigm.reactiveParadigm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Flux<UserDto> getAllUsers() {
        return userRepository.findAll()
                .map(UserConverter::entityToDto);
    }

    public Mono<UserDto> getUser(String id) {
        return userRepository.findById(id)
                .map(UserConverter::entityToDto);
    }
}
