
package org.usfirst.frc.team2791.robot;

import configuration.Camera;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import util.AnalyzeCamera;
import util.DriveTrainAutonHelper;
import util.Logger;
import util.RoboClock;
import util.RoboException;

public class Robot extends IterativeRobot {

	public enum GamePeriod {
		AUTONOMOUS, TELEOP, DISABLED
	}

	public enum SafetyMode {
		SAFETY, FULL_CONTROL
	}
	
	private static DriveTrainAutonHelper DTAH;
	private static AxisCamera cam;
	private static RoboClock disabledTimer;
	private static RoboClock autonTimer;
	private static RoboClock teleopTimer;
	private static RoboClock powerTimer;

	private static GamePeriod gamePeriod;
	private static SafetyMode safetyMode;

	public void robotInit() {
		disabledTimer = new RoboClock();
		disabledTimer.setName("Disabled Timer");

		teleopTimer = new RoboClock();
		teleopTimer.setName("Teleop Timer");

		autonTimer = new RoboClock();
		autonTimer.setName("Auton Timer");

		powerTimer = new RoboClock();
		powerTimer.setName("Power timer");

		gamePeriod = GamePeriod.DISABLED;

		cam = new AxisCamera(Camera.cameraPort);

	}

	public void disabledInit() {
		gamePeriod = GamePeriod.DISABLED;
	}

	public void autonomousInit() {
		gamePeriod = GamePeriod.AUTONOMOUS;
		DTAH = new DriveTrainAutonHelper(cam);
	}

	public void teleopInit() {
		gamePeriod = GamePeriod.TELEOP;
		if (DriverStation.getInstance().isFMSAttached()) {
			safetyMode = SafetyMode.FULL_CONTROL;
		}
	}

	public void disabledPeriodic() {
		super.disabledPeriodic();
	}

	public void autonomousPeriodic() {
		super.autonomousPeriodic();
		
	}

	public void teleopPeriodic() {
		super.teleopPeriodic();
	}

	public static GamePeriod getGamePeriod() {
		return gamePeriod;
	}

	public static SafetyMode getSafetyMode() {
		return safetyMode;
	}

	public static RoboClock getCurrentModeTimer() {
		switch (gamePeriod) {
		case AUTONOMOUS:
			return autonTimer;
		case TELEOP:
			return teleopTimer;
		case DISABLED:
			return disabledTimer;
		default:
			Logger.exception(new RoboException("no current mode timer"));
			return new RoboClock();
		}
	}

	public static RoboClock getPowerTimer() {
		return powerTimer;
	}

	public static RoboClock getTeleopTimer() {
		return teleopTimer;
	}

	public static RoboClock getAutonTimer() {
		return autonTimer;
	}

	public static RoboClock getDisabledTimer() {
		return disabledTimer;
	}
}
