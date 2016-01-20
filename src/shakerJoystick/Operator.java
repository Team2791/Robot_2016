package shakerJoystick;
import configuration.Constants;
public class Operator {
	ShakerJoystick operatorJoystick;
	public Operator(){
		operatorJoystick = new ShakerJoystick(Constants.JOYSTICK_OPERATOR_PORT);
	}
	//place button layout for operator joystick here
	
}
