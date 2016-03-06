package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.commands.AutoLineUpShot;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter.ShooterHeight;
import org.usfirst.frc.team2791.util.Toggle;

import static org.usfirst.frc.team2791.robot.Robot.*;

/**
 * Created by Akhil on 2/14/2016.
 */
public class TeleopHelper extends ShakerHelper {
    private static TeleopHelper teleop;
    private SendableChooser driveTypeChooser;
    private Toggle useArmAttachmentToggle;
    private boolean intakingBall = false;

    private TeleopHelper() {
        // init
        // smartdashboard drop down menu
        driveTypeChooser = new SendableChooser();
        SmartDashboard.putData("Drive Chooser", driveTypeChooser);
        driveTypeChooser.addObject("Tank Drive", "TANK");
        driveTypeChooser.addObject("Arcade Drive", "ARCADE");
        driveTypeChooser.addDefault("GTA Drive", "GTA");
        driveTypeChooser.addObject("Single Arcade", "SINGLE_ARCADE");
        SmartDashboard.putNumber("Shooter Speeds Setpoint range table", 0);

        // toggles, to prevent sending a subsystem a value too many times
        // this is sort of like a light switch
        useArmAttachmentToggle = new Toggle(false);
    }

    public static TeleopHelper getInstance() {
        if (teleop == null)
            teleop = new TeleopHelper();
        return teleop;
    }

    public void run() {
        // runs the three subsystems controls
        operatorRun();// runs the operator controls
        driverRun();// runs the driver controls
        sharedRun();// runs the subsystems that are shared by both
        specialCaseRun();//this is a special case for the intake
    }

