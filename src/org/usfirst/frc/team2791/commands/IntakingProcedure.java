package org.usfirst.frc.team2791.commands;

import static org.usfirst.frc.team2791.robot.Robot.*;
import org.usfirst.frc.team2791.practicebotSubsystems.PracticeShakerIntake.IntakeState;

public class IntakingProcedure extends ShakerCommand {
	private static int counter = 0;
	private static boolean running = false;

	public static void run() {
		if (operatorJoystick.getDpadLeft()) {
			reset();
		}
		if (intake.getIntakeState().equals(IntakeState.EXTENDED))
			switch (counter) {
			case 0:
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
			case 1:

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

}
