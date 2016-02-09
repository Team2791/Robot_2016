package org.usfirst.frc.team2791.helpers;

import static org.usfirst.frc.team2791.robot.Robot.*;

public class OperatorHelper extends ShakerHelper {
	private double whenShotBall = 0;
	private boolean shooterIsReset = false;
	private int shooterSpeedIndex = 0;
	private boolean useShooterPID = false;
	private boolean extendIntake = false;
	private boolean useArmAttachment = false;
	private int shooterLevel = 0;

	public OperatorHelper() {
		// init
	}

	public void teleopRun() {
		// Operator button layout
		if (operatorJoystick.getButtonB()) {
			// Run intake inward with assistance of the shooter wheel
			shooter.setShooterSpeeds(-1  , false);
			intake.pullBall();
		} else if (operatorJoystick.getButtonX()) {
			// Run reverse if button pressed
			shooter.setShooterSpeeds(1, false);
			intake.pushBall();
		} else {
			// else run the manual controls
			shooter.setShooterSpeeds(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT(), useShooterPID);
			intake.stopMotors();
		}

		if (extendIntake)
			// Extend intake
			intake.extendIntake();
		else
			// Retract intake
			intake.retractIntake();

		// autofire shooter
		if (operatorJoystick.getDpadUp())
			shooter.autoFire(1.0);// currently only runs the servo back and
									// forth
		// toggle arm attachments
		if (useArmAttachment)
			intake.setArmAttachmentDown();
		else
			intake.setArmAttachmentUp();
		switch (shooterLevel) {
		case 0:
			shooter.setShooterLow();
		case 1:
			shooter.setShooterMiddle();
		case 2:
			shooter.setShooterHigh();
		}

		// Start button to reset to teleop start
		if (operatorJoystick.getButtonSt())
			reset();
		// toggle the pid
		if (operatorJoystick.getButtonSel())
			useShooterPID = !useShooterPID;
		// intake extension toggle
		if (operatorJoystick.getButtonA())
			extendIntake = !extendIntake;
		// arm attachment
		if (operatorJoystick.getButtonY())
			useArmAttachment = !useArmAttachment;
		if (operatorJoystick.getButtonRB())
			shooterLevel++;
		if (operatorJoystick.getButtonLB())
			shooterLevel--;
		shooterLevel = shooterLevel > 2 ? 2 : shooterLevel;
		shooterLevel = shooterLevel < 0 ? 0 : shooterLevel;
	}

	public void disableRun() {
		intake.disable();
		shooter.disable();
	}

	public void updateSmartDash() {
		intake.updateSmartDash();
		shooter.updateSmartDash();
	}

	public void reset() {
		shooterSpeedIndex = 0;
		shooter.reset();
		intake.reset();
	}

}
