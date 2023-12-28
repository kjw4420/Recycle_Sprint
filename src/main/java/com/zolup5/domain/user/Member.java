package com.zolup5.domain.user;

import com.zolup5.domain.role.Role;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table
@Setter
@ToString
public class Member {
    @Id
    @GeneratedValue
    private Long pid;
    @Column(unique = true, length = 30)
    private String username;
    @Column(nullable = false)
    private String email;
    @Column(nullable = true, length = 150)
    private String password;
    @Column(nullable = false, length = 30)
    private String area;
    @Column(nullable = false, length = 30)
    private String point;
    @Column(nullable = true, length = 999)
    private String trashcode;
    @ColumnDefault("0")
    private int ranking; //순위
    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

}