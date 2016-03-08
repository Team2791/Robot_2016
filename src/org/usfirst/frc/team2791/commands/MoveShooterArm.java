package org.usfirst.frc.team2791.commands;

import static org.usfirst.frc.team2791.robot.Robot.camera;
import static org.usfirst.frc.team2791.robot.Robot.intake;
import static org.usfirst.frc.team2791.robot.Robot.operatorJoystick;
import static org.usfirst.frc.team2791.robot.Robot.shooter;

import org.usfirst.frc.team2791.helpers.TeleopHelper;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerIntake.IntakeState;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerShooter.ShooterHeight;

public class MoveShooterArm extends ShakerCommand {
	private static boolean running = false;
	private static int counter = 0;
	private static ShooterHeight height;

	public static void run() {
		switch (counter) {
		case 0:
			height = operatorWantsToMoveArmTo();
			if (height != null) {
				if (!intake.getIntakeState().equals(IntakeState.EXTENDED))
					intake.extendIntake();
				running = true;
				counter++;
			}
		case 1:
			if (intake.getIntakeState().equals(IntakeState.EXTENDED)) {
				shooter.setShooterArm(height);
				counter++;
			}
		case 2:
			if (shooter.getShooterHeight().equals(height)) {
				intake.retractIntake();
				counter++;
			}
		case 3:
			reset();
		}

	}

	public static void reset() {
		counter = 0;
		running = false;
	}

	public static boolean isRunning() {
		return running;
	}

	public void updateSmartDash() {

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

	public void debug() {

	}

}
