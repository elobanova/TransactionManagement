package transactionservice.model;

/**
 * A class of the model to provide properties of a transaction.
 * 
 * @author Ekaterina Lobanova
 */
public class TransactionItem {
	private String type;
	private double amount;
	private long transactionId;
	private long parentId;

	public TransactionItem(TransactionItemBuilder builder) {
		this.type = builder.type;
		this.amount = builder.amount;
		this.transactionId = builder.transactionId;
		this.parentId = builder.parentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	/**
	 * A builder class for TransactionItemBuilder to avoid unreadable setter
	 * calls.
	 * 
	 * @author Ekaterina Lobanova
	 */
	public static class TransactionItemBuilder {
		private String type;
		private double amount;
		private long transactionId;
		private long parentId;

		public TransactionItemBuilder setType(String type) {
			this.type = type;
			return this;
		}

		public TransactionItemBuilder setAmount(double amount) {
			this.amount = amount;
			return this;
		}

		public TransactionItemBuilder setTransactionId(long transactionId) {
			this.transactionId = transactionId;
			return this;
		}

		public TransactionItemBuilder setParentId(long parentId) {
			this.parentId = parentId;
			return this;
		}

		public TransactionItem build() {
			return new TransactionItem(this);
		}
	}

}
