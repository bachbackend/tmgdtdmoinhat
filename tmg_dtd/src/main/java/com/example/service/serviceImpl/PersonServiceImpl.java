package com.example.service.serviceImpl;

import com.example.dto.AuthRequest;
import com.example.dto.AuthResponse;
import com.example.jpa.entity.*;
import com.example.jpa.repository.*;
import com.example.jwt.JwtTokenUtil;
import com.example.service.PersonService;
import io.jsonwebtoken.Claims;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UpdateHistoryRepository updateHistoryRepository;
    private final AccountRepository accountRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private RedissonClient redissonClient;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository,
                             TransactionHistoryRepository transactionHistoryRepository,
                             UpdateHistoryRepository updateHistoryRepository,
                             AccountRepository accountRepository,
                             LoginHistoryRepository loginHistoryRepository,
                             RoleRepository roleRepository) {
        this.personRepository = personRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.updateHistoryRepository = updateHistoryRepository;
        this.accountRepository = accountRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.roleRepository = roleRepository;
    }


    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }



    public Person getPersonById(Long id) {
//        RLock lock = redissonClient.getLock("getPersonByIdLock");
//        try{
//            lock.lock();
            Optional<Person> optionalPerson = personRepository.findById(id);
            if (optionalPerson.isPresent()) {
                Person person = optionalPerson.get();
//                // Định dạng các giá trị tiền tệ
//                String formattedTotalmoney = person.getFormattedTotalMoney();
//                // Gán các giá trị tiền tệ đã định dạng vào đối tượng Person
//                person.setFormattedTotalMoney(formattedTotalmoney);
                return person;
            } else {
                return null;
            }
//        } finally {
//            lock.unlock();
//        }
    }

//    public void updatePerson(Person person) {
//        if (person != null) {
//            personRepository.save(person);
//        }
//    }


    public Person savePerson(Person person) {
//        RLock lock = redissonClient.getLock("savePersonLock");
//        try{
//            lock.lock();
            return personRepository.save(person);
//        } finally {
//            lock.unlock();
//        }
    }

    public Optional<Person> deleteUser(Long id) {
//        RLock lock = redissonClient.getLock("deleteUserLock");
//        try{
//            lock.lock();
            // Trước tiên, xóa lịch sử giao dịch cho người dùng
            List<TransactionHistory> userTransactionHistory = transactionHistoryRepository.findByPersonId(id);
            transactionHistoryRepository.deleteAll(userTransactionHistory);

            // Trước tiên, xóa lịch sử thay đổi thông tin cho người dùng
            List<UpdateHistory> userUpdateHistory = updateHistoryRepository.findByPersonId(id);
            updateHistoryRepository.deleteAll(userUpdateHistory);

            // Tiếp theo, xóa người dùng
            personRepository.deleteById(id);
            return null;
//        } finally {
//            lock.unlock();
//        }
    }

    public Optional<Account> deleteAccount(Long id) {
//        RLock lock = redissonClient.getLock("deleteAccountLock");
//        try{
//            lock.lock();
            //Xóa lịch sử đăng nhập
            List<LoginHistory> loginHistories = loginHistoryRepository.findByAccountId(id);
            loginHistoryRepository.deleteAll(loginHistories);

            // Tiếp theo, xóa người dùng
            accountRepository.deleteById(id);
            return null;
//        } finally {
//            lock.unlock();
//        }
    }


    public List<Person> getAllPersonsSorted(String sortOrder) {
        if (sortOrder.equalsIgnoreCase("ascId")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("descId")) {
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("descName")) {
            Sort sort = Sort.by(Sort.Direction.DESC, "name");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("ascName")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "name");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("descDob")) {
            Sort sort = Sort.by(Sort.Direction.DESC, "dob");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("ascDob")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "dob");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("descAge")) {
            Sort sort = Sort.by(Sort.Direction.DESC, "age");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("ascAge")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "age");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("ascEmail")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "email");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("descEmail")) {
            Sort sort = Sort.by(Sort.Direction.DESC, "email");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("descPhone")) {
            Sort sort = Sort.by(Sort.Direction.DESC, "phoneNumber");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("ascPhone")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "phoneNumber");
            return personRepository.findAll(sort);
        } else if (sortOrder.equalsIgnoreCase("ascMoney")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "totalMoney");
            return personRepository.findAll(sort);
        } else {
            Sort sort = Sort.by(Sort.Direction.DESC, "totalMoney");
            return personRepository.findAll(sort);
        }
    }


    public ResponseEntity<List<Person>> getPersonsInNegativeBalance() {
        List<Person> personsWithNegativeBalance = personRepository.findByTotalMoneyLessThan(0);
        if (personsWithNegativeBalance.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(personsWithNegativeBalance, HttpStatus.OK);
        }
    }



