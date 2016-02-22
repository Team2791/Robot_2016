package org.usfirst.frc.team2791.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.configuration.Ports;
import org.usfirst.frc.team2791.helpers.AutonHelper;
import org.usfirst.frc.team2791.helpers.TeleopHelper;
import org.usfirst.frc.team2791.shakerJoystick.Driver;
import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.subsystems.ShakerClaw;
import org.usfirst.frc.team2791.subsystems.ShakerDriveTrain;
import org.usfirst.frc.team2791.subsystems.ShakerIntake;
import org.usfirst.frc.team2791.subsystems.ShakerShooter;
import org.usfirst.frc.team2791.util.RoboClock;
import org.usfirst.frc.team2791.util.ShakerAccelerometer;
import org.usfirst.frc.team2791.util.ShakerCameras;

public class Robot extends IterativeRobot {
	// modes
	public static GamePeriod gamePeriod;
	// Joysticks
	public static Driver driverJoystick;
	public static Operator operatorJoystick;
	// operator subsystems
	public static ShakerShooter shooter;
	public static ShakerIntake intake;
	public static ShakerClaw claw;
	// driver subsystems
	public static ShakerDriveTrain driveTrain;
	// timers
	public static RoboClock disabledTimer;
	public static RoboClock autonTimer;
	public static RoboClock teleopTimer;
	public static RoboClock powerTimer;
	public static PowerDistributionPanel PDP;
	public static ShakerCameras cam;
	public Thread shooterThread;
	// public DigitalOutput ledDio;
	// helpers
	private TeleopHelper teleopHelper;
	private AutonHelper autonHelper;
	// other
	private Compressor compressor;
	private ShakerAccelerometer acc;
	// RoboClock stuff
	public static RoboClock getPowerTimer() {
		return powerTimer;
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
			return null;
		}
	}

	// MAIN ROBOT CODE
	@Override
	public void robotInit() {
		// Timer inits
		disabledTimer = new RoboClock();
		disabledTimer.setName("Disabled Timer");

		teleopTimer = new RoboClock();
		teleopTimer.setName("Teleop Timer");

		autonTimer = new RoboClock();
		autonTimer.setName("Auton Timer");

		powerTimer = new RoboClock();
		powerTimer.setName("Power timer");

		gamePeriod = GamePeriod.DISABLED;

		driverJoystick = new Driver();
		operatorJoystick = new Operator();
		driveTrain = new ShakerDriveTrain();

		shooter = new ShakerShooter();
		shooterThread = new Thread(shooter);
		shooterThread.start();
		intake = new ShakerIntake();
		// ledDio = new DigitalOutput(9);
		claw = new ShakerClaw();
		cam = new ShakerCameras();

		// PDP = new PowerDistributionPanel(30);
        autonHelper = new AutonHelper();
        teleopHelper = new TeleopHelper();

		compressor = new Compressor(Ports.PCM_MODULE);
		acc = new ShakerAccelerometer();
		// camServer = CameraServer.getInstance();
		// camServer.startAutomaticCapture("cam1");
	}

	@Override
	public void autonomousInit() {
		gamePeriod = GamePeriod.AUTONOMOUS;
		autonTimer.start();
	}

	@Override
	public void teleopInit() {
		gamePeriod = GamePeriod.TELEOP;

	}

	@Override
	public void disabledInit() {
		gamePeriod = GamePeriod.DISABLED;

	}

	@Override
	public void autonomousPeriodic() {
		super.autonomousPeriodic();
		autonHelper.run();
		autonHelper.updateSmartDash();
	}

	@Override
	public void teleopPeriodic() {
		super.teleopPeriodic();
		teleopHelper.run();
		teleopHelper.updateSmartDash();
		if (shooter.getIfAutoFire())
			compressor.stop();
		else
			compressor.start();
		SmartDashboard.putNumber("Acceleromter in x dir", acc.getX());
		SmartDashboard.putNumber("Acceleromter in y dir", acc.getY());
		SmartDashboard.putNumber("Acceleromter in z dir", acc.getZ());
		SmartDashboard.putNumber("Angle to floor ", acc.getAngleOffGround());

		SmartDashboard.putNumber("Gyro Rate", driveTrain.getGyroRate());
		SmartDashboard.putNumber("Current gyro angle", driveTrain.getAngle());
	

		// ledDio.set(true);
	}

	@Override
	public void disabledPeriodic() {
		super.disabledPeriodic();
		teleopHelper.disableRun();
		compressor.stop();
		autonHelper.disableRun();

		SmartDashboard.putNumber("Gyro Rate", driveTrain.getGyroRate());
		SmartDashboard.putNumber("Current gyro angle", driveTrain.getAngle());
		if (operatorJoystick.getButtonSt())
			driveTrain.calibrateGyro();
		if (operatorJoystick.getButtonSel()) {
			System.out.println("Resetting Auton step counter...");
			autonHelper.resetAutonStepCounter();
			System.out.println("Done...");
		}
		// ledDio.set(false);
	}

	public RoboClock getTeleopTimer() {
		return teleopTimer;
	}

	public RoboClock getAutonTimer() {
		return autonTimer;
	}

	public RoboClock getDisabledTimer() {
		return disabledTimer;
	}

	// ENUMS
	public enum GamePeriod {
		AUTONOMOUS, TELEOP, DISABLED
	}

}