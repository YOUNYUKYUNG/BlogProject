package org.example.blogproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blogproject.domain.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String password;
    private String passwordConfirm;
    private String profileImageUrl;
    private String provider;
    private String socialId;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.passwordConfirm = user.getPasswordConfirm();
        this.profileImageUrl = user.getProfileImageUrl();
        this.provider = user.getProvider();
        this.socialId = user.getSocialId();
    }
}