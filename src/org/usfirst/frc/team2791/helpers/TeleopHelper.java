package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.subsystems.ShakerIntake;
import org.usfirst.frc.team2791.util.Toggle;

import static org.usfirst.frc.team2791.robot.Robot.*;

/**
 * Created by Akhil on 2/14/2016.
 */
public class TeleopHelper extends ShakerHelper {
    private boolean recalibrate = false;// for gyro
    private SendableChooser driveTypeChooser;
    private Toggle clawToggle;
    private Toggle extendIntakeToggle;
    private Toggle useArmAttachmentToggle;
    private double averageVoltage = 0;
    private boolean isShooterNotLow = false;
    private boolean isOperatorOverriding = false;

    public TeleopHelper() {
        // init
        useArmAttachmentToggle = new Toggle(false);
        driveTypeChooser = new SendableChooser();
        SmartDashboard.putData("Drive Chooser", driveTypeChooser);
        driveTypeChooser.addDefault("Tank Drive", "TANK");
        driveTypeChooser.addObject("Arcade Drive", "ARCADE");
        driveTypeChooser.addObject("GTA Drive", "GTA");
        driveTypeChooser.addObject("Single Arcade", "SINGLE_ARCADE");
        clawToggle = new Toggle(false);
        extendIntakeToggle = new Toggle(false);
        useArmAttachmentToggle = new Toggle(false);
        // averageVoltage = PDP.getVoltage();
    }

    public void run() {
        // averageVoltage = 0.8 * averageVoltage + 0.2 * PDP.getVoltage();
        operatorRun();
        driverRun();
        sharedRun();
    }

    private void driverRun() {
        // Reads the current drive type to chooser what layout should be used
        // Tank: Left Stick (Y Dir) and Right Stick (Y Dir)
        // GTA: Left Trigger, Right Trigger, Right Stick (Y Dir)
        // Arcade: Left Stick (Y Dir), Right Stick (X Dir)
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

        // Driver button layout
        // RB HIGH GEAR
        // LB LOW GEAR

        if (driverJoystick.getButtonB())
            driveTrain.setLowGear();
        else
            driveTrain.setHighGear();

    }

    private void operatorRun() {
        // Operator button layout
        if (!isOperatorOverriding) {
            if (operatorJoystick.getButtonB()) {
                // Run intake inward with assistance of the shooter wheel
                shooter.setShooterSpeeds(-0.85, false);
                intake.pullBall();
            } else if (operatorJoystick.getButtonX()) {
                // Run reverse if button pressed
                shooter.setShooterSpeeds(0.85, false);
                intake.pushBall();
            } else {
                // else run the manual controls, if it is autofiring this will do
                // nothing
                shooter.setShooterSpeeds(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT(), false);
                intake.stopMotors();
            }
            // autofire shooter
            if (operatorJoystick.getButtonA())
                shooter.autoFire();// does complete shot

            if (intake.getIntakeState().equals(ShakerIntake.IntakeState.EXTENDED)) {
                // check if the intake is up before doing anything
                // set shooter to spot accordingly
                if (operatorJoystick.getDpadUp()) {
                    shooter.setShooterHigh();
                    isShooterNotLow = true;
                }
                if (operatorJoystick.getDpadRight()) {
                    shooter.setShooterMiddle();
                    isShooterNotLow = true;
                }
                if (operatorJoystick.getDpadDown()) {
                    shooter.setShooterLow();
                    isShooterNotLow = false;
                }
            }
            if (operatorJoystick.getButtonRB()) {// this will run the servo either
                // manually or override an  autoshot
                // if it is currently auto firing then it will override shot else it
                // will just run the servo
                if (shooter.getIfAutoFire())
                    shooter.overrideAutoShot();
                else
                    shooter.pushBall();
            } else if (!shooter.getIfAutoFire())// this just brings the servo back
                // to its place if none of the
                // previous cases apply
                shooter.resetServoAngle();
        }
    }

    private void sharedRun() {
// If the operator needs to override the driver for some reason they can hold the select button(back button)
        // and they will gain control over all the subsystems as well
        if (operatorJoystick.getButtonSel()) {
            isOperatorOverriding = true;
            useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY() || operatorJoystick.getButtonY());
//            if (useArmAttachmentToggle.getToggleOutput())
//                intake.setArmAttachmentDown();
//            else
//                intake.setArmAttachmentUp();
//            intake extension toggle
            extendIntakeToggle.giveToggleInput(driverJoystick.getButtonA() || operatorJoystick.getButtonA());
            if (extendIntakeToggle.getToggleOutput())// Extend intake
                intake.extendIntake();
            else// Retract intake
                intake.retractIntake();
            clawToggle.giveToggleInput(driverJoystick.getButtonX() || operatorJoystick.getButtonX());
            claw.set(clawToggle.get());
        } else {
            isOperatorOverriding = false;
            // arm attachment
            useArmAttachmentToggle.giveToggleInput(driverJoystick.getButtonY());
            // if (useArmAttachmentToggle.getToggleOutput())
            // intake.setArmAttachmentDown();
            // else
            // intake.setArmAttachmentUp();
            // intake extension toggle
            extendIntakeToggle.giveToggleInput(driverJoystick.getButtonA());
            if (extendIntakeToggle.getToggleOutput())// Extend intake
                intake.extendIntake();
            else// Retract intake
                intake.retractIntake();
            clawToggle.giveToggleInput(driverJoystick.getButtonX());
            claw.set(clawToggle.get());
        }
    }

    @Override
    public void disableRun() {
        // runs disable methods of subsystems that fall under the driver
        driveTrain.disable();
    }

    @Override
    public void updateSmartDash() {
        intake.updateSmartDash();
        shooter.updateSmartDash();
        // updateSmartDash the smartDashboard values of subsystems
        driveTrain.updateSmartDash();
        SmartDashboard.putString("Current Driver Input:", getDriveType().toString());
        SmartDashboard.putBoolean("Is Gyro calibrating: ", driveTrain.isGyroCalibrating());
        SmartDashboard.putNumber("turning value", driverJoystick.getAxisLeftX());
        // SmartDashboard.putNumber("Average Voltage", averageVoltage);
        // SmartDashboard.putNumber("Current Voltage", PDP.getVoltage());

    }

    @Override
    public void reset() {
        shooter.reset();
        intake.reset();
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
