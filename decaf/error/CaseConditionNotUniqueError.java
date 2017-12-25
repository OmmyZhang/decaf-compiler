package decaf.error;

import decaf.Location;

/**
 * example：incompatible case expr: bool given, but int expected<br>
 * PA2
 */
public class CaseConditionNotUniqueError extends DecafError {

	public CaseConditionNotUniqueError(Location location) {
		super(location);
	}

	@Override
	protected String getErrMsg() {
		return "condition is not unique";
	}

}
