package com.bootcamp.banca.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.bootcamp.banca.model.AccountTransaction;
import com.bootcamp.banca.model.EnterpriseTransactions;
import com.bootcamp.banca.model.TransactionResponse;
import com.bootcamp.banca.model.TransactionType;
import com.bootcamp.banca.repository.EnterpriseTransactionsRepository;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class DepositTransactionsService {

	@Autowired
	private EnterpriseTransactionsRepository repository;

	public Mono<TransactionResponse> createNewTransaction(AccountTransaction transaction, String ruc) {
		TransactionResponse response = new TransactionResponse();
		if (transaction.getTransactionType().equals(TransactionType.DEPOSITO)) {
			return addMoneyToCurrentAccount(transaction, ruc);
		}
		return Mono.just(response);
	}

	private Mono<TransactionResponse> addMoneyToCurrentAccount(AccountTransaction transaction, String ruc) {
		return repository.findByRucAndAccountNumber(ruc, transaction.getAccountNumber())
				.flatMap(c -> updateAccountBalance(transaction, c))
				.switchIfEmpty(createNewAccountBalance(transaction, ruc));
	}

	private Mono<TransactionResponse> createNewAccountBalance(AccountTransaction transaction, String ruc) {
		EnterpriseTransactions eTransactions = setNewEnterpriseTransactions(transaction, ruc);
		TransactionResponse response = createOKResponse(transaction);
		return repository.insert(eTransactions).flatMap(c -> Mono.just(response));
	}

	private Mono<TransactionResponse> updateAccountBalance(AccountTransaction transaction,
			EnterpriseTransactions eTransactions) {
		eTransactions.setAccountBalance(eTransactions.getAccountBalance().add(transaction.getAmmount()));
		eTransactions.getTransactionList().add(transaction);
		eTransactions.setAccountNumber(transaction.getAccountNumber());
		transaction.setCreatedAt(new Date());
		TransactionResponse response = createOKResponse(transaction);
		return repository.save(eTransactions).flatMap(c -> Mono.just(response));
	}

	private TransactionResponse createOKResponse(AccountTransaction transaction) {
		TransactionResponse response = new TransactionResponse();
		Map<String, Object> responseBody = new HashMap<>();
		response.setMessage("Se agregaron fondos a la cuenta con exito");
		response.setHttpStatus(HttpStatus.OK);
		responseBody.put("transaction", transaction);
		response.setBody(responseBody);
		return response;
	}

	private EnterpriseTransactions setNewEnterpriseTransactions(AccountTransaction transaction, String ruc) {
		EnterpriseTransactions eTransactions = new EnterpriseTransactions();
		ArrayList<AccountTransaction> transactions = new ArrayList<>();
		transaction.setCreatedAt(new Date());
		transactions.add(transaction);
		eTransactions.setTransactionList(transactions);
		eTransactions.setAccountNumber(transaction.getAccountNumber());
		eTransactions.setAccountBalance(transaction.getAmmount());
		eTransactions.setCreatedAt(new Date());
		eTransactions.setRuc(ruc);
		return eTransactions;
	}

	public Mono<EnterpriseTransactions> getAllTransactions(String ruc, String accountNumber) {
		return repository.findByRucAndAccountNumber(ruc, accountNumber);
	}
}
