package com.mealManage.domain;

import com.mealManage.mealmodel.transaction.MasterTransactionsAudit;

/**This POJO class used for Account balance transfer between siblings**/
public class AccBalanceTransferSibling {
	
	private MasterTransactionsAudit sourceTransferTransaction;
	private MasterTransactionsAudit tragetTransferTransaction;
	/**
	 * @return the sourceTransferTransaction
	 */
	public MasterTransactionsAudit getSourceTransferTransaction() {
		return sourceTransferTransaction;
	}
	/**
	 * @param sourceTransferTransaction the sourceTransferTransaction to set
	 */
	public void setSourceTransferTransaction(MasterTransactionsAudit sourceTransferTransaction) {
		this.sourceTransferTransaction = sourceTransferTransaction;
	}
	/**
	 * @return the tragetTransferTransaction
	 */
	public MasterTransactionsAudit getTragetTransferTransaction() {
		return tragetTransferTransaction;
	}
	/**
	 * @param tragetTransferTransaction the tragetTransferTransaction to set
	 */
	public void setTragetTransferTransaction(MasterTransactionsAudit tragetTransferTransaction) {
		this.tragetTransferTransaction = tragetTransferTransaction;
	}

}
