package util;

import edu.wpi.first.wpilibj.Joystick;

public class ShakerJoystick extends Joystick {
   
	private static double JOYSTICK_SCALE  =  Configuration.joystick_scale;
	private static double JOYSTICK_DEADZONE = Configuration.joystick_deadzone;
	private static double AXIS_SCALE = Configuration.axis_scale;
	private static double AXIS_DEADZONE = Configuration.axis_deadzone;
	
    public ShakerJoystick(final int port){
        super(port);
    }
    public double getx(){
        double X = super.getX();
        return fixXYInput(X);
    }
    public double gety(){
        double Y = super.getY();
        return fixXYInput(Y);
    }	
	public double getAxis(int slot){
		double input = super.getRawAxis(slot);
		return fixAxisInput(input);
	}
		
	private double fixXYInput(double input){
		input *= JOYSTICK_SCALE;		
		if(input < JOYSTICK_DEADZONE && input > -JOYSTICK_DEADZONE)
			return 0.0;
		return input;
	}
	private double fixAxisInput(double input){
		input *= AXIS_SCALE;
		if(input < AXIS_DEADZONE && input > -AXIS_DEADZONE)
			return 0.0;
		return input;
	}
}