package com.messages.engine.dto;

import com.messages.engine.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String login;
    private String userName;
    private String email;
    private String password;

    public User toUser() {
        return new User(this.getLogin(),
                this.getUserName(),
                this.getEmail(),
                this.getPassword());
    }

}
