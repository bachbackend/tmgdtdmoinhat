package com.example.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "loginDate")
    private LocalDateTime loginDate;

    @Column(name = "login_name")
    private String userNameHis;

    @ManyToOne
    @JoinColumn(name = "login_id")
    private Account account;

    @Override
    public String toString() {
        return "LoginHistory{" +
                "id=" + id +
                ", loginDate=" + loginDate +
                ", account=" + account +
                '}';
    }
}
