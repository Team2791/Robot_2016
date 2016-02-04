package org.usfirst.frc.team2791.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.helpers.DriveHelper;
import org.usfirst.frc.team2791.helpers.OperatorHelper;
import org.usfirst.frc.team2791.shakerJoystick.Driver;
import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.subsystems.ShakerDriveTrain;
import org.usfirst.frc.team2791.subsystems.ShakerIntake;
import org.usfirst.frc.team2791.subsystems.ShakerShooter;
import org.usfirst.frc.team2791.util.RoboClock;

public class Robot extends IterativeRobot {
    // modes
    public static SafetyMode safetyMode;
    public static GamePeriod gamePeriod;
    // Joysticks
    public static Driver driverJoystick;
    public static Operator operatorJoystick;
    // operator subsystems
    public static ShakerShooter shooter;
    public static ShakerIntake intake;
    // driver subsystems
    public static ShakerDriveTrain driveTrain;
    // timers
    private static RoboClock disabledTimer;
    private static RoboClock autonTimer;
    private static RoboClock teleopTimer;
    private static RoboClock powerTimer;
    // helpers
    private DriveHelper driverHelper;
    private OperatorHelper operatorHelper;
    private Compressor compressor;
    private SendableChooser safeModeChooser;
    public Thread shooterThread;
    public DigitalOutput ledDio;
    

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

        safetyMode = SafetyMode.SAFETY;

        driverHelper = new DriveHelper();
        operatorHelper = new OperatorHelper();

        compressor = new Compressor();
        safeModeChooser = new SendableChooser();
        SmartDashboard.putData("Safe Mode Chooser", safeModeChooser);
        safeModeChooser.addDefault("Safe Mode", "SAFE");
        safeModeChooser.addObject("Test Mode (Partial Safety)", "TEST");
        safeModeChooser.addObject("Full Mode", "FULL");

        shooter = new ShakerShooter();
        shooterThread = new Thread(shooter);
        shooterThread.start();
        intake = new ShakerIntake();
        ledDio = new DigitalOutput(9);
    }

    public void autonomousInit() {
        gamePeriod = GamePeriod.AUTONOMOUS;
    }

    public void teleopInit() {
        gamePeriod = GamePeriod.TELEOP;
        if (DriverStation.getInstance().isFMSAttached()) {
            safetyMode = SafetyMode.FULL_CONTROL;
        } else {
            switch ((String) safeModeChooser.getSelected()) {
                case "FULL":
                    safetyMode = SafetyMode.FULL_CONTROL;
                    break;
                case "TEST":
                    safetyMode = SafetyMode.TEST;
                    break;
                case "SAFE":
                    safetyMode = SafetyMode.SAFETY;
                    break;
            }

        }

    }

    public void disabledInit() {
        gamePeriod = GamePeriod.DISABLED;

    }

    public void autonomousPeriodic() {
        super.autonomousPeriodic();

    }

    public void teleopPeriodic() {
        super.teleopPeriodic();
        driverHelper.teleopRun();
        driverHelper.updateSmartDash();

        operatorHelper.teleopRun();
        operatorHelper.updateSmartDash();
        compressor.start();
        ledDio.set(true);
    }

    public void disabledPeriodic() {
        super.disabledPeriodic();
        driverHelper.disableRun();
        operatorHelper.disableRun();
        compressor.stop();
        ledDio.set(false);
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

    public enum SafetyMode {
        SAFETY, TEST, FULL_CONTROL
    }

}