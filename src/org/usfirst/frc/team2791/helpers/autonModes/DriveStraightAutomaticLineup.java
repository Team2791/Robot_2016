package org.usfirst.frc.team2791.helpers.autonModes;

import static org.usfirst.frc.team2791.robot.Robot.driveTrain;
import static org.usfirst.frc.team2791.robot.Robot.shooter;
import static org.usfirst.frc.team2791.robot.Robot.intake;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerShooter.ShooterHeight;
import org.usfirst.frc.team2791.commands.AutoLineUpShot;

public class DriveStraightAutomaticLineup extends AutonMode {
	private double firstDistance;

	public DriveStraightAutomaticLineup(double distance) {
		firstDistance = distance;
	}

	public void run() {
		switch (state) {
		case 0:
			driveTrain.disable();
			shooter.stopMotors();
			break;
		case 1:
			System.out.println("Starting the drive straight autoLinup ");
			driveTrain.resetEncoders();
			state++;
			break;
		case 2:
			if (driveTrain.setDistance(firstDistance, 0, 0.8, false)) {
				intake.setArmAttachmentDown();
				System.out.println("Drove the first distance");
				driveTrain.resetEncoders();

				AutoLineUpShot.setShootAfterAligned(true);
				AutoLineUpShot.setUseMultipleFrames(true);
				state++;
			}
			break;
			case 5:
			AutoLineUpShot.run();
			System.out.println("Starting autoLineup");
			state++;
			break;
		case 6:
			if (!AutoLineUpShot.isRunning()) {
				state++;
			} else
				AutoLineUpShot.run();
			break;
		case 7:
			AutoLineUpShot.reset();
			System.out.println("I am done with the drive striaght auto");
			driveTrain.resetEncoders();
			state = 0;
			break;
		}
	}
}
