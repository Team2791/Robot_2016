package org.usfirst.frc.team2791.competitionSubsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerDriveTrain;
import org.usfirst.frc.team2791.util.Constants;
import org.usfirst.frc.team2791.util.TalonSet;

/**
 * Created by Akhil on 4/13/2016.
 * This class extends off of AbstractShakerDriveTrain, this system is setup
 * to support different use of electronics and solenoid between the practice robot
 * and the competition robot
 */

public class ShakerDriveTrain extends AbstractShakerDriveTrain {

    private Talon leftTalonA;
    private Talon leftTalonB;
    private Talon leftTalonC;
    private Talon rightTalonA;
    private Talon rightTalonB;
    private Talon rightTalonC;

    public ShakerDriveTrain() {
        super();
        // shifting solenoid
        this.leftTalonA = new Talon(Constants.DRIVE_TALON_LEFT_PORT_FRONT);
        this.leftTalonB = new Talon(Constants.DRIVE_TALON_LEFT_PORT_BACK);
        this.leftTalonC = new Talon(Constants.DRIVE_TALON_LEFT_PORT_C);

        this.rightTalonA = new Talon(Constants.DRIVE_TALON_RIGHT_PORT_FRONT);
        this.rightTalonB = new Talon(Constants.DRIVE_TALON_RIGHT_PORT_BACK);
        this.rightTalonC = new Talon(Constants.DRIVE_TALON_RIGHT_PORT_C);

        this.leftDriveEncoder = new Encoder(Constants.LEFT_DRIVE_ENCODER_PORT_A, Constants.LEFT_DRIVE_ENCODER_PORT_B);
        this.rightDriveEncoder = new Encoder(Constants.RIGHT_DRIVE_ENCOODER_PORT_A,
                Constants.RIGHT_DRIVE_ENCODER_PORT_B);
        // use the talons to create a roboDrive (it has methods that allow for easier control)
        this.robotDrive = new RobotDrive(new TalonSet(leftTalonA, leftTalonB, leftTalonC),
                new TalonSet(rightTalonA, rightTalonB, rightTalonC));
//        robotDrive = new RobotDrive(leftTalonA, leftTalonB, rightTalonA, rightTalonB);
        // stop all motors right away just in case
        robotDrive.stopMotor();
        init();
    }
}