//    public boolean updatePerson(Long id, Person person) {
//        Person existingPerson = personRepository.findById(id).orElse(null);
//        if (existingPerson != null) {
//            long oldId = existingPerson.getId();
//            String oldName = existingPerson.getName();
//            existingPerson.setName(person.getName());
//            int oldAge = existingPerson.getAge();
//            existingPerson.setAge(person.getAge());
//            String oldEmail = existingPerson.getEmail();
//            existingPerson.setEmail(person.getEmail());
//            String oldPhone = existingPerson.getPhoneNumber();
//            existingPerson.setPhoneNumber(person.getPhoneNumber());
//            personRepository.save(existingPerson);
//
//            UpdateHistory updateHistory = new UpdateHistory();
//            updateHistory.setUpdateId(oldId);
//            updateHistory.setUpdateName(oldName);
//            updateHistory.setUpdateDate(LocalDateTime.now());
//            updateHistory.setUpdateAge(oldAge);
//            updateHistory.setUpdateEmail(oldEmail);
//            updateHistory.setUpdatePhone(oldPhone);
//            updateHistoryRepository.save(updateHistory);
//        }
//        return false;
//    }

//    public boolean updatePerson(Long id, Person updatedPerson) {
//        Person existingPerson = personRepository.findById(id).orElse(null);
//        if (existingPerson != null) {
//            // Lấy giá trị totalMoney hiện tại
//            double currentTotalMoney = existingPerson.getTotalMoney();
//
//            // Cập nhật thông tin chỉ định (ngoại trừ totalMoney)
//            existingPerson.setName(updatedPerson.getName());
//            existingPerson.setAge(updatedPerson.getAge());
//            existingPerson.setEmail(updatedPerson.getEmail());
//            existingPerson.setPhoneNumber(updatedPerson.getPhoneNumber());
//
//            // Đặt lại giá trị totalMoney
//            existingPerson.setTotalMoney(currentTotalMoney);
//
//            // Lưu lại thông tin cập nhật
//            personRepository.save(existingPerson);
//
//            UpdateHistory updateHistory = new UpdateHistory();
//            updateHistory.setUpdateId(existingPerson.getId());
//            updateHistory.setUpdateName(existingPerson.getName());
//            updateHistory.setUpdateDate(LocalDateTime.now());
//            updateHistory.setUpdateAge(existingPerson.getAge());
//            updateHistory.setUpdateEmail(existingPerson.getEmail());
//            updateHistory.setUpdatePhone(existingPerson.getPhoneNumber());
//            updateHistoryRepository.save(updateHistory);
//            return true;
//        }
//        return false;
//    }

//    public void updatePerson(Long id, Person updatedPerson) {
//        Person existingPerson = personRepository.findById(id).orElse(null);
//        if (existingPerson != null) {
//            // Lấy giá trị totalMoney hiện tại
//            double currentTotalMoney = existingPerson.getTotalMoney();
//            // Cập nhật thông tin chỉ định (ngoại trừ totalMoney)
//            existingPerson.setName(updatedPerson.getName());
//            existingPerson.setAge(updatedPerson.getAge());
//            existingPerson.setEmail(updatedPerson.getEmail());
//            existingPerson.setPhoneNumber(updatedPerson.getPhoneNumber());
//            // Đặt lại giá trị totalMoney
//            existingPerson.setTotalMoney(currentTotalMoney);
//            // Lưu lại thông tin cập nhật
//            personRepository.save(existingPerson);
//            UpdateHistory updateHistory = new UpdateHistory();
//            updateHistory.setUpdateId(existingPerson.getId());
//            updateHistory.setUpdateName(existingPerson.getName());
//            updateHistory.setUpdateDate(LocalDateTime.now());
//            updateHistory.setUpdateAge(existingPerson.getAge());
//            updateHistory.setUpdateEmail(existingPerson.getEmail());
//            updateHistory.setUpdatePhone(existingPerson.getPhoneNumber());
//            updateHistoryRepository.save(updateHistory);
//        }
//    }
//
//    public void updatePerson(Long id, Person updatedPerson) {
//        Person existingPerson = personRepository.findById(id).orElse(null);
//        if (existingPerson != null) {
//            // Cập nhật thông tin chỉ định (ngoại trừ totalMoney)
//            existingPerson.setName(updatedPerson.getName());
//            existingPerson.setAge(updatedPerson.getAge());
//            existingPerson.setEmail(updatedPerson.getEmail());
//            existingPerson.setPhoneNumber(updatedPerson.getPhoneNumber());
//            // Lưu lại thông tin cập nhật
//            personRepository.save(existingPerson);
//            // Lưu lại lịch sử cập nhật
//            UpdateHistory updateHistory = new UpdateHistory();
//            updateHistory.setUpdateId(existingPerson.getId());
//            updateHistory.setUpdateName(existingPerson.getName());
//            updateHistory.setUpdateDate(LocalDateTime.now());
//            updateHistory.setUpdateAge(existingPerson.getAge());
//            updateHistory.setUpdateEmail(existingPerson.getEmail());
//            updateHistory.setUpdatePhone(existingPerson.getPhoneNumber());
//            updateHistoryRepository.save(updateHistory);
//        }
//    }


