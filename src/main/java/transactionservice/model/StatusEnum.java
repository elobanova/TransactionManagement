package transactionservice.model;

/**
 * An enum with the status of putting a transaction. A result can be either ok
 * (OK) or very bad (ERROR).
 * 
 * @author Ekaterina Lobanova
 */
public enum StatusEnum {
	OK("ok"), ERROR("error");

	private final String name;

	private StatusEnum(String name) {
		this.name = name;
	}

	/**
	 * Returns the string representation of the status state.
	 *
	 * @return the string representation of the status state.
	 */
	public String getName() {
		return this.name;
	}
}