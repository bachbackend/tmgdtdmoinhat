package com.example.controller;

import com.example.dto.AuthRequest;
import com.example.jpa.entity.*;
import com.example.jpa.repository.*;
import com.example.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller
@CrossOrigin
public class PersonController {

    private final PersonService personService;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UpdateHistoryRepository updateHistoryRepository;
    private final PersonRepository personRepository;
    private final AccountRepository accountRepository;

    private final LoginHistoryRepository loginHistoryRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public PersonController(PersonService personService,
                            TransactionHistoryRepository transactionHistoryRepository,
                            UpdateHistoryRepository updateHistoryRepository,
                            PersonRepository personRepository,
                            AccountRepository accountRepository,
                            LoginHistoryRepository loginHistoryRepository,
                            RoleRepository roleRepository) {
        this.personService = personService;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.updateHistoryRepository = updateHistoryRepository;
        this.personRepository = personRepository;
        this.accountRepository = accountRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.roleRepository = roleRepository;
    }

//    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @CrossOrigin(origins = "*")
    @PostMapping("/persons/add")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<String> addUser(@RequestBody Person person) {
        // Kiểm tra nếu đã tồn tại người dùng khác có cùng email hoặc phonenumber
        if (personService.existsByEmailOrPhoneNumber(person.getEmail(), person.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Email or phone number already exists.");
        }
        personService.savePerson(person);
        return ResponseEntity.ok("User added successfully.");
    }

//    @CrossOrigin(origins = "*")
//    @PostMapping("persons/login")
//    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
//        return personService.loginUser(request);
//    }

    @CrossOrigin(origins = "*")
    @GetMapping("/persons")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<Person>> getAllPersons(@RequestParam(value = "sortOrder", required = false) String sortOrder) {
        List<Person> persons;
        if (sortOrder == null) {
            persons = personService.getAllPersons();
        } else {
            persons = personService.getAllPersonsSorted(sortOrder);
        }
        return ResponseEntity.ok(persons);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/persons/negativebalance")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<Person>> getPersonsInNegativeBalance() {
        return personService.getPersonsInNegativeBalance();
    }

//    @GetMapping("/persons/sorted")
//    public ResponseEntity<List<Person>> getAllPersonsSorted(@RequestParam("sortOrder") String sortOrder) {
//        List<Person> persons = personService.getAllPersonsSorted(sortOrder);
//        return ResponseEntity.ok(persons);
//    }
    @CrossOrigin(origins = "*")
    @GetMapping("/persons/search")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<Person>> searchPersons(@RequestParam("searchOrder") String searchOrder) {
        List<Person> searchResults = personService.searchByName(searchOrder);
        return ResponseEntity.ok(searchResults);
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/persons/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Person> getPersonById(@PathVariable("id") Long id) {
        Person person = personService.getPersonById(id);
        if (person == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/persons/transactionHistory/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<TransactionHistory>> getTransactionHistoryById(@PathVariable("id") Long id) {
        List<TransactionHistory> transactionHistories = personService.getTransactionById(id);
        if (transactionHistories == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transactionHistories);
    }

//    @CrossOrigin(origins = "*")
//    @GetMapping("/persons/transactionHistory")
//    public ResponseEntity<List<TransactionHistory>> getTransactionHistory () {
//        List<TransactionHistory> transactionHistoryList = personService.getAllTransactionSortedByDate();
//        return ResponseEntity.ok(transactionHistoryList);
//    }

//    @CrossOrigin(origins = "*")
//    @DeleteMapping("/persons/{id}")
//    public ResponseEntity<Void> deletePerson(@PathVariable("id") long id) {
//        Optional<Person> optionalPerson = personRepository.findById(id);
//        if (optionalPerson.isPresent()) {
//            personRepository.deleteById(id);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/persons/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Optional<Person> personOptional = personRepository.findById(id);

        if (personOptional.isPresent()) {
            Person person = personOptional.get();

            // Xóa lịch sử giao dịch cho người dùng
            List<TransactionHistory> userTransactionHistory = transactionHistoryRepository.findByPersonId(id);
            transactionHistoryRepository.deleteAll(userTransactionHistory);

            // Xóa lịch sử thay đổi thông tin cho người dùng
            List<UpdateHistory> userUpdateHistory = updateHistoryRepository.findByPersonId(id);
            updateHistoryRepository.deleteAll(userUpdateHistory);

            // Xóa người dùng
            personRepository.deleteById(id);

            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/persons/account/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            // Xóa lịch sử đăng nhập
            List<LoginHistory> loginHistories = loginHistoryRepository.findByAccountId(id);
            loginHistoryRepository.deleteAll(loginHistories);

            // Xóa tài khoản
            accountRepository.deleteById(id);

            return ResponseEntity.ok("Account deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    public ResponseEntity<String> deleteUserAndTransactionHistory(@PathVariable("id") long id) {
//        try {
//            // Xóa lịch sử giao dịch có trùng id
//            List<TransactionHistory> transactionHistories = transactionHistoryRepository.findByPersonId(id);
//            if (!transactionHistories.isEmpty()) {
//                transactionHistoryRepository.deleteAll(transactionHistories);
//            }
//
//            // Xóa lịch sử thay đổi thông tin có trùng id
//            List<UpdateHistory> updateHistories = updateHistoryRepository.findByPersonId(id);
//            if (!updateHistories.isEmpty()) {
//                updateHistoryRepository.deleteAll(updateHistories);
//            }
//            // Xóa người dùng
//            personRepository.deleteById(id);
//
//            return ResponseEntity.ok("User and related data deleted successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting user: " + e.getMessage());
//        }
//    }

//    @CrossOrigin(origins = "http://127.0.0.1:5500")
//    @PutMapping("/persons/{id}")
//    public ResponseEntity<Person> updatePerson(@PathVariable("id") Long id, @RequestBody Person updatedPerson) {
//        Optional<Person> optionalPerson = personRepository.findById(id);
//        if (optionalPerson.isPresent()) {
//            Person person = optionalPerson.get();
//            updatedPerson.setId(person.getId());
//            Person savedPerson = personRepository.save(updatedPerson);
//            return new ResponseEntity<>(savedPerson, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }


    @CrossOrigin(origins = "*")
    @PatchMapping("/persons/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<Person> updatePartialPersonEndpoint(@PathVariable("id") long id,
                                                              @RequestBody Person updatedPerson) {
        Person updated = personService.updatePartialPerson(id, updatedPerson);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/persons/calculate")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<String> processTransaction(
            @RequestParam double totalAmount,
            @RequestParam List<Long> selectedUserIds,
            @RequestParam String transactionType,
            @RequestParam String description) {
        try {

            if (transactionType.equalsIgnoreCase("subtract_avg")) {
                personService.calculateAndSaveTotalMoney(totalAmount, selectedUserIds, "PAY", description);
            } else if (transactionType.equalsIgnoreCase("add_avg")) {
                personService.calculateAndSaveTotalMoney(totalAmount, selectedUserIds, "ADD", description);
            } else if (transactionType.equalsIgnoreCase("subtract_individual")) {
                personService.calculateAndSaveTotalMoney1(totalAmount, selectedUserIds, "PAY", description);
            } else if (transactionType.equalsIgnoreCase("add_individual")) {
                personService.calculateAndSaveTotalMoney1(totalAmount, selectedUserIds, "ADD", description);
            } else {
                return ResponseEntity.badRequest().body("Invalid transaction type.");
            }
            return ResponseEntity.ok("Transaction processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during processing.");
        }
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/persons/calculateIndividual")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<String> processTransaction(
            @RequestParam double totalAmount,
            @RequestParam List<Long> selectedUserIds,
            @RequestParam List<Double> individualAmounts,
            @RequestParam String transactionType,
            @RequestParam String description) {
        try {
            personService.calculateAndSaveTotalMoney(totalAmount, selectedUserIds, individualAmounts, transactionType, description);
            return ResponseEntity.ok("Transaction processed successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during processing.");
        }
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/persons/transactionHistory")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<TransactionHistory>> getTransactionHistory() {
        List<TransactionHistory> transactionHistoryList = personService.getAllTransactionSortedByDate();
        return ResponseEntity.ok(transactionHistoryList);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/persons/updateHistory")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<UpdateHistory>> getUpdateHistory() {
        List<UpdateHistory> updateHistory = updateHistoryRepository.findAll();
        return ResponseEntity.ok(updateHistory);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/persons/loginHistory")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<LoginHistory>> getLoginHistory() {
        List<LoginHistory> loginHistories = loginHistoryRepository.findAll();
        return ResponseEntity.ok(loginHistories);
    }

//    @CrossOrigin(origins = "http://127.0.0.1:5500")
//    @PostMapping("/persons/register")
//    public ResponseEntity<String> register(@RequestBody Login login) {
//        if (loginRepository.existsByUserName(login.getUserName())) {
//            return ResponseEntity.badRequest().body("Username already exists.");
//        }
//        loginRepository.save(login);
//        return ResponseEntity.status(HttpStatus.CREATED).body("Register successfully!");
//    }
//
//    @CrossOrigin(origins = "http://127.0.0.1:5500")
//    @PostMapping("/persons/login")
//    public ResponseEntity<String> login(@RequestBody Login login) {
//        Login login1 = loginRepository.findByUserName(login.getUserName());
//        if (login1 != null && login1.getPassword().equals(login.getPassword())) {
//            return ResponseEntity.status(HttpStatus.OK).body("Đăng nhập thành công!");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Đăng nhập thất bại. Vui lòng kiểm tra lại tài khoản hoặc mật khẩu.");
//        }
//    }

//    @CrossOrigin(origins = "*")
//    @PostMapping("persons/register")
//    public ResponseEntity<String> registerUser(@RequestBody Account account) {
//        try {
//            personService.registerUser(account.getUserName(), account.getPassword(), account.getConfirmPassword());
//            return ResponseEntity.ok("User registered successfully");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
    @CrossOrigin(origins = "*")
    @PostMapping("persons/register")
    public ResponseEntity<?> register(@RequestBody @Valid AuthRequest request) {
        return personService.registerUser(request);
    }

//    @CrossOrigin(origins = "*")
//    @PostMapping("persons/login")
//    public ResponseEntity<Account> loginUser(@RequestBody Account account) {
//        Account loggedInUser = personService.loginUser(account.getUserName(), account.getPassword());
//        if (loggedInUser != null) {
//            return ResponseEntity.ok(loggedInUser);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }
    @CrossOrigin(origins = "*")
    @PostMapping("persons/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        return personService.loginUser(request);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/persons/loginInfor")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<List<Account>> getAllLoginInfor() {
        List<Account> accounts = personService.getAllLoginInfor();
        return ResponseEntity.ok(accounts);
    }

//    @CrossOrigin(origins = "*")
//    @PostMapping("/persons/changePassword")
//    public ResponseEntity<String> changePassword(@RequestBody Account account) {
//        try {
//            personService.changePassword(account.getUserName(), account.getCurrentPassword(), account.getNewPassword(), account.getConfirmPassword());
//            return ResponseEntity.ok("Password changed successfully");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
    @CrossOrigin(origins = "*")
    @PostMapping("persons/changePassword")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
        public ResponseEntity<?> changePassword(@RequestBody @Valid AuthRequest request) {
            return personService.changePassword(request);
    }

//    @CrossOrigin(origins = "*")
//    @PostMapping("/{userId}/roles/{roleId}")
//    @RolesAllowed("ROLE_ADMIN")
//    public ResponseEntity<?> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
//        return personService.assignRoleToUser(userId, roleId);
//    }
    @CrossOrigin(origins = "*")
    @PostMapping("/{userId}/roles/{roleId}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<?> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        return personService.assignRoleToUser(userId, roleId);
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/roles")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<Role> create(@RequestBody @Valid Role role) {
        Role savedRole = roleRepository.save(role);
        URI roleURI = URI.create("/roles/" + savedRole.getId());
        return ResponseEntity.created(roleURI).body(savedRole);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/roles")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }



//    @CrossOrigin(origins = "http://127.0.0.1:5500")
//    @PostMapping("/persons/register")
//    public ResponseEntity<String> register(@Valid @RequestBody Login login, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            // Xử lý lỗi validation
//            return ResponseEntity.badRequest().body("Lỗi đăng ký");
//        }
//
//        login.setPassword(PasswordEncoder.encodePassword(login.getPassword())); // Hash mật khẩu
//        loginRepository.save(login);
//        return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công!");
//    }
//
//    @CrossOrigin(origins = "http://127.0.0.1:5500")
//    @PostMapping("/persons/login")
//    public ResponseEntity<String> login(@RequestBody Login login) {
//        Login storedLogin = loginRepository.findByUserName(login.getUserName());
//        if (storedLogin != null && PasswordEncoder.matches(login.getPassword(), storedLogin.getPassword())) {
//            return ResponseEntity.status(HttpStatus.OK).body("Đăng nhập thành công!");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Đăng nhập thất bại. Vui lòng kiểm tra lại tài khoản hoặc mật khẩu.");
//        }
//    }

}


//    @PostMapping("/process")
//    public String processForm(@RequestParam double totalAmount, @RequestParam List<Long> selectedUserIds, @RequestParam String transactionType, @RequestParam String description) {
//        if (transactionType.equals("subtract_avg")) {
//            personService.calculateAndSaveTotalMoney(totalAmount, selectedUserIds, "PAY", description);
//        } else if (transactionType.equals("add_avg")) {
//            personService.calculateAndSaveTotalMoney(totalAmount, selectedUserIds, "ADD", description);
//        } else if (transactionType.equals("subtract_individual")) {
//            personService.calculateAndSaveTotalMoney1(totalAmount, selectedUserIds, "PAY", description);
//        } else if (transactionType.equals("add_individual")) {
//            personService.calculateAndSaveTotalMoney1(totalAmount, selectedUserIds, "ADD", description);
//        }
//        return "redirect:/getall";
//    }

//    @GetMapping("/getall/{id}/delete")
//    public String deleteUser(@PathVariable Long id) {
//        personService.deleteUser(id);
//        return "redirect:/getall";
//
//    }


//    @GetMapping("/getbyid")
//    public String getPersonById(@RequestParam("id") Long id, Model model) {
//        Person person = personService.getPersonById(id);
//        if (person == null) {
//            return "person_not_found";
//        }
//        model.addAttribute("person", person);
//        return "get_person_by_id";
//    }

//    @PostMapping("/personsSearch")
//    public String searchPersons(@ModelAttribute("searchForm") SearchForm searchForm, Model model) {
//        String name = searchForm.getName();
//        List<Person> searchResults = personService.searchByName(name);
//        model.addAttribute("searchResults", searchResults);
//        model.addAttribute("searchForm", searchForm);
//        return "get_all_persons";
//    }

//    @GetMapping("/personsSort")
//    public List<Person> getAllPersonsSorted(SortForm sortForm) {
//        String sortOrder = sortForm.getSortOrder();
//        List<Person> persons = personService.getAllPersonsSorted(sortOrder);
//        return persons;
//    }


//    @PostMapping("/personsSort")
//    public String getAllPersonsSorted(@ModelAttribute SortForm sortForm, Model model) {
//        String sortOrder = sortForm.getSortOrder();
//        List<Person> persons = personService.getAllPersonsSorted(sortOrder);
////        List<Person> persons1 = personService.getAllPersonsSorted(sortOrder);
//        model.addAttribute("persons", persons);
////        model.addAttribute("persons1", persons1);
//        model.addAttribute("sortForm", sortForm);
//        return "get_all_persons";
//    }


// Trang danh sách người dùng có tổng tiền bị âm
//    @GetMapping("/negativebalance")
//    public String getPersonsInNegativeBalance(Model model) {
//        List<Person> persons = (List<Person>) personService.getPersonsInNegativeBalance().getBody();
//        model.addAttribute("persons", persons);
//        return "negative_balance_list";
//    }

//    @PostMapping("/persons")
//    public String getAllPersonsSortedByTotalMoney(SortForm sortForm, Model model) {
//        String sortOrder = sortForm.getSortOrder();
//        List<Person> persons = personService.getAllPersonsSortedByTotalMoney(sortOrder);
//        model.addAttribute("persons", persons);
//        return "get_all_persons";
//    }


// lấy ra thông tin người dùng thông qua id


//    // Trang danh sách người dùng được sắp xếp tăng dần theo tổng tiền
//    @GetMapping("/getAsc")
//    public String getAllPersonsSortedAscTotalmoney(Model model) {
//        List<Person> persons = (List<Person>) personService.getAllPersonsSortedAscTotalmoney().getBody();
//        model.addAttribute("persons", persons);
//        return "total_money_asc";
//    }
//
//    // Trang danh sách người dùng được sắp xếp giảm dần theo tổng tiền
//    @GetMapping("/getDesc")
//    public String getAllPersonsSortedDescTotalmoney(Model model) {
//        List<Person> persons = (List<Person>) personService.getAllPersonsSortedDescTotalmoney().getBody();
//        model.addAttribute("persons", persons);
//        return "total_money_desc";
//    }

//    @GetMapping("/index")
//    public String showSortForm(Model model) {
//        model.addAttribute("sortForm", new SortForm());
//        return "index";
//    }


// Show thông tin của từng người dùng
//    @GetMapping("/getall/{id}")
//    public String showUser(@PathVariable Long id, Model model) {
//        Person person = personService.getPersonById(id);
//        model.addAttribute("person", person);
//        return "person_detail";
//    }


//    @GetMapping("/getall/{id}/edit")
//    public String updateUserForm(@PathVariable Long id, Model model) {
//        Person person = personService.getPersonById(id);
//        if (person == null) {
//            throw new IllegalArgumentException("Invalid user Id:" + id);
//        }
//        model.addAttribute("person", person);
//        return "update_person";
//    }

// update thông tin người dùng
//    @PostMapping("/getall/{id}/edit")
//    public String updateUserSubmit(@PathVariable Long id, @ModelAttribute Person person) {
//        personService.updatePerson(id, person);
//        return "redirect:/getall";
//    }


//    @GetMapping("/form")
//    public String showForm(Model model) {
//        model.addAttribute("totalAmount", 0.0);
//        model.addAttribute("selectedUserIds", "");
//        List<Person> persons = personService.getAllPersons();
//        model.addAttribute("persons", persons);
//        return "person_form";
//    }


// Thông tin lịch sử giao dịch
//    @GetMapping("/transactionHistory")
//    public String viewTransactionHistory(Model model) {
//        List<TransactionHistory> transactionHistoryList = transactionHistoryRepository.findAll();
//        model.addAttribute("transactionHistoryList", transactionHistoryList);
//        return "transaction_history";
//    }

//    // Form load ảnh
//    @GetMapping("/upload")
//    public String showUploadForm() {
//        return "image-upload";
//    }
//
//    // Load ảnh
//    @PostMapping("/upload")
//    public String handleImageUpload(@RequestParam("file") MultipartFile file) throws IOException {
//        imageService.saveImage(file);
//        return "redirect:/images";
//    }
//
//    // Show ảnh
//    @GetMapping("/images")
//    public String showAllImages(Model model) {
//        List<Image> images = imageService.getAllImages();
//        model.addAttribute("images", images);
//        return "image-list";
//    }
//    @GetMapping("/updateHistory")
//    public String viewUpdateHistory(Model model) {
//        List<UpdateHistory> updateHistoryList = updateHistoryRepository.findAll();
//        model.addAttribute("updateHistoryList", updateHistoryList);
//        return "update_history";
//    }

//    @GetMapping("/search")
//    public String showSearchForm(Model model) {
//        model.addAttribute("searchForm", new SearchForm());
//        return "search_form";
//    }

//    @PostMapping("/search")
//    public String searchByName(@ModelAttribute("searchForm") SearchForm searchForm, Model model) {
//        String searchName = searchForm.getName();
//        List<Person> matchingPersons = personService.searchByName(searchName);
//        model.addAttribute("matchingPersons", matchingPersons);
//        return "search_results";
//    }

//    @PostMapping("/search")
//    public String searchPersons(@ModelAttribute SearchForm searchForm, Model model) {
//        String name = searchForm.getName();
//        List<Person> searchResults = personService.searchByName(name);
//        model.addAttribute("searchResults", searchResults);
//        model.addAttribute("searchForm", searchForm);
//        return "search_results";
//    }


//    @GetMapping("/{id}/subtractBalance")
//    public String subtractBalanceForm(@PathVariable Long id, Model model) {
//        User user = userService.getUserById(id);
//        model.addAttribute("user", user);
//        return "subtractBalance";
//    }


//    @PostMapping("/{id}/subtractBalance")
//    public String subtractBalanceSubmit(@PathVariable Long id, @RequestParam double amount) {
//        userService.subtractBalance(id, amount);
//        return "redirect:/users";
//    }


//    //Api dùng để update số tiền ộng vào và số tiền trừ đi để cho hệ thống tính toán
//    @PatchMapping("/{id}")
//    public ResponseEntity<Person> updateAddAndPayMoney(@PathVariable("id") long id, @RequestBody Person updatedPerson) {
//        return personController.updateAddAndPayMoney(id, updatedPerson);
//    }


//    // Trang danh sách người dùng
//    @GetMapping("/persons")
//    public String getAllPersons(Model model) {
//        List<Person> persons = personRepository.findAll();
//        model.addAttribute("persons", persons);
//        return "person_list";
//    }


//    //Api xóa thông tin của người dùng thông qua chỉ số id
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePerson(@PathVariable("id") long id) {
//        return personController.deletePerson(id);
//    }
//
//    //Api tính toán toán tổng tiền khi người dùng muốn nạp thêm tiền
//    @PostMapping("/{id}/calculate/totalmoneyadd")
//    public ResponseEntity<Void> calculateTotalMoneyAdd(@PathVariable long id) {
//        return personController.calculateTotalMoneyAdd(id);
//    }
//
//    //Api tính toán tổng tiền khi người dùng bị trừ tiền
//    @PostMapping("/{id}/calculate/totalmoneypay")
//    public ResponseEntity<Void> calculateTotalMoneyPay(@PathVariable long id) {
//        return personController.calculateTotalMoneyPay(id);
//    }
//

//
//    //Api sắp xếp theo sự tăng dần tổng tiền của tất cả người dùng
//    @GetMapping("/TotalmoneyAsc")
//    public ResponseEntity<List<Person>> getAllPersonsSortedAscTotalmoney() {
//        return personController.getAllPersonsSortedAscTotalmoney();
//    }
//
//    //Api sắp xếp theo sự giảm dần tổng tiền của tất cả người dùng
//    @GetMapping("/TotalmoneyDesc")
//    public ResponseEntity<List<Person>> getAllPersonsSortedDescTotalmoney() {
//        return personController.getAllPersonsSortedDescTotalmoney();
//    }
//
//    //Api lấy ra thông tin của những người dùng mà có tổng tiền bị âm
//    @GetMapping("/alert/negativebalance")
//    public ResponseEntity<List<Person>> getPersonsInNegativeBalance() {
//        return personController.getPersonsInNegativeBalance();
//    }


// Cập nhật số tiền cộng vào và số tiền trừ đi để cho hệ thống tính toán
//    @PatchMapping("/updateAddOrPayMoney/{id}")
//    public String updateAddAndPayMoney(@RequestParam("id") long id, @ModelAttribute Person updatedPerson) {
//        personService.updateAddAndPayMoney(id, updatedPerson);
//        return "update_money";
//    }


//    // Tính toán và cập nhật tiền khi người dùng bị trừ tiền
//    @PostMapping("/persons/{id}/calculate/totalmoneypay")    public String calculateTotalMoneyPay(@PathVariable long id) {
//        personController.calculateTotalMoneyPay(id);
//        return "redirect:/persons";
//    }


//    @PostMapping("/persons/new")
//    public String createPerson(@ModelAttribute Person person) {
//        personRepository.save(person);
//        return "redirect:/persons";
//    }
//
//    // Trang thông tin người dùng theo ID
//    @GetMapping("/persons/{id}")
//    public String getPersonById(@PathVariable("id") long id, Model model) {
//        Optional<Person> optionalPerson = personRepository.findById(id);
//        if (optionalPerson.isPresent()) {
//            Person person = optionalPerson.get();
//            model.addAttribute("user", person);
//            return "getUserById";
//        } else {
//            return "error";
//        }
//    }


//    // Trang thêm người dùng mới
//    @GetMapping("/persons/new")
//    public String showPersonForm(Model model) {
//        model.addAttribute("person", new Person());
//        return "person_form";
//    }


//    @GetMapping("/getall/{id}/addBalance")
//    public String addBalanceForm(@PathVariable Long id, Model model) {
//        Person person = personService.getPersonById(id);
//        model.addAttribute("person", person);
//        return "addBalance";
//    }


// Tính toán và cập nhật tiền khi người dùng nạp thêm tiền
//    @PostMapping("/getall/{id}/totalmoneyadd")
//    public String calculateTotalMoneyAdd(@PathVariable long id) {
//        personService.calculateTotalMoneyAdd(id);
//        return "redirect:/getall";
//    }


//    public void deleteUserAndTransactionHistory(Long id) {
//        // Xóa lịch sử giao dịch trước (nếu có)
//        Optional<Person> personOptional = personRepository.findById(id);
//        if (personOptional.isPresent()) {
//            Person person = personOptional.get();
//            List<TransactionHistory> transactionHistories = person.getTransactionHistories();
//            if (transactionHistories != null && !transactionHistories.isEmpty()) {
//                transactionHistoryRepository.deleteAll(transactionHistories);
//            }
//        }


//    @PostMapping("/getall/{id}/edit")
//    public String editUserSubmit(@PathVariable Long id, @ModelAttribute Person updatedPerson) {
//        personService.updatePerson(id, updatedPerson);
//        return "redirect:/getall";
//    }


//    @GetMapping("/getall/{id}/edit")
//    public String editUserForm(@PathVariable Long id, Model model) {
//        Person person = personService.getPersonById(id);
//        model.addAttribute("person", person);
//        return "edit_user";
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


// Xóa người dùng theo ID
//    @PostMapping("/delete")
//    public String deletePerson(@RequestParam("id") long id) {
//        personService.deletePerson(id);
//        return "redirect:/";
//    }


//    @GetMapping("/selectUsers")
//    public String selectUsers(Model model) {
//        List<Person> persons = personService.getAllPersons();
//        model.addAttribute("persons", persons);
//        return "select_persons";
//    }
//
//    @PostMapping("/calculate")
//    public String calculateShares(@RequestParam("totalAmount") double totalAmount,
//                                  @RequestParam("userIds") List<Long> selectedUserIds) {
//        personService.calculateAndSaveTotalMoney(totalAmount, selectedUserIds);
//        return "redirect:/getall";
//    }
//
//    @GetMapping("/selectUsers1")
//    public String selectUsers1(Model model) {
//        List<Person> persons = personService.getAllPersons();
//        model.addAttribute("persons", persons);
//        return "select_persons1";
//    }
//
//    @PostMapping("/calculate1")
//    public String calculateShares1(@RequestParam("totalAmount") double totalAmount,
//                                   @RequestParam("userIds") List<Long> selectedUserIds) {
//        personService.calculateAndSaveTotalMoney1(totalAmount, selectedUserIds);
//        return "redirect:/getall";
//    }
//
//    @GetMapping("/selectUsers2")
//    public String selectUsers2(Model model) {
//        List<Person> persons = personService.getAllPersons();
//        model.addAttribute("persons", persons);
//        return "select_persons2";
//    }
//
//    @PostMapping("/calculate2")
//    public String calculateShares2(@RequestParam("totalAmount") double totalAmount,
//                                   @RequestParam("userIds") List<Long> selectedUserIds) {
//        personService.calculateAndSaveTotalMoney2(totalAmount, selectedUserIds);
//        return "redirect:/getall";
//    }
//
//    @GetMapping("/selectUsers3")
//    public String selectUsers3(Model model) {
//        List<Person> persons = personService.getAllPersons();
//        model.addAttribute("persons", persons);
//        return "select_persons3";
//    }
//
//    @PostMapping("/calculate3")
//    public String calculateShares3(@RequestParam("totalAmount") double totalAmount,
//                                   @RequestParam("userIds") List<Long> selectedUserIds) {
//        personService.calculateAndSaveTotalMoney3(totalAmount, selectedUserIds);
//        return "redirect:/getall";
//    }


// Trang Home
//    @GetMapping("/")
//    public String homePage() {
//        return "home";
//    }

// Form add người dùng mới
//    @GetMapping("/add")
//    public String showAddPersonForm(Model model) {
//        model.addAttribute("user", new Person());
//        return "add_person";
//
//    }

//    @PostMapping("/add")
//    public String addUser(@ModelAttribute Person person, Model model) {
//        // Kiểm tra nếu đã tồn tại người dùng khác có cùng email hoặc phonenumber
//        if (personService.existsByEmailOrPhoneNumber(person.getEmail(), person.getPhoneNumber())) {
//            model.addAttribute("error", "Email or phone number already exists.");
//            return "add_person";
//        }
//        personService.savePerson(person);
//        return "redirect:/";
//    }

// Trang danh sách người dùng
//    @GetMapping("/getall")
//    public String getAllPersons(Model model) {
//        List<Person> persons = personService.getAllPersons();
//        model.addAttribute("persons", persons);
//        model.addAttribute("sortForm", new SortForm());
//        model.addAttribute("searchForm", new SearchForm());
//        return "get_all_persons";
//    }

//    @GetMapping("/getall")
//    public List<Person> getAllPersons() {
//        return personService.getAllPersons();
//    }

//    @DeleteMapping("/persons/{id}")
//    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
//        boolean deleted = personService.deleteUser(id).isPresent();
//        if (deleted) {
//            return ResponseEntity.ok("User deleted successfully.");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
//        }
//    }

