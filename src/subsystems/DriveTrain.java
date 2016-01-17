package subsystems;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import util.Configuration;
import util.AnalyzeCamera;
import subsystems.DriveTrainAutonHelper;

/**
 *
 */
public class DriveTrain implements Subsystems {
	public static Talon leftTalonA;
	public static Talon leftTalonB;
	public static Talon rightTalonA;
	public static Talon rightTalonB;
	public static AxisCamera cam;
	public static DriveTrainAutonHelper DTAH;

	@Override
	public void init() {
		// TODO Auto-generated method stub
		// instantiated speed controller here
		leftTalonA = new Talon(Configuration.leftTalonPortA);
		leftTalonB = new Talon(Configuration.leftTalonPortA);
		rightTalonA = new Talon(Configuration.leftTalonPortA);
		rightTalonB = new Talon(Configuration.leftTalonPortA);
	}

	@Override
	public void initTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initDisabled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initAutonomous() {
		cam = new AxisCamera(Configuration.cameraPort);
		DTAH = new DriveTrainAutonHelper(cam);
	}

	@Override
	public void runTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void runDisabled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void runAutonomous() {
		// get onto courtyard first
		DTAH.run();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
}
