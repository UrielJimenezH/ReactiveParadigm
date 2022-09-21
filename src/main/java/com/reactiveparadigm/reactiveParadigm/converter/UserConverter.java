package com.reactiveparadigm.reactiveParadigm.converter;

import com.reactiveparadigm.reactiveParadigm.domain.User;
import com.reactiveparadigm.reactiveParadigm.dto.UserDto;
import org.springframework.beans.BeanUtils;

public class UserConverter {
    public static UserDto entityToDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);

        return userDto;
    }

    public static User dtoToEntity(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);

        return user;
    }
}
