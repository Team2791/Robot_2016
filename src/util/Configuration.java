package util;

public class Configuration {
	//////////////////
	////Ports
	//////////////////
	public static int leftDrivePortA = 0;
	public static int leftDrivePortB = 0;
	public static int rightDrivePortA = 0;
	public static int rightDrivePortB = 0;
	
	//////////////////
	////States
	//////////////////
	public enum drive_state{
		ARCADE,TANK
	}
	public enum shifter_mode{
		AUTO,MANUAL
	}
	public enum shifter_state{
		HIGH_GEAR, LOW_GEAR
	}
	//////////////////
	////Constants
	//////////////////
	public static int driver_joystick = 0;
	public static int operator_joystick = 1;
	public static double joystick_scale = 0;
	public static double joystick_deadzone = 0.01;
	public static double axis_scale = 0;
	public static double axis_deadzone=0.01;
}
