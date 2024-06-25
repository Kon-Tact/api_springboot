package example.api.service;

import example.api.config.ConsoleFormatter;
import example.api.model.Account;
import example.api.model.ProtectedId;
import example.api.model.Role;
import example.api.model.Status;
import example.api.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Data
@AllArgsConstructor
@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private Logger log;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private ConsoleFormatter console;

    public AccountService(
            BCryptPasswordEncoder encoder,
            Logger log,
            ConsoleFormatter console) {
        this.encoder = encoder;
        this.log = log;
        this.console = console;
    }

    //Used in the security config for the creation of the authentication manager bean
    public AccountService() {};

    @Autowired
    private AccountRepository accountRepository;

    public Optional<Account> getAccountById(final Long id) { return accountRepository.findById(id); }

    public Iterable<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountByUsername (String username) throws AccountNotFoundException {
        Account retrievedAccount = accountRepository.findByUsername(username);

        if(retrievedAccount == null) {
            log.info(console.format(Status.ERROR, "[ACCOUNT BY USERNAME - KO] -- STATUS : " +
                    HttpStatus.NOT_FOUND));
            //Force
            throw new AccountNotFoundException();
        } else {
            return retrievedAccount;
        }
    }

    public void deleteAccountById (final Long id) {
        accountRepository.deleteById(id);
    }

    public Account saveAccount (Account toSaveAccount) {

        if(!toSaveAccount.getUsername().equals("admin") && !toSaveAccount.getRole().toString().equals("ADMIN")) {
            toSaveAccount.setRole(Role.USER);
            log.info(console.format(Status.IN_METHOD, "ROLE INJECTED"));
        }

        Account savedAccount = this.accountRepository.save(toSaveAccount);
        log.info(console.format(Status.IN_METHOD, "ACCOUNT SAVED"));

        log.info(console.format(Status.IN_METHOD, "ID : " + savedAccount.getId()));
        log.info(console.format(Status.IN_METHOD, "username : " + savedAccount.getUsername()));
        log.info(console.format(Status.IN_METHOD, "password : " + savedAccount.getPassword()));
        log.info(console.format(Status.IN_METHOD, "email : " + savedAccount.getEmail()));
        log.info(console.format(Status.IN_METHOD, "role : " + savedAccount.getRole()));
        log.info(console.format(Status.IN_METHOD, "authorities : " + savedAccount.getAuthorities().toString()));
        return savedAccount;
    }

    public void deleteAllAccounts () {
        accountRepository.deleteAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Account account = accountRepository.findByUsername(username);

        if(account == null) {
            log.severe(console.format(Status.ERROR, "ACCOUNT NOT FOUND"));
            throw new UsernameNotFoundException("");
        }
        log.info(console.format(Status.IN_METHOD, "ACCOUNT FOUND"));

        return new User(
                account.getUsername(),
                account.getPassword(),
                account.getAuthorities()
        );
    }

    public void superUserManagement() {
        log.info(console.format(Status.METHOD_TYPE, "SUPER USER MANAGEMENT METHOD CALLED"));
        try {
            Account account = accountRepository.findByUsername("admin");
            account = account == null ? accountRepository.findByRole(Role.SUPER_ADMIN) : account;

            if (account == null) {
                Account superuser = new Account(
                        "admin", "admin", "admin@email.com"
                );
                log.info(console.format(Status.IN_METHOD, "ACCOUNT CREATED"));

                superuser.setPassword(encoder.encode(superuser.getPassword()));

                superuser.setRole(Role.SUPER_ADMIN);
                log.info(console.format(Status.IN_METHOD, "SUPER ADMIN ROLE SET"));

                superuser = saveAccount(superuser);
                ProtectedId.id = superuser.getId();
            } else {
                ProtectedId.id = account.getId();
                if (!Objects.equals(account.getRole().toString(), "SUPER_ADMIN")) {
                    account.setRole(Role.SUPER_ADMIN);
                    saveAccount(account);
                }
            }
            log.info(console.format(Status.SUCCESS, "ID " + ProtectedId.id + " HAS BEEN SECURED"));
            log.info(console.format(Status.SUCCESS, "[SUPER USER MANAGEMENT METHOD - DONE]"));
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR, "ERROR IN SUPER USER MANAGEMENT METHOD"));
            throw new RuntimeException(e);
        }
    }
}
