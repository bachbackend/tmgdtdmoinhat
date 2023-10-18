package com.example.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "update_history")
@Getter
@Setter
public class UpdateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "update_name")
    private String updateName;

    @Column(name = "update_age")
    private int updateAge;

    @Column(name = "update_phone")
    private String updatePhone;

    @Column(name = "update_email")
    private String updateEmail;

    @Column(name = "update_dob")
    private LocalDate updateDob;

//    @Column(name = "update_id")
//    private long updateId;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "UpdateHistory{" +
                "id=" + id +
                ", updateDate=" + updateDate +
                ", updateName='" + updateName + '\'' +
                ", updateAge=" + updateAge +
                ", updatePhone='" + updatePhone + '\'' +
                ", updateEmail='" + updateEmail + '\'' +
//                ", updateId=" + updateId +
                ", person=" + person +
                '}';
    }
}