//    public List<UpdateHistory> getAllUpdateHistory() {
//        return updateHistoryRepository.findAll();
//    }


    public void calculateAndSaveTotalMoney(double totalAmount, List<Long> selectedUserIds, String transactionType, String description) {
//        RLock lock = redissonClient.getLock("calculateAndSaveTotalMoneyLock");
//        try{
//            lock.lock();
            double sharedAmount = totalAmount / selectedUserIds.size();
            List<Person> personList = personRepository.findByIdIn(selectedUserIds);

            for (Long selectedUserId : selectedUserIds) {
                Person person = personList.stream()
                        .filter(item -> item.getId().equals(selectedUserId))
                        .findFirst()
                        .orElse(null);
                if (person != null) {
                    double userShare = person.getTotalMoney();
                    double newTotalMoney = transactionType.equals("PAY") ? (userShare - sharedAmount) : (userShare + sharedAmount);
                    person.setTotalMoney(newTotalMoney);
                    person = personRepository.save(person);
                    // Save transaction history
                    TransactionHistory transactionHistory = new TransactionHistory();
                    transactionHistory.setSubTotal(person.getTotalMoney());
                    transactionHistory.setDescription(description);
                    transactionHistory.setTransactionDate(new Date());
                    transactionHistory.setTransactionType(transactionType);
                    transactionHistory.setTransactionAmount(sharedAmount);
                    transactionHistory.setPerson(person);
                    transactionHistoryRepository.save(transactionHistory);
                }
            }
//        } finally {
//            lock.unlock();
//        }
    }

    public List<TransactionHistory> getAllTransactionSortedByDate() {
        Sort sort = Sort.by(Sort.Direction.DESC, "transactionDate");
        return transactionHistoryRepository.findAll(sort);
    }


    public void calculateAndSaveTotalMoney1(double totalAmount,
                                            List<Long> selectedUserIds,
                                            String transactionType,
                                            String description) {
//        RLock lock = redissonClient.getLock("calculateAndSaveTotalMoney1Lock");
//        try{
//            lock.lock();
            List<Person> personList = personRepository.findByIdIn(selectedUserIds);

            for (Long selectedUserId : selectedUserIds) {
                Person person = personList.stream()
                        .filter(item -> item.getId().equals(selectedUserId))
                        .findFirst()
                        .orElse(null);
                if (person != null) {
                    double userShare = person.getTotalMoney();
                    double newTotalMoney = transactionType.equals("PAY") ? (userShare - totalAmount) : (userShare + totalAmount);
                    person.setTotalMoney(newTotalMoney);
                    person = personRepository.save(person);
                    // Save transaction history
                    TransactionHistory transactionHistory = new TransactionHistory();
                    transactionHistory.setSubTotal(person.getTotalMoney());
                    transactionHistory.setDescription(description);
                    transactionHistory.setTransactionDate(new Date());
                    transactionHistory.setTransactionType(transactionType);
                    transactionHistory.setTransactionAmount(totalAmount);
                    transactionHistory.setPerson(person);
                    transactionHistoryRepository.save(transactionHistory);
                }
            }
//        } finally {
//            lock.unlock();
//        }
    }


    public boolean existsByEmailOrPhoneNumber(String email, String phoneNumber) {
        return personRepository.existsByEmailOrPhoneNumber(email, phoneNumber);
    }
    public List<Person> searchByName(String name) {
        return null;
    }

//    public List<Person> searchByName(String name) {
//        if (name == null || name.trim().isEmpty()) {
//            return new ArrayList<>();
//        }
//        List<Person> personList = personRepository.findByNameContaining(name);
//        return personList;
//    }


