package org.usfirst.frc.team2791.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.helpers.AutonHelper;
import org.usfirst.frc.team2791.helpers.TeleopHelper;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticePorts;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerClaw;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerDriveTrain;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerIntake;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter;
import org.usfirst.frc.team2791.shakerJoystick.Driver;
import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.util.Constants;
import org.usfirst.frc.team2791.util.ShakerCamera;

public class Robot extends IterativeRobot {
	// modes
	public static GamePeriod gamePeriod;
	// Joysticks
	public static Driver driverJoystick;
	public static Operator operatorJoystick;
	// operator subsystems
	// public static ShakerShooter shooter;
	// public static ShakerIntake intake;
	// public static ShakerClaw claw;
	// public static ShakerDriveTrain driveTrain;

	public static PracticeShakerShooter shooter;
	public static PracticeShakerIntake intake;
	public static PracticeShakerClaw claw;
	public static PracticeShakerDriveTrain driveTrain;
	// camera
	public static ShakerCamera camera;

	// other
	public static Compressor compressor;
	public Thread shooterThread;
	// helpers
	private TeleopHelper teleopHelper;
	private AutonHelper autonHelper;

	// MAIN ROBOT CODE
	@Override
	public void robotInit() {
		gamePeriod = GamePeriod.DISABLED;

		driverJoystick = new Driver();
		operatorJoystick = new Operator();

		// driveTrain = new ShakerDriveTrain();
		// intake = new ShakerIntake();
		// claw = new ShakerClaw();
		// shooter = new ShakerShooter();

		driveTrain = new PracticeShakerDriveTrain();
		intake = new PracticeShakerIntake();
		claw = new PracticeShakerClaw();
		shooter = new PracticeShakerShooter();

		shooterThread = new Thread(shooter);
		shooterThread.start();

		autonHelper = new AutonHelper();
		teleopHelper = new TeleopHelper();

		compressor = new Compressor(Constants.PCM_MODULE);
		camera = new ShakerCamera("cam0");
	}

	@Override
	public void autonomousInit() {
		gamePeriod = GamePeriod.AUTONOMOUS;
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

		SmartDashboard.putNumber("Gyro Rate", driveTrain.getGyroRate());
		SmartDashboard.putNumber("Current gyro angle", driveTrain.getAngle());

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
	}

	// ENUMS
	public enum GamePeriod {
		AUTONOMOUS, TELEOP, DISABLED
	}

}