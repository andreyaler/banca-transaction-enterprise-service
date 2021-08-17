package com.bootcamp.banca.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bootcamp.banca.model.EnterpriseTransactions;

import reactor.core.publisher.Mono;

public interface EnterpriseTransactionsRepository extends ReactiveMongoRepository<EnterpriseTransactions, String> {

	Mono<EnterpriseTransactions> findByRucAndAccountNumber(String ruc, String accountNumber);
}