//    public Person updatePartialPerson(Long id, Person updatedPerson) {
//        Optional<Person> optionalPerson = personRepository.findById(id);
//        if (optionalPerson.isPresent()) {
//            Person person = optionalPerson.get();
//            if (updatedPerson.getName() != null) {
//                person.setName(updatedPerson.getName());
//            }
//            if (updatedPerson.getAge() != 0) {
//                person.setAge(updatedPerson.getAge());
//            }
//            if (updatedPerson.getEmail() != null) {
//                person.setEmail(updatedPerson.getEmail());
//            }
//            if (updatedPerson.getPhoneNumber() != null) {
//                person.setPhoneNumber(updatedPerson.getPhoneNumber());
//            }
//            Person savedPerson = personRepository.save(person);
//            return savedPerson;
//        } else {
//            return null;
//        }
//    }

    public Person updatePartialPerson(Long id, Person updatedPerson) {
//        RLock lock = redissonClient.getLock("updatePartialPerson");
//        try{
//            lock.lock();
            Optional<Person> optionalPerson = personRepository.findById(id);
            if (optionalPerson.isPresent()) {
                Person person = optionalPerson.get();
                // Kiểm tra xem email đã thay đổi và có bị trùng với người dùng khác không
                if (updatedPerson.getEmail() != null && !updatedPerson.getEmail().equals(person.getEmail())) {
                    boolean isUniqueEmail = !personRepository.existsByEmail(updatedPerson.getEmail());
                    if (!isUniqueEmail) {
                        throw new IllegalArgumentException("Email is already in use by another user");
                    }
                }
                // Kiểm tra xem số điện thoại đã thay đổi và có bị trùng với người dùng khác không
                if (updatedPerson.getPhoneNumber() != null && !updatedPerson.getPhoneNumber().equals(person.getPhoneNumber())) {
                    boolean isUniquePhoneNumber = !personRepository.existsByPhoneNumber(updatedPerson.getPhoneNumber());
                    if (!isUniquePhoneNumber) {
                        throw new IllegalArgumentException("Phone number is already in use by another user");
                    }
                }
                // Lưu lại thông tin cũ trước khi cập nhật
                Long oldId = person.getId();
                String oldName = person.getName();
                int oldAge = person.getAge();
                String oldEmail = person.getEmail();
                String oldPhoneNumber = person.getPhoneNumber();
                LocalDate oldDob = person.getDob();
                // Cập nhật thông tin người dùng
                if (updatedPerson.getName() != null) {
                    person.setName(updatedPerson.getName());
                }
                if (updatedPerson.getAge() != 0) {
                    person.setAge(updatedPerson.getAge());
                }
                if (updatedPerson.getEmail() != null) {
                    person.setEmail(updatedPerson.getEmail());
                }
                if (updatedPerson.getPhoneNumber() != null) {
                    person.setPhoneNumber(updatedPerson.getPhoneNumber());
                }
                if (updatedPerson.getDob() != null) {
                    person.setDob(updatedPerson.getDob());
                }
                Person savedPerson = personRepository.save(person);
                // Lưu lịch sử cập nhật
                UpdateHistory updateHistory = new UpdateHistory();
                updateHistory.setUpdateName(oldName);
                updateHistory.setUpdateDate(LocalDateTime.now());
                updateHistory.setUpdateAge(oldAge);
                updateHistory.setUpdateEmail(oldEmail);
                updateHistory.setUpdatePhone(oldPhoneNumber);
                updateHistory.setUpdateDob(oldDob);
                updateHistory.setPerson(savedPerson);
                updateHistoryRepository.save(updateHistory);
                return savedPerson;
            } else {
                return null;
            }
//        } finally {
//            lock.unlock();
//        }

    }


    public boolean existsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }


    public boolean existsByPhoneNumber(String phoneNumber) {
        return personRepository.existsByPhoneNumber(phoneNumber);
    }


    public void calculateAndSaveTotalMoney(double totalAmount,
                                           List<Long> selectedUserIds,
                                           List<Double> individualAmounts,
                                           String transactionType,
                                           String description) {
//        RLock lock = redissonClient.getLock("calculateAndSaveTotalMoneyLock");
//        try {
//            lock.lock();
            if (individualAmounts.size() != selectedUserIds.size()) {
                throw new IllegalArgumentException("Number of individual amounts does not match the number of selected users.");
            }
            double sumIndividualAmounts = individualAmounts.stream().mapToDouble(Double::doubleValue).sum();
            if (Math.abs(sumIndividualAmounts - totalAmount) > 0) {
                throw new IllegalArgumentException("Sum of individual amounts does not match the total amount.");
            }
            List<Person> personList = personRepository.findByIdIn(selectedUserIds);
            for (int i = 0; i < selectedUserIds.size(); i++) {
                Long userId = selectedUserIds.get(i);
                Double individualAmount = individualAmounts.get(i);
                Person person = personList.stream()
                        .filter(item -> item.getId().equals(userId))
                        .findFirst()
                        .orElse(null);
                if (person != null) {
                    double userShare = person.getTotalMoney();
                    double newTotalMoney = transactionType.equals("PAY") ? (userShare - individualAmount) : (userShare + individualAmount);
                    person.setTotalMoney(newTotalMoney);
                    person = personRepository.save(person);
                    // Save transaction history
                    TransactionHistory transactionHistory = new TransactionHistory();
                    transactionHistory.setSubTotal(person.getTotalMoney());
                    transactionHistory.setDescription(description);
                    transactionHistory.setTransactionDate(new Date());
                    transactionHistory.setTransactionType(transactionType);
                    transactionHistory.setTransactionAmount(individualAmount);
//                    transactionHistory.setAccount(account);
                    transactionHistory.setPerson(person);
                    transactionHistoryRepository.save(transactionHistory);
                }
            }
//        } finally {
//            lock.unlock();
//        }

    }


