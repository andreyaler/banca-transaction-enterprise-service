package com.bootcamp.banca.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@Document
public class EnterpriseTransactions {

	@Id
	private String id;
	private String ruc;
	private Date createdAt;
	private Date modifiedAt;
	private BigDecimal accountBalance;
	private String accountNumber;
	private ArrayList<AccountTransaction> transactionList;
}
