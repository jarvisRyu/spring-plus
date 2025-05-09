package org.example.expert.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private final Long id;
    private final String email;
    private final String nickname;

    public UserResponse(Long id, String email,String nickname) {
        this.id = id;
        this.email = email;
        this.nickname=nickname;
    }
}
