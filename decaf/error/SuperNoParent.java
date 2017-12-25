package decaf.error;

import decaf.Location;

/**
 * no parent class exist for class : people
 * PA2
 */
public class SuperNoParent extends DecafError {

    String classType;

	public SuperNoParent(Location location, String classType) {
		super(location);
		this.classType = classType;
	}

	@Override
	protected String getErrMsg() {
		return "no parent class exist for "+ classType;
	}

}
