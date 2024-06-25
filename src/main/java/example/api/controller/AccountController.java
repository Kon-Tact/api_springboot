package example.api.controller;

import example.api.config.ConsoleFormatter;
import example.api.config.JSonManager;
import example.api.config.JwtTokenProvider;
import example.api.model.*;
import example.api.service.AccountService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
public class AccountController {

    //Spring component injection
    @Autowired
    private AccountService accountService;

    //Beans injections
    @Autowired
    private final JwtTokenProvider jwtProvider;
    @Autowired
    private final AuthenticationManager authManager;
    @Autowired
    private final BCryptPasswordEncoder encoder;
    @Autowired
    private final Logger log;
    @Autowired
    private final ConsoleFormatter console;
    @Autowired
    private final JSonManager jSonManager;

    //Constructor for bean injection
    public AccountController(
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            Logger log,
            ConsoleFormatter consoleFormat,
            JSonManager jSonManager
    ) {
        this.jwtProvider = jwtTokenProvider;
        this.authManager = authenticationManager;
        this.encoder = bCryptPasswordEncoder;
        this.log = log;
        this.console = consoleFormat;
        this.jSonManager = jSonManager;
    }

    //Specific method

    //Factorising authentication protocol
    public Authentication toAuthenticate(Account account) {
        try {
            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                    account.getUsername(),
                    account.getPassword()
            );
            Authentication authentication = authManager.authenticate(userToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean toInvalidate(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.info(console.format(Status.IN_METHOD, "TOKEN EXTRACTED"));
            log.info(console.format(Status.IN_METHOD, token));
            jwtProvider.invalidateToken(token);
            log.info(console.format(Status.IN_METHOD, "TOKEN INVALIDATED"));
            return true;
        } else {
            log.info(console.format(Status.IN_METHOD, "TOKEN NOT FOUND"));
            return false;
        }
    }

    //HTTP Methods
    @CrossOrigin(origins = "http://192.168.55.208:8081") //MOTHERFUCKING SOLUTION
    @RequestMapping(value = "/account/login", method = {RequestMethod.OPTIONS})
    public ResponseEntity<Account> handleOptionsSaveAccount() {
        log.info(console.format(Status.METHOD_TYPE, "LOGIN OPTIONS CALLED"));
        try {
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/account/delete", method = {RequestMethod.OPTIONS})
    public ResponseEntity<Account> handleOptionsDeleteAccount() {
        log.info(console.format(Status.METHOD_TYPE, "DELETE OPTIONS CALLED"));
        try {
            return new ResponseEntity<>( HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Niveau d'autorisation : Admin
    //@PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/account/list")
    public Iterable<Account> getAllAccount() {
        log.info(console.format(Status.METHOD_TYPE, "ACCOUNT LIST METHOD CALLED"));
        Iterable<Account> accountList;
        try {
            accountList = accountService.getAllAccounts();
            log.info(console.format(Status.SUCCESS,
                    "[ACCOUNT LIST METHOD - DONE]  -- STATUS : " + HttpStatus.OK));
            return accountList;
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[ACCOUNT LIST METHOD - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
            throw new RuntimeException(e);
        }
    }

    //Niveau d'autorisation : User
    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PostMapping("/account/nothing")
    public String getAccountByUsername(@RequestBody String username) {
        log.info(console.format(Status.METHOD_TYPE, "ACCOUNT BY USERNAME METHOD CALLED"));
        try {
            Account account = accountService.getAccountByUsername(username);
            log.info(console.format(Status.IN_METHOD, "ACCOUNT FIND"));

            String role = String.valueOf(account.getRole());

            log.info(role);
            log.info(console.format(Status.SUCCESS,
                    "[GET ACCOUNT - DONE]  -- STATUS : " + HttpStatus.OK));
            return jSonManager.addLine("role", role).build();
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[GET ACCOUNT - DONE]  -- STATUS : "  + HttpStatus.INTERNAL_SERVER_ERROR));
            throw new RuntimeException(e);
        }
    }

    //Niveau d'autorisation : Admin
    //@PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/account/delete")
    public ResponseEntity<String> deleteAccountById(@RequestParam final Long id) {
        log.info(console.format(Status.METHOD_TYPE, "DELETE ACCOUNT BY ID METHOD CALLED"));
        try {

            if(Objects.equals(id, ProtectedId.id)) {
                log.severe(console.format(Status.ERROR,
                        "[DELETE ACCOUNT - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
                return new ResponseEntity<>(jSonManager.addLine("Error", "The super admin account cannot be deleted").build(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info(console.format(Status.IN_METHOD, "CHECK CONNECTED ACCOUNT"));
            accountService.deleteAccountById(id);
            log.info(console.format(Status.SUCCESS,
                    "[DELETE ACCOUNT - DONE]  -- STATUS : " + HttpStatus.OK));
            return new ResponseEntity<>(jSonManager.addLine("Status", "200").addLine("Method", "Delete account").build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[DELETE ACCOUNT - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
            return new ResponseEntity<>(jSonManager.addLine("Status", "500").addLine("Method", "Delete account").build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Niveau d'autorisation : Admin
    //@PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/account/clear")
    public ResponseEntity<AccountResponse> deleteAllAccounts(@Nonnull HttpServletRequest request) {
        log.info(console.format(Status.METHOD_TYPE, "DELETE ALL ACCOUNT METHOD CALLED"));
        AccountResponse response = new AccountResponse();
        try {
            toInvalidate(request);
            log.info(console.format(Status.IN_METHOD, "ACCOUNT DISCONNECTED"));

            accountService.deleteAllAccounts();
            log.info(console.format(Status.IN_METHOD, "ALL ACCOUNTS DELETED"));

            //Create a generic admin account
            Account generic = new Account("admin", "admin", "admin@email.com");
            String encodedPassword = encoder.encode(generic.getPassword());
            generic.setPassword(encodedPassword);
            log.info(console.format(Status.IN_METHOD, "PASSWORD ENCODED"));
            accountService.saveAccount(generic);
            log.info(console.format(Status.IN_METHOD, "GENERIC ADMIN ACCOUNT CREATED"));

            Authentication authentication = toAuthenticate(generic);
            log.info(console.format(Status.IN_METHOD, "AUTHENTICATION SET"));

            // Generate JWT token
            String token = jwtProvider.generateToken(authentication);
            log.info(console.format(Status.IN_METHOD, "JWT TOKEN GENERATED"));

            log.info(console.format(Status.SUCCESS,
                    "[DELETE ALL ACCOUNT - DONE]  -- STATUS: " + HttpStatus.OK));

            response.setJsonResponse(
                    jSonManager.addLine("Status", "200").addLine("Method", "Delete all accounts").build()
            );
            response.setAccount(generic);
            response.setToken(token);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[DELETE ALL ACCOUNT - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
            response.setJsonResponse(
                    jSonManager.addLine("Status", "500").addLine("Method", "Delete all accounts").build()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    //Niveau d'autorisation : Tout le monde
    @PostMapping("/account/save")
    public ResponseEntity<AccountResponse> saveAccount(@RequestBody Account initialAccount) {
        log.info(console.format(Status.METHOD_TYPE, "SAVING ACCOUNT METHOD CALLED"));
        AccountResponse response = new AccountResponse();
        try {

            String inMemoryPassword = initialAccount.getPassword();
            String encodedPassword = encoder.encode(initialAccount.getPassword());
            initialAccount.setPassword(encodedPassword);
            initialAccount.setRole(Role.USER);
            Account newAccount = accountService.saveAccount(initialAccount);
            initialAccount.setPassword(inMemoryPassword);

            CompletableFuture<Authentication> loginFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(50); // Wait for 0.05 seconds before logging in
                    //Asynchronous method needed because database writing and reading was made at the same time
                    log.info(console.format(Status.IN_METHOD, "CURRENTLY LOGGING IN ..."));
                    return toAuthenticate(initialAccount);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            });

            Authentication authentication = loginFuture.get();
            String token = jwtProvider.generateToken(authentication);
            response.setJsonResponse(jSonManager.addLine("Status", "200").addLine("Method", "Save account").build());
            response.setAccount(newAccount);
            response.setToken(token);
            log.info(console.format(Status.IN_METHOD, "RESPONSE CREATED"));

            log.info(console.format(Status.SUCCESS, "[SAVE ACCOUNT - DONE]  -- STATUS : " + HttpStatus.OK));
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.severe(console.format(Status.ERROR, "[SAVE ACCOUNT - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
            e.printStackTrace();
            response.setJsonResponse(jSonManager.addLine("Status", "500").addLine("Method", "Save account").build());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Niveau d'autorisation : User
    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PutMapping("/account/edit")
    public ResponseEntity<AccountResponse> editAccount(@RequestBody Account account) {
        log.info(console.format(Status.METHOD_TYPE, "EDIT ACCOUNT METHOD CALLED"));
        AccountResponse response = new AccountResponse();
        try {
            Optional<Account> existingAccount = accountService.getAccountById(account.getId());

            if (existingAccount.isPresent()) {

                if(account.getPassword().isEmpty()) {
                    account.setPassword(existingAccount.get().getPassword());
                    log.info(console.format(Status.IN_METHOD, "DB PASSWORD REUSE"));
                } else {
                    account.setPassword(encoder.encode(account.getPassword()));
                    log.info(console.format(Status.IN_METHOD, "ENCODED PASSWORD SET"));
                }

                Account newAccount = accountService.saveAccount(account);
                log.info(console.format(Status.IN_METHOD, "NEW ACCOUNT SAVED"));

                response.setJsonResponse(
                        jSonManager.addLine("Status", "200").addLine("Method", "Edit account").build()
                );
                response.setAccount(newAccount);

                log.info(console.format(Status.SUCCESS, "[EDIT ACCOUNT - DONE]  -- STATUS : " + HttpStatus.OK));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                log.severe(console.format(Status.ERROR, "[EDIT ACCOUNT - ACCOUNT NOT FOUND]  -- STATUS : " + HttpStatus.NOT_FOUND));
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[EDIT ACCOUNT - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR + e.getMessage()));
            response.setJsonResponse(
                    jSonManager.addLine("Status", "500").addLine("Method", "Edit account").build()
            );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/account/login")
    public ResponseEntity<AccountResponse> login(@RequestBody Account account) {
        log.info(console.format(Status.METHOD_TYPE, "LOGIN METHOD CALLED"));
        AccountResponse response = new AccountResponse();
        try {

            log.info(console.format(Status.IN_METHOD, "Credentials sent :"));
            log.info(console.format(Status.IN_METHOD, "username : " + account.getUsername()));
            log.info(console.format(Status.IN_METHOD, "password : " + account.getPassword()));
            Account existingAccount = accountService.getAccountByUsername(account.getUsername());

            if (existingAccount == null || !encoder.matches(account.getPassword(), existingAccount.getPassword())) {
                log.severe(console.format(Status.ERROR, "[LOGIN METHOD - INVALID USERNAME OR PASSWORD]"));
                response.setJsonResponse(
                        jSonManager.addLine("response","Invalid username or password").build()
                );
                return new ResponseEntity<>(response,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info(console.format(Status.IN_METHOD, "ACCOUNT RECOGNIZED"));

            //Authentication protocol
            Authentication authentication = toAuthenticate(account);
            log.info(console.format(Status.IN_METHOD, "AUTHENTICATION SET"));

            // Generate JWT token
            String token = jwtProvider.generateToken(authentication);
            log.info(console.format(Status.IN_METHOD, "JWT TOKEN GENERATED"));

            // Getting role of the existing account for authorization management in front
            String role = String.valueOf(existingAccount.getRole());

            String responseJson = jSonManager
                    .addLine("token", token)
                    .addLine("role", role)
                    .addLine("email", existingAccount.getEmail())
                    .addLine("id", existingAccount.getId().toString())
                    .build();

            response.setJsonResponse(
                    jSonManager.addLine("Status", "200")
                            .addLine("Method", "Login")
                            .build()
            );
            account.setRole(existingAccount.getRole());
            account.setEmail(existingAccount.getEmail());
            account.setId(existingAccount.getId());
            response.setAccount(account);
            response.setToken(token);

            log.info(console.format(Status.IN_METHOD, responseJson));
            log.info(console.format(Status.SUCCESS, "[LOGIN - DONE]  -- STATUS : " + HttpStatus.OK));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[LOGIN - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR + e.getMessage()));
            response.setJsonResponse(jSonManager.addLine("response", "The login method did not worked").build());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("account/logout")
    public ResponseEntity<String> logout(@Nonnull HttpServletRequest request) {
        log.info(console.format(Status.METHOD_TYPE, "LOGOUT METHOD CALLED"));
        try {
            boolean isTokenInvalidated = toInvalidate(request);
            log.info(console.format(Status.SUCCESS, "[LOGOUT - DONE]  -- STATUS : " + HttpStatus.OK));
            String tokenOrNot = isTokenInvalidated ? "Account disconnected and token invalidate" : "Account disconnected";
            return new ResponseEntity<>(jSonManager.addLine("response", tokenOrNot).build(),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[LOGOUT - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR + e.getMessage()));
            return new ResponseEntity<>(jSonManager
                    .addLine("response", "The logout method did not worked").build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Niveau d'autorisation : Tout le monde
    //Only used in test
    @GetMapping("/account/actual")
    public String getCurrentAccount() throws AccountNotFoundException {
        log.info(console.format(Status.METHOD_TYPE, "ACTUAL METHOD CALLED"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal().toString().equals("anonymousUser")) {
            log.severe(console.format(Status.ERROR, "[ACTUAL METHOD - DEFAULT AUTHENTICATION GOT]"));
            return ("There's no connected account");
        }
        log.info(console.format(Status.IN_METHOD, "[ACTUAL METHOD - AUTHENTICATION GOT]"));
        String username = (String) authentication.getPrincipal();
        log.info(console.format(Status.IN_METHOD, "[ACTUAL METHOD - USERNAME GOT : "+ username.toUpperCase(Locale.ROOT) +"]"));
        Account account = accountService.getAccountByUsername(username);
        log.info(console.format(Status.SUCCESS, "[ACTUAL METHOD - USERNAME GOT : "+ username.toUpperCase(Locale.ROOT) +"]"));

        return "The connected account is " + username + ". Authorities : " + account.getAuthorities();
    }

    @PostMapping("/account/role")
    public ResponseEntity<String> changeRole(@RequestBody String[] idRole) {
        log.info(console.format(Status.METHOD_TYPE, "CHANGE ROLE METHOD CALLED"));
        try  {
            Optional<Account> optional = accountService.getAccountById(Long.valueOf(idRole[0]));

            if(optional.isEmpty()) {
                log.severe(console.format(Status.ERROR, "ACCOUNT UNRECOGNIZED"));
                return new ResponseEntity<>(jSonManager.addLine("Error", "Account unrecognized").build(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info(console.format(Status.IN_METHOD, "ACCOUNT GET"));

            Account account = optional.orElse(new Account());
            if (idRole[1].equals("USER")) {
                account.setRole(Role.ADMIN);
            } else if (idRole[1].equals("ADMIN")) {
                account.setRole(Role.USER);
            } else {
                return new ResponseEntity<>(jSonManager.addLine("Error", "Role unrecognized or protected").build(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info(console.format(Status.IN_METHOD, "NEW ROLE SET"));

            log.info(console.format(Status.IN_METHOD, account.toString()));
            accountService.saveAccount(account);
            log.info(console.format(Status.IN_METHOD, "[CHANGE METHOD - DONE] -- STATUS : " + HttpStatus.OK));
            return new ResponseEntity<>(jSonManager.addLine("Status", "200").addLine("Method", "Change role").build(),
                    HttpStatus.OK);

        } catch (Exception e) {
            log.severe(console.format(Status.ERROR, "[CHANGE METHOD - KO] -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
            e.printStackTrace();
            return new ResponseEntity<>(jSonManager.addLine("Status", "500").addLine("Method", "Change role").build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
