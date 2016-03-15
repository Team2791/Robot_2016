package practiceSubsystems;

import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerDriveTrain;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;

public class PracticeShakerDriveTrain extends AbstractShakerDriveTrain {
	
	private Talon leftTalonA;
	private Talon leftTalonB;
	private Talon rightTalonA;
	private Talon rightTalonB;
	
	private DoubleSolenoid driveSolenoid;
	
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
		this.driveSolenoid = new DoubleSolenoid(PracticePorts.PCM_MODULE, PracticePorts.DRIVE_PISTON_FORWARD,
				PracticePorts.DRIVE_PISTON_REVERSE);
		this.leftDriveEncoder = new Encoder(PracticePorts.LEFT_DRIVE_ENCODER_PORT_A,
				PracticePorts.LEFT_DRIVE_ENCODER_PORT_B);
		this.rightDriveEncoder = new Encoder(PracticePorts.RIGHT_DRIVE_ENCOODER_PORT_A,
				PracticePorts.RIGHT_DRIVE_ENCODER_PORT_B);
		
		this.leftDriveEncoder = new Encoder(PracticePorts.LEFT_DRIVE_ENCODER_PORT_A,
				PracticePorts.LEFT_DRIVE_ENCODER_PORT_B);
		this.rightDriveEncoder = new Encoder(PracticePorts.RIGHT_DRIVE_ENCOODER_PORT_A,
				PracticePorts.RIGHT_DRIVE_ENCODER_PORT_B);
	}
	
	public GearState getCurrentGear() {
		if (driveSolenoid.get().equals(PracticeConstants.DRIVE_HIGH_GEAR))
			return GearState.HIGH;
		else if (driveSolenoid.get().equals(PracticeConstants.DRIVE_LOW_GEAR))
			return GearState.LOW;
		else
			return GearState.LOW;
	}

	public void setHighGear() {
		// put the gear into the high state
		driveSolenoid.set(PracticeConstants.DRIVE_HIGH_GEAR);
	}

	public void setLowGear() {
		// put gear into the low state
		driveSolenoid.set(PracticeConstants.DRIVE_LOW_GEAR);
	}

}