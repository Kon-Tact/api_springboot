package example.api.model;

import lombok.Data;

@Data
public class AccountResponse {
    private String jsonResponse;
    private Account account;
    private String token;

    public AccountResponse() {}
}
