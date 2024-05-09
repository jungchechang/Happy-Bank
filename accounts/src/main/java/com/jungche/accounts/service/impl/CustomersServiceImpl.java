package com.jungche.accounts.service.impl;

import com.jungche.accounts.dto.AccountsDto;
import com.jungche.accounts.dto.CardsDto;
import com.jungche.accounts.dto.CustomerDetailsDto;
import com.jungche.accounts.dto.LoansDto;
import com.jungche.accounts.entity.Accounts;
import com.jungche.accounts.entity.Customer;
import com.jungche.accounts.exception.ResourceNotFoundException;
import com.jungche.accounts.mapper.AccountsMapper;
import com.jungche.accounts.mapper.CustomerMapper;
import com.jungche.accounts.repository.AccountsRepository;
import com.jungche.accounts.repository.CustomerRepository;
import com.jungche.accounts.service.ICustomersService;
import com.jungche.accounts.service.client.CardsFeignClient;
import com.jungche.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;

    }
}
