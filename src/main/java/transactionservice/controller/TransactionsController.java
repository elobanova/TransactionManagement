package transactionservice.controller;

import java.net.HttpURLConnection;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import transactionservice.exporter.JSONExporter;
import transactionservice.model.StatusEnum;
import transactionservice.model.TransactionItem;
import transactionservice.service.TransactionService;

/**
 * A controller class to handle RESTful requests. The operations supported are
 * returning a transaction by Id on GET, a collection of transactions of a given
 * type on GET, a sum of linked to parent transactions on GET and updating or
 * adding if not present a transaction on PUT.
 * 
 * @author Ekaterina Lobanova
 *
 */
@Path("/transactionservice")
public class TransactionsController {

	/**
	 * Updates a transaction or adds it if not present.
	 * 
	 * @param transactionId
	 *            an id of transaction to update or put
	 * @param json
	 *            a body of a transaction with its properties
	 * @return a response in a JSON format with the status of operation
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/transaction/{transactionId}")
	public Response putTransactionWithId(@PathParam("transactionId") long transactionId, String json) {
		TransactionItem transactionToPut = JSONExporter.getInstance().exportFromJSON(json);
		transactionToPut.setTransactionId(transactionId);
		TransactionService.getInstance().putIfAbsent(transactionId, transactionToPut);
		TransactionService.getInstance().replace(transactionId, transactionToPut);
		JSONObject statusAsJSON = JSONExporter.getInstance().exportStatusToJSON(StatusEnum.OK);
		return Response.status(HttpURLConnection.HTTP_OK).entity(statusAsJSON.toString()).build();
	}

	/**
	 * Retrieves a transaction by id.
	 * 
	 * @param transactionId
	 *            an id of transaction to retrieve
	 * @return a response containing transaction with an id matching a query or
	 *         an empty object in JSON format
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/transaction/{transactionId}")
	public Response getTransactionById(@PathParam("transactionId") long transactionId) {
		TransactionItem transactionResult = TransactionService.getInstance().getTransactions().get(transactionId);
		JSONObject transactionResultJSON = JSONExporter.getInstance().exportTransactionToJSON(transactionResult);
		return Response.status(HttpURLConnection.HTTP_OK).entity(transactionResultJSON.toString()).build();
	}

	/**
	 * Produces a json list of all transaction ids that share the same given
	 * type
	 * 
	 * @param type
	 *            a type to share
	 * @return a response containing a JSON array with ids of those transactions
	 *         which share a given type
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/types/{type}")
	public Response getTransactionsOfType(@PathParam("type") String type) {
		Set<Long> matchingTypeSet = TransactionService.getInstance().getTransactionIdsOfType(type);
		JSONArray transactionsResultJSON = JSONExporter.getInstance().exportIdListToJSON(matchingTypeSet);
		return Response.status(HttpURLConnection.HTTP_OK).entity(transactionsResultJSON.toString()).build();
	}

	/**
	 * Gets a sum of all transactions that are transitively linked by their
	 * parent id to transactionId as a JSON.
	 * 
	 * @param transactionId
	 *            a transaction Id to which the transactions participating in
	 *            sum aggregation are linked
	 * @return a response in a JSON format with all transactions that are
	 *         transitively linked by their parent id to transactionId.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sum/{transactionId}")
	public Response getSumOfTransactionsLinkedTo(@PathParam("transactionId") long transactionId) {
		double sum = TransactionService.getInstance().getSumOfTransactionsLinkedTo(transactionId);
		JSONObject sumAsJSON = JSONExporter.getInstance().exportSumToJSON(sum);
		return Response.status(HttpURLConnection.HTTP_OK).entity(sumAsJSON.toString()).build();
	}
}
