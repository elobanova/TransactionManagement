package transactionservice.tests.exporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import transactionservice.exporter.JSONExporter;
import transactionservice.model.StatusEnum;
import transactionservice.model.TransactionItem;
import transactionservice.model.TransactionItem.TransactionItemBuilder;
import transactionservice.service.TransactionService;

/**
 * @author Ekaterina Lobanova
 */
public class JSONExporterTest {
	private TransactionItem transaction;

	@Before
	public void setUp() throws IOException {
		transaction = new TransactionItemBuilder().setTransactionId(2).setAmount(100).setType("firstType").build();
	}

	@Test
	public void testExportNullTransactionToJSON() {
		JSONObject transactionAsJSON = JSONExporter.getInstance().exportTransactionToJSON(null);
		assertNotNull(transactionAsJSON);

		assertFalse(transactionAsJSON.has(JSONExporter.TYPE_PROPERTY));
		assertFalse(transactionAsJSON.has(JSONExporter.AMOUNT_PROPERTY));
	}

	@Test
	public void testExportTransactionWithoutParentToJSON() {
		JSONObject transactionAsJSON = JSONExporter.getInstance().exportTransactionToJSON(transaction);
		assertNotNull(transactionAsJSON);

		assertTrue(transactionAsJSON.has(JSONExporter.TYPE_PROPERTY));
		assertTrue(transactionAsJSON.has(JSONExporter.AMOUNT_PROPERTY));

		assertFalse(transactionAsJSON.has(JSONExporter.PARENT_ID_PROPERTY));
	}

	@Test
	public void testExportTransactionToJSON() {
		long expectedParentId = 1;
		transaction.setParentId(expectedParentId);
		JSONObject transactionAsJSON = JSONExporter.getInstance().exportTransactionToJSON(transaction);
		assertNotNull(transactionAsJSON);

		assertTrue(transactionAsJSON.has(JSONExporter.TYPE_PROPERTY));
		assertTrue(transactionAsJSON.has(JSONExporter.AMOUNT_PROPERTY));

		assertTrue(transactionAsJSON.has(JSONExporter.PARENT_ID_PROPERTY));

		double expectedAmount = 100;
		assertEquals(expectedAmount, transactionAsJSON.getDouble(JSONExporter.AMOUNT_PROPERTY), 0);

		String expectedType = "firstType";
		assertEquals(expectedType, transactionAsJSON.getString(JSONExporter.TYPE_PROPERTY));

		assertEquals(expectedParentId, transactionAsJSON.getLong(JSONExporter.PARENT_ID_PROPERTY));
	}

	@Test
	public void testExportSumToJSON() {
		double expectedSum = 100;
		JSONObject sumAsJSON = JSONExporter.getInstance().exportSumToJSON(100);
		assertNotNull(sumAsJSON);

		assertTrue(sumAsJSON.has(JSONExporter.SUM_PROPERTY));
		assertEquals(expectedSum, sumAsJSON.getDouble(JSONExporter.SUM_PROPERTY), 0);
	}

	@Test
	public void testExportEmptyListToJSON() {
		Set<Long> listOfIds = new HashSet<>();
		JSONArray listAsJSON = JSONExporter.getInstance().exportIdListToJSON(listOfIds);
		assertNotNull(listAsJSON);

		assertEquals(0, listAsJSON.length());
	}

	@Test
	public void testExportNullListToJSON() {
		JSONArray listAsJSON = JSONExporter.getInstance().exportIdListToJSON(null);
		assertNotNull(listAsJSON);

		assertEquals(0, listAsJSON.length());
	}

	@Test
	public void testExportIdListToJSON() {
		Set<Long> listOfIds = new HashSet<>();
		long firstId = 1l;
		long secondId = 2l;
		listOfIds.add(firstId);
		listOfIds.add(secondId);
		JSONArray listAsJSON = JSONExporter.getInstance().exportIdListToJSON(listOfIds);
		assertNotNull(listAsJSON);

		assertEquals(2, listAsJSON.length());
		assertEquals(firstId, listAsJSON.getLong(0));
		assertEquals(secondId, listAsJSON.getLong(1));
	}

	@Test
	public void testExportNullStatusToJSON() {
		JSONObject statusAsJson = JSONExporter.getInstance().exportStatusToJSON(null);
		assertNotNull(statusAsJson);

		assertFalse(statusAsJson.has(JSONExporter.STATUS_PROPERTY));
	}

	@Test
	public void testExportStatusToJSON() {
		String status = StatusEnum.OK.getName();
		JSONObject statusAsJson = JSONExporter.getInstance().exportStatusToJSON(StatusEnum.OK);
		assertNotNull(statusAsJson);

		assertTrue(statusAsJson.has(JSONExporter.STATUS_PROPERTY));
		assertEquals(status, statusAsJson.getString(JSONExporter.STATUS_PROPERTY));
	}

	@Test
	public void testExportFromJSONWhenStringIsNull() {
		TransactionItem item = JSONExporter.getInstance().exportFromJSON(null);
		assertNull(item);
	}

	@Test
	public void testExportFromJSONWhenNoParent() {
		TransactionItem item = JSONExporter.getInstance().exportFromJSON("{\"amount\":50,\"type\":\"sometype\"}");
		assertNotNull(item);

		assertEquals(50, item.getAmount(), 0);
		assertEquals("sometype", item.getType());
		assertEquals(TransactionService.ABSENT_ID, item.getParentId());
	}

	@Test
	public void testExportFromJSON() {
		TransactionItem item = JSONExporter.getInstance().exportFromJSON(
				"{\"amount\":50,\"type\":\"sometype\",\"parent_id\":7}");
		assertNotNull(item);

		assertEquals(50, item.getAmount(), 0);
		assertEquals("sometype", item.getType());
		assertEquals(7, item.getParentId());
	}
}