//    public Account registerUser(String userName, String password, String confirmPassword) {
//        RLock lock = redissonClient.getLock("registerLock");
//        try {
//            lock.lock(); // Wait until the lock is acquired
//            Account account = accountRepository.findByUserName(userName).get();
//            if (account != null) {
//                throw new RuntimeException("Username already exists");
//            }
//            if (!password.equals(confirmPassword)) {
//                throw new RuntimeException("Confirm password does not match password");
//            }
//            account = new Account();
//            account.setUserName(userName);
//            account.setPassword(md5Hash(password));
//            account.setConfirmPassword(md5Hash(confirmPassword));
//            account.setCreateDate(LocalDate.now());
//            return accountRepository.save(account);
//        } finally {
//            lock.unlock();
//        }
//    }
    
    public ResponseEntity<?> registerUser(AuthRequest request) {
        // Check if the email is already registered
        if (accountRepository.existsByUserName(request.getUserName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already registered");
        }

        // Check if password matches confirm password
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Confirm password does not match password");
        }

        // Create a new user
        Account account = new Account();
        account.setUserName(request.getUserName());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setCreateDate(LocalDate.now());
        accountRepository.save(account);

        // Generate an access token for the new user
        String accessToken = jwtTokenUtil.generateAccessToken(account);
        AuthResponse response = new AuthResponse(account.getUsername(), accessToken);
        return ResponseEntity.ok(response);
    }

//    public Account loginUser(String userName, String password) {
//        RLock lock = redissonClient.getLock("loginLock");
//        try {
//            lock.lock(); // Chờ cho đến khi nhận được lock
//            Account account = accountRepository.findByUserName(userName);
//            if (account != null && account.getPassword().equals(md5Hash(password))) {
//                LoginHistory loginHistory = new LoginHistory();
//                loginHistory.setLoginDate(LocalDateTime.now());
//                loginHistory.setUserNameHis(account.getUserName());
//                loginHistory.setAccount(account);
//                loginHistoryRepository.save(loginHistory);
//                return account;
//            }
//            return null;
//        } finally {
//            lock.unlock();
//        }
//    }

    public ResponseEntity<?> loginUser(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
            );
            Account account = (Account) authentication.getPrincipal();
            String accessToken = jwtTokenUtil.generateAccessToken(account);
            AuthResponse response = new AuthResponse(account.getUsername(), accessToken);

                LoginHistory loginHistory = new LoginHistory();
                loginHistory.setLoginDate(LocalDateTime.now());
                loginHistory.setUserNameHis(account.getUsername());
                loginHistory.setAccount(account);
                loginHistoryRepository.save(loginHistory);

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

//    public Account changePassword(String userName, String currentPassword, String newPassword, String confirmPassword) {
//        RLock lock = redissonClient.getLock("changePasswordLock");
//        try {
//            lock.lock(); // Chờ cho đến khi nhận được lock
//            Account account = accountRepository.findByUserName(userName);
//            if (account != null && account.getPassword().equals(md5Hash(currentPassword))) {
//                String hashedNewPassword = md5Hash(newPassword);
//                String hashedConfirmPassword = md5Hash(confirmPassword);
//                if(hashedNewPassword.equals(hashedConfirmPassword)){
//                    account.setPassword(hashedNewPassword);
//                    accountRepository.save(account);
//                } else{
//                    throw new RuntimeException("Confirm password does not equal with new password");
//                }
//
//            } else {
//                throw new RuntimeException("Invalid current password");
//            }
//            return accountRepository.save(account);
//        } finally {
//            lock.unlock();
//        }
//    }

    public ResponseEntity<?> changePassword(AuthRequest request) {
        Optional<Account> optionalAccount = accountRepository.findByUserName(request.getUserName());

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();

            if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect current password");
            }

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Confirm password does not match new password");
            }

            account.setPassword(passwordEncoder.encode(request.getNewPassword()));
            accountRepository.save(account);

            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }



//    public Account loginUser(String userName, String password) {
//        RLock lock = redissonClient.getLock("loginLock");
//        try {
//            lock.lock(); // Chờ cho đến khi nhận được lock
//            Account account = accountRepository.findByUserName(userName);
//            if (account == null) {
//                throw new RuntimeException("Username không tồn tại");
//            }
//
//            if (!account.getPassword().equals(md5Hash(password))) {
//                throw new RuntimeException("Mật khẩu sai");
//            }
//
//            return account;
//        } finally {
//            lock.unlock();
//        }
//    }

    public List<Account> getAllLoginInfor() {
//        RLock lock = redissonClient.getLock("loginInforLock");
//        try {
//            lock.lock();
            return accountRepository.findAll();
//        } finally {
//            lock.unlock();
//        }
    }


    public List<TransactionHistory> getTransactionById(Long id) {
//        RLock lock = redissonClient.getLock("getTransactionById");
//        try {
//            lock.lock();
            List<TransactionHistory> transactionHistories = transactionHistoryRepository.findAllByPersonId(id);
            return transactionHistories;
//        } finally {
//            lock.unlock();
//        }
    }

    public ResponseEntity<?> assignRoleToUser(Long userId, Long roleId) {
        Account account = accountRepository.findById(userId).orElse(null);
        Role role = roleRepository.findById(Math.toIntExact(roleId)).orElse(null);

        if (account == null || role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Role not found");
        }

        account.getRoles().add(role);
        accountRepository.save(account);

        return ResponseEntity.ok("Role assigned successfully");
    }




