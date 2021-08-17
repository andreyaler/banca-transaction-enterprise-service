package com.bootcamp.banca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bootcamp.banca.model.AccountTransaction;
import com.bootcamp.banca.model.EnterpriseTransactions;
import com.bootcamp.banca.model.TransactionResponse;
import com.bootcamp.banca.service.DepositTransactionsService;
import com.bootcamp.banca.service.WithdrawTransactionsService;

import reactor.core.publisher.Mono;

@RestController
public class AccountTransactionsController {

	@Autowired
	private WithdrawTransactionsService withdrawService;

	@Autowired
	private DepositTransactionsService depositService;

	@PostMapping("/e-transaction/withdraw")
	public Mono<TransactionResponse> withdrawTransaction(@RequestBody AccountTransaction transaction,
			@RequestParam String ruc) {
		return withdrawService.createNewTransaction(transaction, ruc);
	}

	@PostMapping("/e-transaction/deposit")
	public Mono<TransactionResponse> depositTransation(@RequestBody AccountTransaction transaction,
			@RequestParam String ruc) {
		return depositService.createNewTransaction(transaction, ruc);
	}

	@GetMapping("/e-transaction/getall")
	public Mono<EnterpriseTransactions> getAllTransactions(String ruc, String accountNumber) {
		return depositService.getAllTransactions(ruc, accountNumber);
	}
}
