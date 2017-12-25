package decaf.error;

import decaf.Location;

/**
 * no parent class exist for class : people
 * PA2
 */
public class CopyNotSameError extends DecafError {

    String type;
    String destinType;

	public CopyNotSameError(Location location, String destinType, String type) 
	{
		super(location);
		this.type = type;
		this.destinType = destinType;
	}

	@Override
	protected String getErrMsg() {
		return "For copy expr, the source " + type + " and the destination " + destinType + " are not same";
	}

}