//    public Person getPersonById(Long id) {
//        RLock lock = redissonClient.getLock("getPersonByIdLock");
//        try{
//            lock.lock();
//            Optional<Person> optionalPerson = personRepository.findById(id);
//            if (optionalPerson.isPresent()) {
//                Person person = optionalPerson.get();
//                // Định dạng các giá trị tiền tệ
//                String formattedTotalmoney = person.getFormattedTotalMoney();
//                // Gán các giá trị tiền tệ đã định dạng vào đối tượng Person
//                person.setFormattedTotalMoney(formattedTotalmoney);
//                return person;
//            } else {
//                return null;
//            }
//        } finally {
//            lock.unlock();
//        }
//    }

}

//    public Login registerUser(String userName, String password, String confirmPassword) {
//        RLock lock = redissonClient.getLock("registerLock");
//        try {
//            lock.lock(); // Chờ cho đến khi nhận được lock
//            // Check if the user already exists
//            Login login = loginRepository.findByUserName(userName);
//            if (login != null) {
//                throw new RuntimeException("Username already exists");
//            }
//            String hashedPassword = md5Hash(login.getPassword());
//            String hashedConfirmPassword = md5Hash(login.getConfirmPassword());
//            if (hashedPassword.equals(hashedConfirmPassword)){
//                login.setCreateDate(LocalDate.from(LocalDateTime.now()));
//                return loginRepository.save(login);
//            } else {
//                throw new RuntimeException("Confirm password does not equal with password");
//            }
//        } finally {
//            lock.unlock();
//        }
//    }


//    public Person updatePerson(Long id, Person updatedPerson) {
//        Person person = getPersonById(id);
//        if (person != null) {
//            person.setName(updatedPerson.getName());
//            person.setAge(updatedPerson.getAge());
//            person.setEmail(updatedPerson.getEmail());
//            person.setPhonenumber(updatedPerson.getPhonenumber());
////            person.setAddmoney(updatedPerson.getAddmoney());
////            person.setPaymoney(updatedPerson.getPaymoney());
//            personRepository.save(person);
//        }
//        return person;
//    }

//    public ResponseEntity<Person> getPersonById(@PathVariable("id") long id) {
//        Optional<Person> optionalPerson = personRepository.findById(id);
//        if (optionalPerson.isPresent()) {
//            Person person = optionalPerson.get();
//
//            // Định dạng các giá trị tiền tệ
//            String formattedTotalmoney = person.getFormattedTotalmoney();
//            String formattedAddmoney = person.getFormattedAddmoney();
//            String formattedPaymoney = person.getFormattedPaymoney();
//
//            // Gán các giá trị tiền tệ đã định dạng vào đối tượng Person
//            person.setFormattedTotalmoney(formattedTotalmoney);
//            person.setFormattedAddmoney(formattedAddmoney);
//            person.setFormattedPaymoney(formattedPaymoney);
//
//            return new ResponseEntity<>(person, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }


//    public ResponseEntity<Void> deletePerson(@PathVariable("id") long id) {
//        Optional<Person> optionalPerson = personRepository.findById(id);
//        if (optionalPerson.isPresent()) {
//            personRepository.deleteById(id);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }


//    @PostMapping("/{id}/calculate/totalmoneyadd")
//    public ResponseEntity<Void> calculateTotalMoneyAdd(@PathVariable long id) {
//        Optional<Person> optionalPerson = personRepository.findById(id);
//        if (optionalPerson.isPresent()) {
//            Person person = optionalPerson.get();
//            double totalMoney = person.getTotalmoney();
//            double addMoney = person.getAddmoney();
//            double calculatedMoney = totalMoney + addMoney;
//            // Cập nhật totalmoney với giá trị calculatedMoney
//            person.setTotalmoney(calculatedMoney);
//            // Lưu đối tượng Person đã cập nhật vào cơ sở dữ liệu
//            personRepository.save(person);
////            TransactionHistory transactionHistory = new TransactionHistory(person, "Add Money", addMoney);
////            transactionHistoryRepository.save(transactionHistory);
//            // Thêm thông tin vào bảng lịch sử giao dịch (transaction_history)
//            TransactionHistory transactionHistory =  TransactionHistory.builder()
//                    .subTotal(calculatedMoney)
//                    .person(person)
//                    .description("NẠP TIỀN")
//                    .transactionType("0").transactionAmount(addMoney).build();
//            transactionHistoryRepository.save(transactionHistory);
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }


