package ru.valkerik.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.valkerik.entity.enums.UserState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;

    @CreationTimestamp
    private LocalDateTime firstLoginDate;

    private String firstName;

    private String lastName;

    private String userName;

    private String email;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private UserState userState;




}
