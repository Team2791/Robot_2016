package org.usfirst.frc.team2791.practiceSubsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerDriveTrain;

public class PracticeShakerDriveTrain extends AbstractShakerDriveTrain {

    private Talon leftTalonA;
    private Talon leftTalonB;
    private Talon rightTalonA;
    private Talon rightTalonB;


    public PracticeShakerDriveTrain() {
        // instanciate the four talons for the drive train
        this.leftTalonA = new Talon(PracticePorts.DRIVE_TALON_LEFT_PORT_FRONT);
        this.leftTalonB = new Talon(PracticePorts.DRIVE_TALON_LEFT_PORT_BACK);
        this.rightTalonA = new Talon(PracticePorts.DRIVE_TALON_RIGHT_PORT_FRONT);
        this.rightTalonB = new Talon(PracticePorts.DRIVE_TALON_RIGHT_PORT_BACK);
        // use the talons to create a roboDrive (it has methods that allow for
        // easier control)
        this.robotDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
        // stop all motors right away just in case
        robotDrive.stopMotor();


        // shifting solenoid
        this.leftDriveEncoder = new Encoder(PracticePorts.LEFT_DRIVE_ENCODER_PORT_A,
                PracticePorts.LEFT_DRIVE_ENCODER_PORT_B);
        this.rightDriveEncoder = new Encoder(PracticePorts.RIGHT_DRIVE_ENCOODER_PORT_A,
                PracticePorts.RIGHT_DRIVE_ENCODER_PORT_B);

        init();
    }


}