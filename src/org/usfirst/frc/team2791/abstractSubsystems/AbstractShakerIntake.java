package org.usfirst.frc.team2791.abstractSubsystems;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class AbstractShakerIntake extends ShakerSubsystem {
	public final double INTAKE_SPEED = 1.0;

	protected Talon rightIntakeMotor;
	protected Talon leftIntakeMotor;

	public AbstractShakerIntake() {
	}

	// THIS METHOD NEEDS TO BE CALLED BY THE SUB CLASS
	protected void init() {
		leftIntakeMotor.setInverted(true);
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

	public abstract void retractIntake();

	public abstract void extendIntake();

	public abstract IntakeState getIntakeState();

	public abstract void setArmAttachmentUp();

	public abstract void setArmAttachmentDown();

	public abstract boolean getArmAttachementUp();

	public boolean isExtended() {
		return getIntakeState().equals(IntakeState.EXTENDED);
	}

	public boolean isRetracted() {
		return getIntakeState().equals(IntakeState.RETRACTED);
	}

	public void stopMotors() {
		// sends 0 to both motors to stop them
		leftIntakeMotor.set(0.0);
		rightIntakeMotor.set(0.0);
	}

	public void pullBall() {
		// runs intake inward
		leftIntakeMotor.set(INTAKE_SPEED);
		rightIntakeMotor.set(INTAKE_SPEED);
	}

	public void pushBall() {
		// runs intake outward
		leftIntakeMotor.set(-INTAKE_SPEED);
		rightIntakeMotor.set(-INTAKE_SPEED);

	}

	public enum IntakeState {
		RETRACTED, EXTENDED
	}
}
