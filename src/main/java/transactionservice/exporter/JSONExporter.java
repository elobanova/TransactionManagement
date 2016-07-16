package transactionservice.exporter;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import transactionservice.model.StatusEnum;
import transactionservice.model.TransactionItem;
import transactionservice.model.TransactionItem.TransactionItemBuilder;
import transactionservice.service.TransactionService;

/**
 * A class which exports the model objects into a JSON representation.
 * 
 * @author Ekaterina Lobanova
 */
public class JSONExporter {
	public static final String AMOUNT_PROPERTY = "amount";
	public static final String TYPE_PROPERTY = "type";
	public static final String PARENT_ID_PROPERTY = "parent_id";
	public static final String STATUS_PROPERTY = "status";
	public static final String SUM_PROPERTY = "sum";

	private static JSONExporter instance = null;

	private JSONExporter() {
	}

	public static JSONExporter getInstance() {
		if (instance == null) {
			synchronized (JSONExporter.class) {
				if (instance == null) {
					instance = new JSONExporter();
				}
			}
		}

		return instance;
	}

	/**
	 * Exports an instance of TransactionItem to JSON.
	 * 
	 * @param transactionItem
	 *            an item to export
	 * @return a JSON representation of transaction with its properties or an
	 *         empty JSON object
	 */
	public JSONObject exportTransactionToJSON(TransactionItem transactionItem) {
		JSONObject transactionAsJson = new JSONObject();
		if (transactionItem != null) {
			long parentId = transactionItem.getParentId();
			if (parentId != TransactionService.ABSENT_ID) {
				transactionAsJson.put(PARENT_ID_PROPERTY, parentId);
			}
			transactionAsJson.put(AMOUNT_PROPERTY, transactionItem.getAmount());
			transactionAsJson.put(TYPE_PROPERTY, transactionItem.getType());
		}
		return transactionAsJson;
	}

	/**
	 * Builds a JSON object with the sum property.
	 * 
	 * @param sum
	 *            a value for the sum property
	 * @return a JSON object with the sum property
	 */
	public JSONObject exportSumToJSON(double sum) {
		JSONObject sumObject = new JSONObject();
		sumObject.put(SUM_PROPERTY, sum);
		return sumObject;
	}

	/**
	 * Exports a set of transaction ids to a JSON array.
	 * 
	 * @param transactionIds
	 *            a set with ids to export
	 * @return a JSON array with ids or an empty JSON array
	 */
	public JSONArray exportIdListToJSON(Set<Long> transactionIds) {
		JSONArray transactionIdsAsJson = new JSONArray();
		if (transactionIds != null) {
			transactionIds.stream().forEach(transactionId -> transactionIdsAsJson.put(transactionId));
		}
		return transactionIdsAsJson;
	}

	/**
	 * Exports a status to JSON.
	 * 
	 * @param status
	 *            a status enum to export
	 * @return a JSON object with the status property
	 */
	public JSONObject exportStatusToJSON(StatusEnum status) {
		JSONObject statusAsJson = new JSONObject();
		if (status != null) {
			statusAsJson.put(STATUS_PROPERTY, status.getName());
		}
		return statusAsJson;
	}

	/**
	 * Constructs an instance of TransactionItem from a JSON string
	 * 
	 * @param json
	 *            a string containing a JSON representation of a transaction
	 * @return a configured instance of TransactionItem or null if string is
	 *         null
	 */
	public TransactionItem exportFromJSON(String json) {
		if (json == null) {
			return null;
		}

		JSONObject jsonObject = new JSONObject(json);
		TransactionItemBuilder builder = new TransactionItemBuilder();
		if (jsonObject.has(AMOUNT_PROPERTY)) {
			builder.setAmount(jsonObject.getDouble(AMOUNT_PROPERTY));
		}

		if (jsonObject.has(TYPE_PROPERTY)) {
			builder.setType(jsonObject.getString(TYPE_PROPERTY));
		}

		if (jsonObject.has(PARENT_ID_PROPERTY)) {
			builder.setParentId(jsonObject.getLong(PARENT_ID_PROPERTY));
		}

		return builder.build();
	}
}
