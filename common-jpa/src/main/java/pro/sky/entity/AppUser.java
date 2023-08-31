package pro.sky.entity;



import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import pro.sky.enums.UserState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name="app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private Long telegramUserId;

    /*добавит текущую дату на момент сохранения данных в БД*/
    @CreationTimestamp
    private LocalDateTime firstLoginDate;
    private String firstName;
    private String lastname;
    private String userName;
    private String email;
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private UserState state;


}