//    @PostMapping("/{id}/calculate/totalmoneypay")
//    public ResponseEntity<Void> calculateTotalMoneyPay(@PathVariable long id) {
//        Optional<Person> optionalPerson = personRepository.findById(id);
//        if (optionalPerson.isPresent()) {
//            Person person = optionalPerson.get();
//            double totalMoney = person.getTotalmoney();
//            double payMoney = person.getPaymoney();
//            double calculatedMoney = totalMoney - payMoney;
//
//            // Cập nhật totalmoney với giá trị calculatedMoney
//            person.setTotalmoney(calculatedMoney);
//
//            personRepository.save(person); // Lưu đối tượng đã cập nhật vào cơ sở dữ liệu
//
//            // Thêm thông tin vào bảng lịch sử giao dịch (transaction_history)
//            TransactionHistory transactionHistory =  TransactionHistory.builder()
//                    .subTotal(calculatedMoney)
//                    .person(person)
//                    .description("TRỪ TIỀN")
//                    .transactionType("1")
//                    .transactionAmount(payMoney).build();
//            transactionHistoryRepository.save(transactionHistory);
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }


//    @PatchMapping("/{id}")
//    public ResponseEntity<Person> updateAddAndPayMoney(@PathVariable("id") long id, @RequestBody Person updatedPerson) {
//        Optional<Person> optionalPerson = personRepository.findById(id);
//        if (optionalPerson.isPresent()) {
//            Person person = optionalPerson.get();
//            // Update only the non-null properties
//            if (updatedPerson.getName() != null) {
//                person.setName(updatedPerson.getName());
//            }
//            if (updatedPerson.getTotalmoney() != 0) {
//                person.setTotalmoney(updatedPerson.getTotalmoney());
//            }
//            if (updatedPerson.getAddmoney() != 0) {
//                person.setAddmoney(updatedPerson.getAddmoney());
//            }
//            if (updatedPerson.getPaymoney() != 0) {
//                person.setPaymoney(updatedPerson.getPaymoney());
//            }
//            Person savedPerson = personRepository.save(person);
//            return new ResponseEntity<>(savedPerson, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }


//    public void calculateAndSaveTotalMoney(double totalAmount, List<Long> selectedUserIds) {
//
//        double sharedAmount = totalAmount / selectedUserIds.size();
//        for (Long userId : selectedUserIds) {
//            Person person = personRepository.findById(userId).orElse(null);
//            if (person != null) {
//                double userShare = person.getTotalmoney();
//                person.setTotalmoney(userShare - sharedAmount);
//                personRepository.save(person);
//            }
//        }
//    }


//    public void calculateAndSaveTotalMoney1(double totalAmount, List<Long> selectedUserIds) {
//
//        double sharedAmount = totalAmount / selectedUserIds.size();
//        for (Long userId : selectedUserIds) {
//            Person person = personRepository.findById(userId).orElse(null);
//            if (person != null) {
//                double userShare = person.getTotalmoney();
//                person.setTotalmoney(userShare + sharedAmount);
//                personRepository.save(person);
//            }
//        }
//    }


//    public void calculateAndSaveTotalMoney2(double totalAmount, List<Long> selectedUserIds) {
//
//        for (Long userId : selectedUserIds) {
//            Person person = personRepository.findById(userId).orElse(null);
//            if (person != null) {
//                double userShare = person.getTotalmoney();
//                person.setTotalmoney(userShare - totalAmount);
//                personRepository.save(person);
//            }
//        }
//    }


//    public void calculateAndSaveTotalMoney3(double totalAmount, List<Long> selectedUserIds) {
//
//        for (Long userId : selectedUserIds) {
//            Person person = personRepository.findById(userId).orElse(null);
//            if (person != null) {
//                double userShare = person.getTotalmoney();
//                person.setTotalmoney(userShare + totalAmount);
//                personRepository.save(person);
//            }
//        }
//    }

//    public void calculateAndSaveTotalMoney1(double totalAmount, List<Long> selectedUserIds) {
//        double sharedAmount = totalAmount / selectedUserIds.size();
//        for (Long userId : selectedUserIds) {
//            Person person = personRepository.findById(userId).orElse(null);
//            if (person != null) {
//                double userShare = person.getTotalMoney();
//                person.setTotalMoney(userShare + sharedAmount);
//                personRepository.save(person);
//                // Save transaction history
//                TransactionHistory transactionHistory = new TransactionHistory();
//                transactionHistory.setTransactionDate(new Date());
//                transactionHistory.setTransactionType("ADD");
//                transactionHistory.setTransactionAmount(sharedAmount);
//                transactionHistory.setPerson(person);
//                transactionHistoryRepository.save(transactionHistory);
//            }
//        }
//    }

