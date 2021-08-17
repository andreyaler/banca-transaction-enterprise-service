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

import java.math.MathContext;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WithdrawTransactionsService {

	@Autowired
	private EnterpriseTransactionsRepository repository;

	public Mono<TransactionResponse> createNewTransaction(AccountTransaction transaction, String ruc) {
		TransactionResponse response = new TransactionResponse();
		if (transaction.getTransactionType().equals(TransactionType.RETIRO)) {
			return sustractMoneyFromCurrentAccount(transaction, ruc);
		}
		return Mono.just(response);
	}

	private Mono<TransactionResponse> sustractMoneyFromCurrentAccount(AccountTransaction transaction, String ruc) {
		return repository.findByRucAndAccountNumber(ruc, transaction.getAccountNumber())
				.flatMap(et -> updateAccountBalance(transaction, et, ruc)).switchIfEmpty(noBalanceFound(transaction));
	}

	// TODO falta aplicar el costo de la transaccion
	private Mono<TransactionResponse> updateAccountBalance(AccountTransaction transaction, EnterpriseTransactions et,
			String ruc) {
		if (et.getAccountBalance().compareTo(transaction.getAmmount()) < 0)
			return noBalanceFound(transaction);
		MathContext mc = new MathContext(2);
		transaction.setCreatedAt(new Date());
		et.getTransactionList().add(transaction);
		et.setAccountBalance(et.getAccountBalance().subtract(transaction.getAmmount(), mc));
		et.setAccountNumber(transaction.getAccountNumber());
		et.setRuc(ruc);
		TransactionResponse response = createOKResponse(transaction);
		return repository.save(et).flatMap(t -> Mono.just(response));
	}

	private TransactionResponse createOKResponse(AccountTransaction transaction) {
		TransactionResponse response = new TransactionResponse();
		Map<String, Object> bodyResponse = new HashMap<>();
		response.setMessage("Se actualizo el saldo de la cuenta");
		bodyResponse.put("transaction", transaction);
		response.setBody(bodyResponse);
		response.setHttpStatus(HttpStatus.OK);
		return response;
	}

	// Si no existe un registro la cuenta esta en 0
	private Mono<TransactionResponse> noBalanceFound(AccountTransaction transaction) {
		TransactionResponse response = new TransactionResponse();
		Map<String, Object> bodyResponse = new HashMap<>();
		response.setMessage("La cuenta no tiene fondos suficientes");
		response.setHttpStatus(HttpStatus.BAD_REQUEST);
		bodyResponse.put("refusedTransaction", transaction);
		response.setBody(bodyResponse);
		return Mono.just(response);
	}
}
