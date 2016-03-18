package org.usfirst.frc.team2791.competitionSubsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerDriveTrain;
import org.usfirst.frc.team2791.util.Constants;

public class ShakerDriveTrain extends AbstractShakerDriveTrain {
    private Solenoid shifterSolenoid;
    private Talon leftTalonA;
    private Talon leftTalonB;
    private Talon rightTalonA;
    private Talon rightTalonB;

    public ShakerDriveTrain() {
        super();
        // shifting solenoid
        this.shifterSolenoid = new Solenoid(Constants.PCM_MODULE, Constants.DRIVE_SHIFTING_PISTON);
        this.leftTalonA = new Talon(Constants.DRIVE_TALON_LEFT_PORT_FRONT);
        this.leftTalonB = new Talon(Constants.DRIVE_TALON_LEFT_PORT_BACK);
        this.rightTalonA = new Talon(Constants.DRIVE_TALON_RIGHT_PORT_FRONT);
        this.rightTalonB = new Talon(Constants.DRIVE_TALON_RIGHT_PORT_BACK);
        this.leftDriveEncoder = new Encoder(Constants.LEFT_DRIVE_ENCODER_PORT_A, Constants.LEFT_DRIVE_ENCODER_PORT_B);
        this.rightDriveEncoder = new Encoder(Constants.RIGHT_DRIVE_ENCOODER_PORT_A,
                Constants.RIGHT_DRIVE_ENCODER_PORT_B);
        // use the talons to create a roboDrive (it has methods that allow for
        // easier control)
        this.robotDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
        // stop all motors right away just in case
        robotDrive.stopMotor();
        init();
    }

    public GearState getCurrentGear() {
        if (!shifterSolenoid.get())
            return GearState.HIGH;
        else if (shifterSolenoid.get())
            return GearState.LOW;
        else
            return GearState.LOW;
    }

    public void setHighGear() {
        // put the gear into the high state
        shifterSolenoid.set(false);
    }

    public void setLowGear() {
        // put gear into the low state
        shifterSolenoid.set(true);
    }
}