//    //Trừ trung bình
//    public void calculateAndSaveTotalMoney(double totalAmount, List<Long> selectedUserIds) {
//        double sharedAmount = totalAmount / selectedUserIds.size();
//        List<Person> personList = personRepository.findByIdIn(selectedUserIds);
//        for (Long userId : selectedUserIds) {
//            Person person = personList.stream()
//                    .filter(item -> item.getId().equals(userId))
//                    .findFirst()
//                    .orElse(null);
//            if (person != null) {
//                double userShare = person.getTotalMoney();
//                person.setTotalMoney(userShare - sharedAmount);
//                person = personRepository.save(person);
//                // Save transaction history
//                TransactionHistory transactionHistory = new TransactionHistory();
//                transactionHistory.setTransactionDate(new Date());
//                transactionHistory.setTransactionType("PAY");
//                transactionHistory.setTransactionAmount(sharedAmount);
//                transactionHistory.setPerson(person);
//                transactionHistoryRepository.save(transactionHistory);
//            }
//        }
//    }
//
//    //Cộng trung bình
//    public void calculateAndSaveTotalMoney1(double totalAmount, List<Long> selectedUserIds) {
//        double sharedAmount = totalAmount / selectedUserIds.size();
//        List<Person> personList = personRepository.findByIdIn(selectedUserIds);
//        for (Long userId : selectedUserIds) {
//            Person person = personList.stream()
//                    .filter(item -> item.getId().equals(userId))
//                    .findFirst()
//                    .orElse(null);
//            if (person != null) {
//                double userShare = person.getTotalMoney();
//                person.setTotalMoney(userShare + sharedAmount);
//                personRepository.save(person);
//                // Save transaction history
//                TransactionHistory transactionHistory = new TransactionHistory();
//                transactionHistory.setTransactionDate(new Date());
//                transactionHistory.setTransactionType("ADD");
//                transactionHistory.setTransactionAmount(sharedAmount);
//                transactionHistory.setPerson(person);
//                transactionHistoryRepository.save(transactionHistory);
//            }
//        }
//    }
//
//    //Trừ cá nhân
//    public void calculateAndSaveTotalMoney2(double totalAmount, List<Long> selectedUserIds) {
//        List<Person> personList = personRepository.findByIdIn(selectedUserIds);
//        for (Long userId : selectedUserIds) {
//            Person person = personList.stream()
//                    .filter(item -> item.getId().equals(userId))
//                    .findFirst()
//                    .orElse(null);
//            if (person != null) {
//                double userShare = person.getTotalMoney();
//                person.setTotalMoney(userShare - totalAmount);
//                personRepository.save(person);
//
//                // Save transaction history
//                TransactionHistory transactionHistory = new TransactionHistory();
//                transactionHistory.setTransactionDate(new Date());
//                transactionHistory.setTransactionType("PAY");
//                transactionHistory.setTransactionAmount(totalAmount);
//                transactionHistory.setPerson(person);
//                transactionHistoryRepository.save(transactionHistory);
//            }
//        }
//    }
//
//    //Cộng cá nhân
//    public void calculateAndSaveTotalMoney3(double totalAmount, List<Long> selectedUserIds) {
//        List<Person> personList = personRepository.findByIdIn(selectedUserIds);
//        for (Long userId : selectedUserIds) {
//            Person person = personList.stream()
//                    .filter(item -> item.getId().equals(userId))
//                    .findFirst()
//                    .orElse(null);
//            if (person != null) {
//                double userShare = person.getTotalMoney();
//                person.setTotalMoney(userShare + totalAmount);
//                personRepository.save(person);
//                // Save transaction history
//                TransactionHistory transactionHistory = new TransactionHistory();
//                transactionHistory.setTransactionDate(new Date());
//                transactionHistory.setTransactionType("ADD");
//                transactionHistory.setTransactionAmount(totalAmount);
//                transactionHistory.setPerson(person);
//                transactionHistoryRepository.save(transactionHistory);
//            }
//        }
//    }


//    public ResponseEntity<List<Person>> getAllPersonsSortedAscTotalmoney() {
//        Sort sort = Sort.by(Sort.Direction.ASC, "totalmoney"); // Sắp xếp tăng dần theo trường "Totalmoney"
//        List<Person> entities = personRepository.findAll(sort);
//        return new ResponseEntity<>(entities, HttpStatus.OK);
//    }

//    public ResponseEntity<List<Person>> getAllPersonsSortedDescTotalmoney() {
//        Sort sort = Sort.by(Sort.Direction.DESC, "totalmoney"); // Sắp xếp giảm dần theo trường "Totalmoney"
//        List<Person> entities = personRepository.findAll(sort);
//        return new ResponseEntity<>(entities, HttpStatus.OK);
//    }


