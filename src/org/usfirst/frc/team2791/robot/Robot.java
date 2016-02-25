package org.usfirst.frc.team2791.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.helpers.AutonHelper;
import org.usfirst.frc.team2791.helpers.TeleopHelper;
import org.usfirst.frc.team2791.shakerJoystick.Driver;
import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.subsystems.ShakerClaw;
import org.usfirst.frc.team2791.subsystems.ShakerDriveTrain;
import org.usfirst.frc.team2791.subsystems.ShakerIntake;
import org.usfirst.frc.team2791.subsystems.ShakerShooter;
import org.usfirst.frc.team2791.util.Constants;

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
        driveTrain = new ShakerDriveTrain();

        shooter = new ShakerShooter();
        shooterThread = new Thread(shooter);
        shooterThread.start();
        intake = new ShakerIntake();
        claw = new ShakerClaw();
        autonHelper = new AutonHelper();
        teleopHelper = new TeleopHelper();

        compressor = new Compressor(Constants.PCM_MODULE);
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