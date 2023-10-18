package com.example.service;

import com.example.dto.AuthRequest;
import com.example.jpa.entity.Account;
import com.example.jpa.entity.Person;
import com.example.jpa.entity.TransactionHistory;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    List<Person> getAllPersons();
    Person getPersonById(Long id);

    Person savePerson(Person person);
    Optional<Person> deleteUser(Long id);

    Optional<Account> deleteAccount(Long id);


    List<Person> getAllPersonsSorted(String sortOrder);


public List<TransactionHistory> getAllTransactionSortedByDate();


    ResponseEntity<List<Person>> getPersonsInNegativeBalance();

    void calculateAndSaveTotalMoney(double totalAmount, List<Long> selectedUserIds, String transactionType, String description);
    void calculateAndSaveTotalMoney1(double totalAmount, List<Long> selectedUserIds, String transactionType, String description);

    boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

    List<Person> searchByName(String name);

//    public void updatePerson(Long id, Person updatedPerson);

//    public Person updatePartialPerson(long id, Person updatedPerson);
    public Person updatePartialPerson(Long id, Person updatedPerson);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);


    void calculateAndSaveTotalMoney(double totalAmount, List<Long> selectedUserIds, List<Double> individualAmounts, String transactionType, String description);
    String md5Hash(String input);
//    public Account registerUser(String userName, String password, String confirmPassword);

    public ResponseEntity<?> registerUser(AuthRequest request);

//    public Account loginUser(String userName, String password);
    public ResponseEntity<?> loginUser(AuthRequest request);

    public List<Account> getAllLoginInfor();

//public Account changePassword(String userName, String currentPassword, String newPassword, String confirmPassword);

    public ResponseEntity<?> changePassword(AuthRequest request);
    public List<TransactionHistory> getTransactionById(Long id);
    public ResponseEntity<?> assignRoleToUser(Long userId, Long roleId);
}
