package transactionservice.tests.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import transactionservice.model.TransactionItem;
import transactionservice.model.TransactionItem.TransactionItemBuilder;
import transactionservice.service.TransactionService;

/**
 * @author Ekaterina Lobanova
 */
public class TransactionServiceTest {

	private static final String PRESENT_TYPE = "presentType";
	private static final String EMPTY_ID_TYPE = "emptyId";

	private TransactionItem childOfSecondItem;
	private TransactionItem childOfFifthItem;

	@Before
	public void setUp() {
		addNonMemberTransactionItems();

		this.childOfSecondItem = new TransactionItemBuilder().setTransactionId(5).setParentId(2).setAmount(100).build();
		TransactionService.getInstance().addTransaction(childOfSecondItem);

		this.childOfFifthItem = new TransactionItemBuilder().setTransactionId(6).setParentId(5).setAmount(50).build();
		TransactionService.getInstance().addTransaction(childOfFifthItem);
	}

	private void addNonMemberTransactionItems() {
		TransactionItemBuilder firstItem = new TransactionItemBuilder().setAmount(10).setTransactionId(1)
				.setType(PRESENT_TYPE);
		TransactionService.getInstance().addTransaction(firstItem.build());

		TransactionItemBuilder secondItem = new TransactionItemBuilder().setAmount(5).setTransactionId(2)
				.setParentId(1).setType("secondType");
		TransactionService.getInstance().addTransaction(secondItem.build());

		TransactionItemBuilder thirdItem = new TransactionItemBuilder().setTransactionId(3).setType(PRESENT_TYPE)
				.setParentId(1);
		TransactionService.getInstance().addTransaction(thirdItem.build());

		TransactionItemBuilder emptyIdItem = new TransactionItemBuilder().setType(EMPTY_ID_TYPE);
		TransactionService.getInstance().addTransaction(emptyIdItem.build());

		TransactionItemBuilder nullTypeItem = new TransactionItemBuilder().setTransactionId(4).setAmount(80)
				.setParentId(2);
		TransactionService.getInstance().addTransaction(nullTypeItem.build());
	}

	@Test
	public void testGetTransactionIdsOfTypeWhenQueryIsEmpty() {
		Set<Long> transactionIdsOfType = TransactionService.getInstance().getTransactionIdsOfType(null);

		assertNotNull(transactionIdsOfType);
		assertTrue(transactionIdsOfType.isEmpty());
	}

	@Test
	public void testGetTransactionIdsOfTypeWhenTargetIdIsEmpty() {
		Set<Long> transactionIdsOfType = TransactionService.getInstance().getTransactionIdsOfType(EMPTY_ID_TYPE);

		assertNotNull(transactionIdsOfType);
		assertTrue(transactionIdsOfType.isEmpty());
	}

	@Test
	public void testGetTransactionIdsOfTypeWhenSomeIdIsEmpty() {
		Set<Long> transactionIdsOfType = TransactionService.getInstance().getTransactionIdsOfType(PRESENT_TYPE);

		assertNotNull(transactionIdsOfType);
		assertFalse(transactionIdsOfType.isEmpty());

		int expectedMatchSize = 2;
		assertEquals(expectedMatchSize, transactionIdsOfType.size());
	}

	@Test
	public void testGetTransactionIdsOfType() {
		Set<Long> transactionIdsOfType = TransactionService.getInstance().getTransactionIdsOfType(PRESENT_TYPE);

		assertNotNull(transactionIdsOfType);
		assertFalse(transactionIdsOfType.isEmpty());

		int expectedMatchSize = 2;
		assertEquals(expectedMatchSize, transactionIdsOfType.size());
	}

	@Test
	public void testIsAncestorOf() {
		assertTrue(TransactionService.getInstance().isAncestorOf(2, childOfSecondItem));
		assertTrue(TransactionService.getInstance().isAncestorOf(5, childOfFifthItem));

		assertFalse(TransactionService.getInstance().isAncestorOf(3, childOfFifthItem));
	}

	@Test
	public void testIsAncestorOfWhenTransient() {
		assertTrue(TransactionService.getInstance().isAncestorOf(1, childOfSecondItem));
		assertTrue(TransactionService.getInstance().isAncestorOf(1, childOfFifthItem));
	}

	@Test
	public void testGetSumOfTransactionsLinkedTo() {
		double expectedSum = 230;
		double actualSum = TransactionService.getInstance().getSumOfTransactionsLinkedTo(2);

		assertEquals(expectedSum, actualSum, 0);
	}

	@Test
	public void testGetSumOfTransactionsLinkedToNoChildren() {
		double expectedSum = 0;
		double actualSum = TransactionService.getInstance().getSumOfTransactionsLinkedTo(6);

		assertEquals(expectedSum, actualSum, 0);
	}
}
