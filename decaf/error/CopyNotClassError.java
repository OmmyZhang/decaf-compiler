package decaf.error;

import decaf.Location;

/**
 * no parent class exist for class : people
 * PA2
 */
public class CopyNotClassError extends DecafError {

    String type;

	public CopyNotClassError(Location location, String type) {
		super(location);
		this.type = type;
	}

	@Override
	protected String getErrMsg() {
		return "expected class type for copy expr but " + type + " given";
	}

}
