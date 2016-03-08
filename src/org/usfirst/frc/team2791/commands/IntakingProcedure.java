package org.usfirst.frc.team2791.commands;

import static org.usfirst.frc.team2791.robot.Robot.*;

import org.usfirst.frc.team2791.helpers.TeleopHelper;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerIntake.IntakeState;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter.ShooterHeight;

public class IntakingProcedure extends ShakerCommand {
	private static int counter = 0;
	private static boolean running = false;
	private static ShooterHeight height;
	private static boolean override = false;

	public static void run() {
		if (operatorJoystick.getDpadLeft())
			override = true;

		if (override) {
			if (!shooter.getShooterHeight().equals(ShooterHeight.MOVING))
				reset();
			else if (shooter.getShooterHeight().equals(ShooterHeight.MOVING))
				intake.extendIntake();

		} else if (intake.getIntakeState().equals(IntakeState.EXTENDED))
			switch (counter) {
			case 0:
				if (shooter.getShooterHeight().equals(ShooterHeight.MOVING))
					return;
				else if (!shooter.getShooterHeight().equals(ShooterHeight.LOW))
					shooter.setShooterLow();
				else
					counter++;
			case 1:
				if (operatorJoystick.getButtonB()) {
					// Run intake inward with assistance of the shooter wheel

					shooter.setToggledShooterSpeed(-0.85, false);
					intake.pullBall();
				} else if (operatorJoystick.getButtonX()) {
					// Run intake inward with assistance of the shooter wheel
					shooter.setToggledShooterSpeed(0.85, false);
					intake.pushBall();
				}
				if (shooter.hasBall())
					counter++;
			case 2:
				height = operatorWantsToMoveArmTo();
				if (height != null) {
					shooter.setShooterArm(height);
					counter++;
				}
			case 3:
				if (!shooter.getShooterHeight().equals(ShooterHeight.MOVING)
						&& shooter.getShooterHeight().equals(height))
					counter++;
			case 4:
				reset();
			}
		else if (intake.getIntakeState().equals(IntakeState.RETRACTED))
			intake.extendIntake();
	}

	public void updateSmartDash() {

	}

	public static boolean isRunning() {
		return running;
	}

	public static void reset() {
		running = false;
		intake.retractIntake();
		counter = 0;
	}

	public void debug() {

	}

	private static ShooterHeight operatorWantsToMoveArmTo() {
		if (operatorJoystick.getDpadUp()) {
			TeleopHelper.useArmAttachmentToggle.setManual(false);
			intake.extendIntake();
			camera.setCameraValues(1, 1);
			return ShooterHeight.HIGH;
		} else if (operatorJoystick.getDpadRight()) {
			intake.extendIntake();
			TeleopHelper.useArmAttachmentToggle.setManual(true);
			camera.setCameraValues(1, 1);
			return (ShooterHeight.MID);
		} else if (operatorJoystick.getDpadDown()) {
			intake.extendIntake();
			TeleopHelper.useArmAttachmentToggle.setManual(false);
			camera.setCameraValuesAutomatic();
			return (ShooterHeight.LOW);
		}
		return null;
	}
}
