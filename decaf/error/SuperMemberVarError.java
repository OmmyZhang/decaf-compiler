package decaf.error;

import decaf.Location;

/**
 * super.member_var is not supported
 * PA2
 */
public class SuperMemberVarError extends DecafError {

    String classType;

	public SuperMemberVarError(Location location) {
		super(location);
	}

	@Override
	protected String getErrMsg() {
		return "super.member_var is not supported";
	}

}
