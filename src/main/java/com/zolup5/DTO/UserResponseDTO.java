package com.zolup5.DTO;
import com.zolup5.domain.user.Member;
import com.zolup5.domain.role.Role;
import lombok.*;

@Getter
@NoArgsConstructor
public class UserResponseDTO {

    private String username;
    private String email;
    private String area;
    private String point;
    private String trash;
    private Role role;

    @Builder
    public UserResponseDTO(Member user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.area = user.getArea();
        this.point = user.getPoint();
        this.trash = user.getTrashcode();
        this.role = user.getRole();
    }
}