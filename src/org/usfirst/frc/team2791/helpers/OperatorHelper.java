package org.usfirst.frc.team2791.helpers;

import static org.usfirst.frc.team2791.robot.Robot.*;

import edu.wpi.first.wpilibj.Timer;

/**
 * Created by Akhil on 1/28/2016.
 */
public class OperatorHelper extends ShakerHelper {
	private int shooterSpeedIndex = 0;
	double whenShotBall = 0;
	boolean shooterIsReset = false;

	public OperatorHelper() {
		// init
	}

	public void teleopRun() {
		// Operator button layout
		// RB - pull ball
		// LB - push Ball
		if (operatorJoystick.getButtonB())
			intake.pullBall();
		else if (operatorJoystick.getButtonX())
			intake.pushBall();
		else
			intake.stopMotors();
		// DPAD up - extend intake
		// Dpad down - retract intake
		if (operatorJoystick.getButtonA())
			intake.extendIntake();
		if (operatorJoystick.getButtonY())
			intake.retractIntake();
		// if (operatorJoystick.getButtoX())
		// shooterSpeedIndex = shooterSpeedIndex == 0 ? 0 : shooterSpeedIndex--;
		// if (operatorJoystick.getButtonRB())
		// shooterSpeedIndex = shooterSpeedIndex == 3 ? 3 : shooterSpeedIndex++;
		
		shooter.shooterSpeedsWithoutPID(operatorJoystick.getAxisRT() - operatorJoystick.getAxisLT());

		// shooter.shooterSpeedWithPID(shooterSpeedIndex);



//		if (operatorJoystick.getDpadUp())
//		{
//			System.out.println("opp pressed servo button!");
//			if (Timer.getFPGATimestamp() - whenShotBall > 0.5 && shooterIsReset) {
//				System.out.println("shooting servo!");
//				shooter.pushBall();
//				whenShotBall = Timer.getFPGATimestamp();
//			}
//		}
//		if (Timer.getFPGATimestamp() - whenShotBall > 0.5 && !shooterIsReset)
//		{
//			System.out.println("resetting servo");
//			shooter.resetServoAngle();
//			shooterIsReset = true;
//		} else {
//			System.out.println("pushing servo more!");
//			shooter.pushBall();
//		}
		if (operatorJoystick.getDpadUp()) {
			shooter.pushBall();
		} else {
			shooter.resetServoAngle();
		}

		if (operatorJoystick.getButtonSel())

		{
			shooterSpeedIndex = 0;
			shooter.stopMotors();
		}

		// Start button to reset to teleop start
		if (operatorJoystick.getButtonSt())

			reset();

		// if (opJoy.getDpadRight())
		// autoShootHigh();
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

	private void autoShootHigh() {
		intake.extendIntake();
		shooter.setShooterHigh();
		shooter.shooterSpeedWithPID(3);
	}
}
