package example.api.repository;

import example.api.model.Account;
import example.api.model.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByUsername(String username);

    Account findByRole(Role role);
}
