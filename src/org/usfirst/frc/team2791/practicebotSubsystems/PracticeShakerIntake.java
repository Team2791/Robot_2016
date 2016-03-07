package org.usfirst.frc.team2791.practicebotSubsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PracticeShakerIntake extends PracticeShakerSubsystem {
	private static PracticeShakerIntake practiceIntake = null;
	private Talon rightIntakeMotor;
	private Talon leftIntakeMotor;
	private DoubleSolenoid intakeSolenoid;
	private DoubleSolenoid armAttachment;
	private double timeSinceIntakeActuation;

	private PracticeShakerIntake() {
		// init
		this.leftIntakeMotor = new Talon(PracticePorts.INTAKE_TALON_LEFT_PORT);
		this.rightIntakeMotor = new Talon(PracticePorts.INTAKE_TALON_RIGHT_PORT);
		leftIntakeMotor.setInverted(true);
		this.intakeSolenoid = new DoubleSolenoid(PracticePorts.PCM_MODULE, PracticePorts.INTAKE_PISTON_CHANNEL_FORWARD,
				PracticePorts.INTAKE_PISTON_CHANNEL_REVERSE);

		armAttachment = new DoubleSolenoid(PracticePorts.PCM_MODULE, PracticePorts.INTAKE_ARM_CHANNEL_FORWARD,
				PracticePorts.INTAKE_ARM_CHANNEL_REVERSE);

	}

	public static PracticeShakerIntake getInstance() {
		if (practiceIntake == null)
			practiceIntake = new PracticeShakerIntake();
		return practiceIntake;
	}

	public void run() {
	}

	public void updateSmartDash() {
	}

	public void debug() {
		SmartDashboard.putString("Intake state", getIntakeState().toString());
	}

	public void reset() {
		// runs methods to bring back to original position
		retractIntake();
		stopMotors();
	}

	public void disable() {
		// when disabled makes sure that motors are stopped
		stopMotors();
	}

	public void retractIntake() {
		// bring intake back behind bumpers
		if (getSolenoidState().equals(IntakeState.EXTENDED))
			timeSinceIntakeActuation = Timer.getFPGATimestamp();
		intakeSolenoid.set(PracticeConstants.INTAKE_RECTRACTED_VALUE);
	}

	public void extendIntake() {
		// extends the intake for ball pickup
		if (getSolenoidState().equals(IntakeState.RETRACTED))
			timeSinceIntakeActuation = Timer.getFPGATimestamp();
		timeSinceIntakeActuation = Timer.getFPGATimestamp();
		intakeSolenoid.set(PracticeConstants.INTAKE_EXTENDED_VALUE);

	}

	private IntakeState getSolenoidState() {
		if (intakeSolenoid.get().equals(PracticeConstants.INTAKE_RECTRACTED_VALUE))
			return IntakeState.RETRACTED;
		else if (intakeSolenoid.get().equals(PracticeConstants.INTAKE_EXTENDED_VALUE))
			return IntakeState.EXTENDED;
		else
			return IntakeState.EXTENDED;
	}

	public IntakeState getIntakeState() {
		// returns state of intake in form of the enum IntakeState
		if (Timer.getFPGATimestamp() - timeSinceIntakeActuation > 2.5) {
			if (intakeSolenoid.get().equals(PracticeConstants.INTAKE_RECTRACTED_VALUE))
				return IntakeState.RETRACTED;
			else if (intakeSolenoid.get().equals(PracticeConstants.INTAKE_EXTENDED_VALUE))
				return IntakeState.EXTENDED;
			else
				return IntakeState.EXTENDED;
		}
		return IntakeState.MOVING;
	}

	public void stopMotors() {
		// sends 0 to both motors to stop them
		leftIntakeMotor.set(0.0);
		rightIntakeMotor.set(0.0);
	}

	public void pullBall() {
		// runs intake inward
		leftIntakeMotor.set(PracticeConstants.INTAKE_SPEED);
		rightIntakeMotor.set(PracticeConstants.INTAKE_SPEED);
	}

	public void pushBall() {
		// runs intake outward
		leftIntakeMotor.set(-PracticeConstants.INTAKE_SPEED);
		rightIntakeMotor.set(-PracticeConstants.INTAKE_SPEED);

	}

	public void setArmAttachmentUp() {
		// set the flipper up
		armAttachment.set(PracticeConstants.INTAKE_ARM_UP_VALUE);
	}

	public void setArmAttachmentDown() {
		// set the flipper down
		armAttachment.set(PracticeConstants.INTAKE_ARM_DOWN_VALUE);
	}

	public enum IntakeState {
		RETRACTED, EXTENDED, MOVING
	}
}
