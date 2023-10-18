package com.example.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.text.NumberFormat;
import java.util.Locale;


@Entity
@Table(name = "Person")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "total_money")
    private double totalMoney;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    @Column(name = "age")
    private int age;
    @Column(name = "dob")
    private LocalDate dob;

    @Transient // Đánh dấu trường này không cần mapping vào cơ sở dữ liệu
    private String formattedTotalmoney;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalMoney=" + totalMoney +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", age=" + age +
                ", dob=" + dob +
                ", formattedTotalmoney='" + formattedTotalmoney + '\'' +
                '}';
    }

    public String getFormattedTotalMoney() {
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        return currencyFormatter.format(totalMoney);
    }
    public void setFormattedTotalMoney(String formattedTotalmoney) {
        this.formattedTotalmoney = formattedTotalmoney;
    }

//    public String getFormattedTotalMoney() {
//        DecimalFormat decimalFormat = new DecimalFormat("#,##0 VNĐ");
//        return decimalFormat.format(totalMoney);
//    }




}
