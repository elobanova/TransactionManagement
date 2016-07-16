package transactionservice.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import transactionservice.model.TransactionItem;

/**
 * A service singleton provides operations on the transactions such as finding a
 * sum of all transactions amount that are transitively linked by a parent id to
 * a given transaction id, collecting a list of all transaction identifiers that
 * share the same given type, adding a transaction into a map.
 * 
 * @author Ekaterina Lobanova
 */
public class TransactionService {
	public static final long ABSENT_ID = 0;
	private ConcurrentMap<Long, TransactionItem> transactions;

	private static TransactionService instance;

	private TransactionService() {
		this.transactions = new ConcurrentHashMap<>();
	}

	public static TransactionService getInstance() {
		if (instance == null) {
			synchronized (TransactionService.class) {
				if (instance == null) {
					instance = new TransactionService();
				}
			}
		}

		return instance;
	}

	/**
	 * Adds a transaction to a map, if a transaction is not empty and has an id.
	 * 
	 * @param transaction
	 *            a transaction to add
	 * @return a previous value associated with transaction id, or null if there
	 *         was no mapping for id (a return value is consistent with a Map's
	 *         put return value)
	 */
	public TransactionItem addTransaction(TransactionItem transaction) {
		if (transaction != null && transaction.getTransactionId() != ABSENT_ID) {
			return transactions.put(transaction.getTransactionId(), transaction);
		}

		return null;
	}

	/**
	 * Calculates the sum of amount of all transactions that are transitively
	 * linked by a parent id to a given transactionId
	 * 
	 * @param transactionId
	 *            a given transaction id
	 * @return the sum of amount of all transactions that are transitively
	 *         linked by a parent id to a given transactionId
	 */
	public double getSumOfTransactionsLinkedTo(long transactionId) {
		double sumOfLinkedTransactions = 0;
		Collection<TransactionItem> transactionItems = transactions.values();
		if (transactionItems != null && !transactionItems.isEmpty()) {
			return transactionItems.stream().filter(transactionItem -> isAncestorOf(transactionId, transactionItem))
					.map(transactionItem -> transactionItem.getAmount()).reduce((one, another) -> one + another)
					.orElse(0d);
		}
		return sumOfLinkedTransactions;
	}

	/**
	 * Collects a list of all transaction identifiers that share the same given
	 * type
	 * 
	 * @param transactionType
	 *            a type to share
	 * @return a list of all transaction identifiers that share a given
	 *         transaction type
	 */
	public Set<Long> getTransactionIdsOfType(String transactionType) {
		Set<Long> transactionIds = new HashSet<>();
		Collection<TransactionItem> transactionItems = transactions.values();
		if (transactionItems != null && !transactionItems.isEmpty()) {
			return transactionItems.stream()
					.filter(transactionItem -> isTransactionTypeEqual(transactionItem, transactionType))
					.map(transactionItem -> transactionItem.getTransactionId()).collect(Collectors.toSet());
		}
		return transactionIds;
	}

	private boolean isTransactionTypeEqual(TransactionItem transactionItem, String transactionType) {
		if (transactionItem != null) {
			String transactionItemType = transactionItem.getType();
			return transactionItemType != null && transactionItemType.equals(transactionType);
		}

		return false;
	}

	/**
	 * Performs the check if the parent transaction with parentId is an ancestor
	 * of transactionItem
	 * 
	 * @param parentId
	 *            an id of an ancestor candidate transaction
	 * @param transactionItem
	 *            a query transaction
	 * @return true if the parent transaction with parentId is an ancestor of
	 *         transactionItem
	 */
	public boolean isAncestorOf(long parentId, TransactionItem transactionItem) {
		if (transactionItem == null || parentId == ABSENT_ID) {
			return false;
		}

		if (transactionItem.getParentId() == parentId) {
			return true;
		}

		return isAncestorOf(parentId, transactions.get(transactionItem.getParentId()));
	}

	public ConcurrentMap<Long, TransactionItem> getTransactions() {
		return transactions;
	}

	/**
	 * Adds a transaction to a map, if a transaction is not empty and has an id.
	 * Does the same as addTransaction() except that the action is performed
	 * atomically.
	 * 
	 * @param transactionId
	 *            an id of transaction being added
	 * @param transactionToPut
	 *            a transaction to add
	 * @return a previous value associated with transaction id, or null if there
	 *         was no mapping for id (a return value is consistent with a Map's
	 *         putIfAbsent return value)
	 */
	public TransactionItem putIfAbsent(long transactionId, TransactionItem transactionToPut) {
		if (transactionToPut != null && transactionId != ABSENT_ID) {
			return transactions.putIfAbsent(transactionId, transactionToPut);
		}
		return null;
	}

	/**
	 * Updates a transaction in a map, if it is present there. The action is
	 * performed atomically.
	 * 
	 * @param transactionId
	 *            an id of transaction being updated
	 * @param transactionWithUpdate
	 *            a transaction item to replace an old one
	 * @return a previous value associated with transaction id, or null if there
	 *         was no mapping for id (a return value is consistent with a Map's
	 *         replace return value)
	 */
	public TransactionItem replace(long transactionId, TransactionItem transactionWithUpdate) {
		if (transactionWithUpdate != null && transactionId != ABSENT_ID) {
			return transactions.replace(transactionId, transactionWithUpdate);
		}
		return null;
	}
}
