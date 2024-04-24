package com.jungche.accounts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerDto {

    private String name;

    private String email;

    private String mobileNumber;

    @JsonProperty("accounts")
    private AccountsDto accountsDto;
}
