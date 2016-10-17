package org.usfirst.frc.team2791.helpers.autonModes;

import edu.wpi.first.wpilibj.Timer;

import static org.usfirst.frc.team2791.robot.Robot.*;

import org.usfirst.frc.team2791.commands.AutoLineUpShot;

/**
 * Created by Akhil on 4/11/2016. This class will run a sort of search pattern
 * to look for the target if it doens't find it, this is in-case that over the
 * defense the bot gets off angle
 */
public class VisionSearchBackup extends AutonMode {
	private double firstDistance;
	private double multiplier = -1.25;// This is how much the search angle
										// increases by if no target is found
	private double angle = 30;
	private Timer visionLineUpTimer;
	private double maxOutput = 0.5;

	public VisionSearchBackup(double distance, double turnAngle) {
		firstDistance = distance;
		angle = -turnAngle;
		visionLineUpTimer = new Timer();
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
			intake.extendIntake();
			state++;
			break;
		case 2:
			if (driveTrain.setDistance(firstDistance, 0, maxOutput, false)) {
				// intake.setArmAttachmentDown();
				System.out.println("Drove the first distance");
				driveTrain.resetEncoders();
				shooter.setShooterMiddle();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				state++;
			}
			break;
		case 3:
			AutoLineUpShot.setShootAfterAligned(true);
			AutoLineUpShot.setUseMultipleFrames(true);
			AutoLineUpShot.run();
			visionLineUpTimer.reset();
			visionLineUpTimer.start();
			System.out.println("Starting autoLineup");
			state++;
			break;
		case 4:
			if (camera.getTarget() == null && !AutoLineUpShot.isRunning() && visionLineUpTimer.get() < 1) {
				// That means that autolineup probably ran too quickly so do a
				// quick turn
				// to scan nearby
				state++;
			} else if (!AutoLineUpShot.isRunning()) {
				state = 7;
				System.out.println("Auto lineup is no longer running and finishing up");
			}
			break;
		case 5:
			// set the drive train to look rightward first then leftward
			if (driveTrain.setAngle(angle *= multiplier, maxOutput, false)) {
				visionLineUpTimer.reset();
				visionLineUpTimer.start();
				state = 4;
			}
			break;
		case 6:
			if (driveTrain.setAngle(-angle, maxOutput, false)) {
				driveTrain.resetEncoders();
				state++;
			}
			break;
		case 7:
			// dive back the original distance minus some amount just incase not
			// to cross the line, uncomment for backup
//			if (driveTrain.setDistance(-firstDistance - .75, 0, maxOutput, false))
//				state++;
//			break;
			state++;
		case 8:
			// turn around
			if (driveTrain.setAngle(180, maxOutput, false)) {
				state++;
			}
			break;
		case 9:
			AutoLineUpShot.reset();
			System.out.println("I am done with the drive straight auto");
			driveTrain.resetEncoders();
			state = 0;
			break;
		}
	}
}
