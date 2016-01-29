package org.usfirst.frc.team2791.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Talon;
import org.usfirst.frc.team2791.configuration.Constants;
import org.usfirst.frc.team2791.configuration.Ports;

/**
 * Created by Akhil on 1/28/2016.
 */
public class ShakerIntake extends ShakerSubsystem {
	private Talon rightIntakeMotor;
	private Talon leftIntakeMotor;
	private DoubleSolenoid intakeSolenoid;
	private IntakeState intakePos;

	public ShakerIntake() {
		init();
	}

	protected void init() {
		this.leftIntakeMotor = new Talon(Ports.INTAKE_TALON_LEFT_PORT);
		this.rightIntakeMotor = new Talon(Ports.INTAKE_TALON_RIGHT_PORT);
		leftIntakeMotor.setInverted(true);
		this.intakeSolenoid = new DoubleSolenoid(Ports.PCM_MODULE, Ports.INTAKE_PISTON_CHANNEL_FORWARD,
				Ports.INTAKE_PISTON_CHANNEL_REVERSE);
		refreshIntakeState();
	}

	public void run() {
	}

	public void update() {

	}

	public void reset() {
		extendIntake();
		stopMotors();
	}

	public void disable() {
		stopMotors();
	}

	public void retractIntake() {
		if (isIntakeRetracted()) {
			intakePos = IntakeState.RETRACTED;
			intakeSolenoid.set(Constants.INTAKE_RECTRACTED_VALUE);
		}
	}

	public void extendIntake() {
		if (!isIntakeRetracted()) {
			intakePos = IntakeState.EXTENDED;
			intakeSolenoid.set(Constants.INTAKE_EXTENDED_VALUE);
		}
	}

	public boolean isIntakeRetracted() {
		return intakePos.equals(Constants.INTAKE_RECTRACTED_VALUE) ? true : false;
	}

	private void refreshIntakeState() {
		if (intakeSolenoid.get().equals(Constants.INTAKE_RECTRACTED_VALUE))
			intakePos = IntakeState.RETRACTED;
		else if (intakeSolenoid.get().equals(Constants.INTAKE_EXTENDED_VALUE))
			intakePos = IntakeState.EXTENDED;

	}

	public void stopMotors() {
		leftIntakeMotor.set(0.0);
		rightIntakeMotor.set(0.0);
	}

	public void pullBall() {
		leftIntakeMotor.set(Constants.INTAKE_SPEED);
		rightIntakeMotor.set(Constants.INTAKE_SPEED);
	}

	public void pushBall() {
		leftIntakeMotor.set(-Constants.INTAKE_SPEED);
		rightIntakeMotor.set(-Constants.INTAKE_SPEED);

	}

	public enum IntakeState {
		RETRACTED, EXTENDED
	}
}
