package decaf.error;

import decaf.Location;

/**
 * example：type: complex is different with other expr's type int<br>
 * PA2
 */
public class CaseTypeDifferentError extends DecafError {
    
    String myType;
    
    String type;
    
	public CaseTypeDifferentError(Location location, String myType, String type) {
		super(location);
		this.myType = myType;
		this.type = type;
	}

	@Override
	protected String getErrMsg() {
		return "type: "+ myType +" is different with other expr's type " + type;
	}

}
