package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.Ports;
import org.usfirst.frc.team2791.robot.Robot;

public class ShakerDriveTrain extends ShakerSubsystem {
    private Talon leftTalonA;
    private Talon leftTalonB;
    private Talon rightTalonA;
    private Talon rightTalonB;
    private Encoder leftDriveEncoder;
    private Encoder rightDriveEncoder;
    private RobotDrive roboDrive;
    private DoubleSolenoid driveSolenoid;

    public ShakerDriveTrain() {
        this.leftTalonA = new Talon(Ports.DRIVE_TALON_LEFT_PORT_FRONT);
        this.leftTalonB = new Talon(Ports.DRIVE_TALON_LEFT_PORT_BACK);
        this.rightTalonA = new Talon(Ports.DRIVE_TALON_RIGHT_PORT_FRONT);
        this.rightTalonB = new Talon(Ports.DRIVE_TALON_RIGHT_PORT_BACK);
        this.roboDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
        roboDrive.stopMotor();
        this.driveSolenoid
                = new DoubleSolenoid(Ports.PCM_MODULE, Ports.DRIVE_PISTON_FORWARD, Ports.DRIVE_PISTON_REVERSE);
        leftDriveEncoder = new Encoder(Ports.LEFT_DRIVE_ENCODER_PORT_A, Ports.LEFT_DRIVE_ENCODER_PORT_B);
        rightDriveEncoder = new Encoder(Ports.RIGHT_DRIVE_ENCOODER_PORT_A, Ports.RIGHT_DRIVE_ENCODER_PORT_B);
    }

    public void run() {
        // nothing here?
    }

    public void reset() {
        this.disable();
        this.setLowGear();
        // this.setDriveType((String) driveTypeChooser.getSelected());
    }

    public void updateSmartDash() {
        SmartDashboard.putNumber("Gear : ", isHighGear() ? 2 : 1);
        SmartDashboard.putNumber("Left Drive Encoders Rate", leftDriveEncoder.getRate());
        SmartDashboard.putNumber("Right Drive Encoders Rate", rightDriveEncoder.getRate());
    }

    public void disable() {
        roboDrive.stopMotor();
    }

    public void setLeftRight(double left, double right) {
        roboDrive.setLeftRightMotorOutputs(sanitizeValue(left), sanitizeValue(right));
        if (Robot.safetyMode.equals(Robot.SafetyMode.SAFETY))
            this.setLowGear();
    }

    public void setArcadeDrive(double left, double right) {
        roboDrive.arcadeDrive(left, right);
    }

    private double sanitizeValue(double value) {
        //This is just used as a threshold for the values
        //if connected to ds station or connected overriden via smartdash will return value that is inputed
        switch (Robot.safetyMode) {
            case SAFETY:
            case TEST:
                return value * Constants.FULL_SPEED_SAFETY_MODE;

            case FULL_CONTROL:
                return value;
        }
        return value;
    }


    public boolean isHighGear() {

        switch (getCurrentGear()) {
            case HIGH:
                return true;
            case LOW:
                return false;
        }
        return false;
    }

    @NotNull
    private GearState getCurrentGear() {
        if (driveSolenoid.get().equals(Constants.DRIVE_HIGH_GEAR))
            return GearState.HIGH;
        else if (driveSolenoid.get().equals(Constants.DRIVE_LOW_GEAR))
            return GearState.LOW;
        else
            return GearState.LOW;
    }

    public void setHighGear() {
        driveSolenoid.set(Constants.DRIVE_HIGH_GEAR);

    }

    public void setLowGear() {
        driveSolenoid.set(Constants.DRIVE_LOW_GEAR);

    }


    private enum GearState {
        HIGH, LOW
    }


}
