package com.bootcamp.banca.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class AccountTransaction {

	private BigDecimal ammount;
	private Date createdAt;
	private String accountNumber;
	private TransactionType transactionType;
}
