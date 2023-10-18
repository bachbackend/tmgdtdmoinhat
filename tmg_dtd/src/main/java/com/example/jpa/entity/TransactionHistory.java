package com.example.jpa.entity;

import lombok.*;
import javax.persistence.*;
import java.util.Date;
import java.text.NumberFormat;
import java.util.Locale;

@Entity
@Table(name = "transaction_history")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transactionDate")
    private Date transactionDate;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_amount")
    private double transactionAmount;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "sub_total")
    private double subTotal;
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "login_id")
    private Account account;

    @Transient
    private String formattedSubTotal;
    @Transient
    private String formattedTransactionAmount;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    public String getFormattedSubTotal() {
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        return currencyFormatter.format(subTotal);
    }
    public void setFormattedSubTotal(String formattedSubTotal) {
        this.formattedSubTotal = formattedSubTotal;
    }

    public String getFormattedTransactionAmount() {
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        return currencyFormatter.format(transactionAmount);
    }
    public void setFormattedTransactionAmount(String formattedTransactionAmount) {
        this.formattedTransactionAmount = formattedTransactionAmount;
    }



//    public String getFormattedTotalMoney() {
//        Locale locale = new Locale("vi", "VN");
//        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
//        return currencyFormatter.format(totalMoney);
//    }
//    public void setFormattedTotalMoney(String formattedTotalmoney) {
//        this.formattedTotalmoney = formattedTotalmoney;
//    }


    @Override
    public String toString() {
        return "TransactionHistory{" +
                "id=" + id +
                ", transactionDate=" + transactionDate +
                ", transactionType='" + transactionType + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", person=" + person +
                ", subTotal=" + subTotal +
                ", description='" + description + '\'' +
                '}';
    }
}
