package util;

public class Configuration {
	//////////////////
	//// Vision - global targeting values
	//////////////////
	public static final String cameraPort = "cam0";// check on robotRio(web
													// interface)
	public static final int hueLow = 60;
	public static final int hueHigh = 100;
	public static final int saturationLow = 90;
	public static final int saturationHigh = 255;
	public static final int valueLow = 20;
	public static final int valueHigh = 255;
	// Minimum area of particles to be considered
	public static final int minArea = 150;
	public static final int maxArea = 65535;
	// Maximum number of particles to process
	public static final int maxParticles = 8;

	// Color settings for particle filtering
	public static final int redLow = 0;
	public static final int redHigh = 100;
	public static final int greenLow = 110;
	public static final int greenHigh = 255;
	public static final int blueLow = 235;
	public static final int blueHigh = 255;
	// Minimum width the height ratio of valid targets;
	public static final int minWidHighRat = 1;//rough estimate of min must be tested
	public static final int minWidth = 80;// min width of targets
	public static final int calibrated = 0;
	public static final int exclude = 0;
	//////////////////
	//// Ports - on RRIO
	//////////////////
	public static final int leftTalonPortA = 0;
	public static final int leftTalonPortB = 1;
	public static final int rightTalonPortA = 2;
	public static final int rightTalonPortB = 3;

	//////////////////
	//// States
	//////////////////
	public static enum AutonStates {
		ONE_BALL, TWO_BALL, SPY_BOT
	}

	public static enum DriveState {
		AUTO, MANUAL
	}
	
	public static enum GearState{
		HIGH,LOW
	}

	//////////////////
	//// Constants - global final values
	//////////////////
	public static final String AutonSelection = null;

}