    private void driverRun() {
        // Reads the current drive type to chooser what layout should be used
        // if any of the pid driver controls are being used dont let the
        // triggers take control
        // this includes autoLineup Procedures
        if (!AutoLineUpShot.isRunning()) {
            switch (getDriveType()) {
                case TANK:
                    driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightY());
                    break;
                default:
                case GTA:
                    driveTrain.setLeftRight(driverJoystick.getGtaDriveLeft(), driverJoystick.getGtaDriveRight());
                    break;
                case ARCADE:
                    driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisRightX());
                    break;
                case SINGLE_ARCADE:
                    driveTrain.setLeftRight(-driverJoystick.getAxisLeftY(), -driverJoystick.getAxisLeftX());
                    break;
            }
            // driver control for pid movement
            // if (driverJoystick.getDpadDown())// drive back 2 feet
            // driveTrain.driveInFeet(driveTrain.getLeftDistance() - 2.0,
            // driveTrain.getAngle(), 0.4);
            // else if (driverJoystick.getDpadUp())// drive forward 2 feet
            // driveTrain.driveInFeet(driveTrain.getLeftDistance() + 2.0,
            // driveTrain.getAngle(), 0.4);
            // else if (driverJoystick.getDpadLeft())// turn 90 deg clockwise
            // driveTrain.setAngle(driveTrain.getAngle() + 90, 0.4);
            // else if (driverJoystick.getDpadRight())// 90 degrees counter
            // // clockwise
            // driveTrain.setAngle(driveTrain.getAngle() - 90, 0.4);
            // gear switching, defaults to low gear
            if (driverJoystick.getButtonB())
                driveTrain.setHighGear();
            else
                driveTrain.setLowGear();
        }
    }

    private void operatorRun() {
        // Operator button layout


        if (operatorJoystick.getButtonRS()) {
            shooter.prepShot();
        }
        if (operatorJoystick.getButtonA()) {
            shooter.autoFire();
        }

        if (operatorJoystick.getButtonRB()) {// actuation of servo arm for
            // shooter
            if (shooter.getIfAutoFire() || shooter.getIfPreppingShot())// if is currently autofiring will
                // override the auto fire
                shooter.overrideAutoShot();
            else
                shooter.pushBall();
        } else if (!shooter.getIfAutoFire() && !shooter.getIfPreppingShot())// this just brings the servo back
            // to its place if none of the
            // previous cases apply
            shooter.resetServoAngle();

        if (shooter.getIfAutoFire() || shooter.getIfPreppingShot() || AutoLineUpShot.isRunning())
            compressor.stop();
        else
            compressor.start();

        if (operatorJoystick.getButtonLB() || AutoLineUpShot.isRunning()) {
            // if operator hits start begin
            if (operatorJoystick.getButtonSt()) {
                shooter.reset();
                AutoLineUpShot.reset();
            } else {
                AutoLineUpShot.run();
            }
        }

    }

    private void sharedRun() {
        // arm attachment
        useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY() || operatorJoystick.getButtonY());
        if (useArmAttachmentToggle.getToggleOutput())
            intake.setArmAttachmentDown();
        else
            intake.setArmAttachmentUp();

    }

    private void specialCaseRun() {
        //this is for things that fall into multiple categories and
        //should be grouped together
        if (operatorJoystick.getButtonB())
            //if the intaking the ball then set intkaing ball to true
            intakingBall = true;

        if (intakingBall) {
            //if intaking ball extend the intake until the ball is gotten or overriden by
            //the operator
            intake.extendIntake();
            if (operatorJoystick.getButtonB()) {
                // Run intake inward with assistance of the shooter wheel
                shooter.setToggledShooterSpeed(-0.85, false);
                intake.pullBall();
            } else {
                shooter.setShooterSpeeds(SmartDashboard.getNumber("Shooter Speeds Setpoint range table"), true);
                //shooter.setShooterSpeeds(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT(), false);
                intake.stopMotors();
            }
            if (shooter.hasBall() || operatorJoystick.getDpadLeft())
                intakingBall = false;
        } else {
            if (operatorJoystick.getDpadUp()) {
                useArmAttachmentToggle.setManual(false);
                intake.extendIntake();
                shooter.delayedShooterPosition(ShooterHeight.HIGH);
                camera.setCameraValues(1, 1);
            } else if (operatorJoystick.getDpadRight()) {
                intake.extendIntake();
                useArmAttachmentToggle.setManual(true);
                shooter.delayedShooterPosition(ShooterHeight.MID);
                camera.setCameraValues(1, 1);
            } else if (operatorJoystick.getDpadDown()) {
                intake.extendIntake();
                useArmAttachmentToggle.setManual(false);
                camera.setCameraValuesAutomatic();
                shooter.delayedShooterPosition(ShooterHeight.LOW);
            } else if (operatorJoystick.getDpadLeft()) {
                intake.extendIntake();
                camera.cameraDown();
            } else if (operatorJoystick.getButtonX()) {
                // Run reverse if button pressed
                shooter.setToggledShooterSpeed(0.85, false);
                intake.pushBall();
            } else {
                if (shooter.getShooterHeight().equals(ShooterHeight.LOW) && (operatorJoystick.getButtonSel()))
                    camera.cameraDown();
                else
                    camera.cameraUp();

                intake.retractIntake();
            }
        }
    }

    public void disableRun() {
        // runs disable methods of subsystems that fall under the driver
        driveTrain.disable();
        shooter.disable();
        intake.disable();
        AutoLineUpShot.reset();
    }

    public void updateSmartDash() {
    }

    public void reset() {
        shooter.reset();
        intake.reset();
    }

    public void debug() {
        SmartDashboard.putString("Current Driver Input:", getDriveType().toString());
        SmartDashboard.putBoolean("Is Gyro calibrating: ", driveTrain.isGyroCalibrating());
    }

    public DriveType getDriveType() {
        // reads data of the smart dashboard and converts to enum DriveType
        String driverInputType = (String) driveTypeChooser.getSelected();
        switch (driverInputType) {
            default:
            case "GTA":
                return DriveType.GTA;
            case "ARCADE":
                return DriveType.ARCADE;
            case "TANK":
                return DriveType.TANK;
            case "SINGLE_ARCADE":
                return DriveType.SINGLE_ARCADE;
        }
    }

    public enum DriveType {
        TANK, ARCADE, GTA, SINGLE_ARCADE
    }

}
