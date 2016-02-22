package org.usfirst.frc.team2791.helpers;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2791.configuration.PID;

import static org.usfirst.frc.team2791.robot.Robot.*;

/**
 * Created by Akhil on 1/28/2016.
 */
public class AutonHelper extends ShakerHelper {
	private int counter = 0;
	private double setPoint = 0;
	private double lowBar_sweetSpotTime = 0;
	private double timeSinceShooterHigh = 0;
	private int microCounter = 0;
	private SendableChooser defenseNumber;
	private SendableChooser defenseToCross;

	public AutonHelper() {

		SmartDashboard.putNumber("Angle P", PID.STATIONARY_ANGLE_P);
		SmartDashboard.putNumber("Angle I", PID.STATIONARY_ANGLE_I);
		SmartDashboard.putNumber("Angle D", PID.STATIONARY_ANGLE_D);

		SmartDashboard.putNumber("DISTANCE P", PID.DRIVE_DISTANCE_P);
		SmartDashboard.putNumber("DISTANCE I", PID.DRIVE_DISTANCE_I);
		SmartDashboard.putNumber("Distance D", PID.DRIVE_DISTANCE_D);
		SmartDashboard.putNumber("Angle setpoint", setPoint);
		SmartDashboard.putNumber("pid distance travel", 1.0);
		SmartDashboard.getNumber("pid distance travel");
		SmartDashboard.putNumber("Auton step counter", counter);
		defenseNumber = new SendableChooser();// choose what number defense
												// robot is front of
		// 1 is all the way left 5 is all the way right
		SmartDashboard.putData("Auton Starting Position", defenseNumber);
		defenseNumber.addObject("1", "1");
		defenseNumber.addObject("2 Center", "2");//to center goal
		defenseNumber.addObject("2 Left", "14");// to the side goal
		defenseNumber.addObject("3", "3");
		defenseNumber.addObject("4 Center", "4");
		defenseNumber.addObject("4 Right", "15");// to side goal
		defenseNumber.addObject("5", "5");
		defenseNumber.addObject("test moving", "16");
		defenseNumber.addObject("test stationary", "17");
		defenseNumber = new SendableChooser();// 1 is all the way left 5 is all
												// the way right

		defenseToCross = new SendableChooser();
		SmartDashboard.putData("Auton Defnse type to cross", defenseToCross);
		defenseToCross.addObject("Low bar", "6");
		defenseToCross.addObject("Rough Terrain/Rock Wall/etc.", "7");
		defenseToCross.addObject("Cheval de Fries", "8");
		defenseToCross.addObject("Port Cullis", "9");
		defenseToCross.addObject("Gate", "10");
		counter = 0;
		microCounter = 0;
	}

	public void run() {
		double pidSweetSpotEnterTime = 0;
		// cases 6-10 will be how to act from after finishing defense
		int defenseStartPos = Integer.parseInt(defenseNumber.getSelected().toString());
		// 1-5 will decide how to maneuver the defense
		int defenseType = Integer.parseInt(defenseToCross.getSelected().toString());
		switch (counter) {// auton state machine
		case 0:// this state resets everything
			driveTrain.setLowGear();
			driveTrain.resetGyro();
			driveTrain.resetEncoders();
			intake.extendIntake();
			counter = defenseStartPos;
			microCounter = 0;
			break;
		// these first five cases will traverse the designated defense type
		case 1:
			if (defenseOneToShootingSpot()){
				counter = defenseType;
				driveTrain.resetEncoders();
			}
			break;
		case 2:
			if (defenseTwoToCenterShootingSpot()){
				counter = defenseType;
				driveTrain.resetEncoders();
			}
			break;
		case 3:
			if (defenseThreeToShootingSpot()){
				counter = defenseType;
				driveTrain.resetEncoders();
			}
			break;
		case 4:
			if (defenseFourToCenterShootingSpot()){
				counter = defenseType;
				driveTrain.resetEncoders();
			}
			break;
		case 5:
			if (defenseFiveToShootingSpot()){
				counter = defenseType;
				driveTrain.resetEncoders();
			}
			break;
		
		// These next 5 choose where to move after crossing the defense
		case 6:
			if(traverseLowBar())
				counter = 11;
		case 7:
			if(traverseUnevenTerrain())
				counter = 11;
		case 8:
			if(traverseFunBridges())
				counter = 11;
		case 9:
			if(traversePortCullis())
				counter = 11;
		case 10:
			if(traverseGate())
				counter = 11;
			
		case 11://11-13 are the auto shooting procedure
			shooter.setShooterHigh();
			timeSinceShooterHigh = Timer.getFPGATimestamp();
			counter++;
			break;
		case 12:
			if (Timer.getFPGATimestamp() - timeSinceShooterHigh > 0.7) {
				shooter.autoFire();
				counter++;
			}
			break;
		case 13:
			if (!shooter.getIfAutoFire())
				counter = 999;
			break;
		case 14:
			if (defenseTwoToLeftShootingSpot()){
				counter = defenseType;
				driveTrain.resetEncoders();
			}
			break;
		case 15:
			if (defenseFourToRightShootingSpot()){
				counter = defenseType;
				driveTrain.resetEncoders();
			}
			break;
		case 16:
			driveTrain.driveInFeet(SmartDashboard.getNumber("pid distance travel"),SmartDashboard.getNumber("Angle setpoint"),0.5);
			break;
			// case 1://next few cases are working low bar
			// if (driveTrain.driveInFeet(20, 0, 0.75))
			// counter++;
			// break;
			// case 2:
			// if (driveTrain.setAngle(60)) {
			// counter++;
			// driveTrain.resetEncoders();
			// }
			// break;
			// case 3:
			// if (driveTrain.driveInFeet(8, 60, 0.75)) {
			// counter++;
			// }
			// break;
			//
			// case 4:
			// shooter.setShooterHigh();
			// timeSinceShooterHigh = Timer.getFPGATimestamp();
			// counter++;
			// break;
			// case 5:
			// if (Timer.getFPGATimestamp() - timeSinceShooterHigh > 0.7) {
			// shooter.autoFire();
			// counter++;
			// }
			// break;
			// case 6:
			// if (!shooter.getIfAutoFire())
			// counter = 999;
			// break;
		case 17: //This is for testing the stationary angle pid
		driveTrain.setAngle(SmartDashboard.getNumber("Angle setpoint"));
			 break;
		default:
		case 999:
			driveTrain.setLeftRight(0, 0);
			System.out.println("Yea!!!!!!!! im done with auton");
			break;

		}
		// counter = (int) SmartDashboard.getNumber("Auton step counter");
		SmartDashboard.putNumber("Auton step counter", counter);
	}

	@Override
	public void disableRun() {
		driveTrain.disable();
		counter = 0;
	}

	@Override
	public void updateSmartDash() {

		PID.STATIONARY_ANGLE_P = SmartDashboard.getNumber("Stat Angle P");
		PID.STATIONARY_ANGLE_I = SmartDashboard.getNumber("Stat Angle I");
		PID.STATIONARY_ANGLE_D = SmartDashboard.getNumber("Stat Angle D");

		PID.DRIVE_ANGLE_P = SmartDashboard.getNumber("Angle P");
		PID.DRIVE_ANGLE_I = SmartDashboard.getNumber("Angle I");
		PID.DRIVE_ANGLE_D = SmartDashboard.getNumber("Angle D");

		PID.DRIVE_DISTANCE_P = SmartDashboard.getNumber("DISTANCE P");
		PID.DRIVE_DISTANCE_I = SmartDashboard.getNumber("DISTANCE I");
		PID.DRIVE_DISTANCE_D = SmartDashboard.getNumber("Distance D");
		driveTrain.updateSmartDash();
	}

	@Override
	public void reset() {

	}

	public boolean traverseLowBar() {// This should be the distance from the
										// neutral zone to right after the low
		// bar it waits for the pid to be good for at least 0.5 seconds before
		// giving true
		// intake.armAttachmentDown();
		intake.extendIntake();
		return driveTrain.driveInFeet(7, 0, 0.5);
		// return driveTrain.driveInFeet(19.4, 0, 0.5);
		// if (!driveTrain.driveInFeet(19.4, 0, 0.5)) {
		// lowBar_sweetSpotTime = Timer.getFPGATimestamp();
		// }
		// if (Timer.getFPGATimestamp() - lowBar_sweetSpotTime > 0.5)
		// return true;
	}

	public boolean traverseUnevenTerrain() {
		// follows the same principle as traverse low bar but allows for greater
		// output for pid
		// intake.setArmAttachmentUp();
		return !driveTrain.driveInFeet(5, 0, 0.7);
	}

	public boolean traverseFunBridges() {// this is for future autons
		return false;
	}

	public boolean traversePortCullis() {// future auton
		return false;
	}

	public boolean traverseGate() {// future auton
		return false;
	}

	public boolean defenseOneToShootingSpot() {
		if (driveTrain.driveInFeet(12.4, 0, 0.5)) {
			if (driveTrain.driveInFeet(8, 60, 0.5))
				return true;
		}

		return false;
	}
	public boolean defenseTwoToLeftShootingSpot() {
		if (driveTrain.driveInFeet(14, 0, 0.5)) {
			if (driveTrain.driveInFeet(4, 45, 0.5))
				return true;
		}

		return false;
	}
	public boolean defenseTwoToCenterShootingSpot() {
		if (driveTrain.driveInFeet(12.4, 0, 0.5)) {
			if (driveTrain.driveInFeet(4.2, 90, 0.5)){
				if(driveTrain.driveInFeet(2, -90, 0.5))
			
				return true;
			}
		}

		return false;
	}
	public boolean defenseThreeToShootingSpot() {
		if (driveTrain.driveInFeet(12, 0, 0.5)) {
			return true;
		}

		return false;
	}
	public boolean defenseFourToCenterShootingSpot() {
		if (driveTrain.driveInFeet(10, 0, 0.5)) {
			if (driveTrain.driveInFeet(4.2, -90, 0.5)){
				if(driveTrain.driveInFeet(2, 90, 0.5))
			
				return true;
			}
		}

		return false;
	}
	public boolean defenseFourToRightShootingSpot() {
		if (driveTrain.driveInFeet(14, 0, 0.5)) {
			if (driveTrain.driveInFeet(4, -45, 0.5))
				return true;
		}

		return false;
	}
	public boolean defenseFiveToShootingSpot() {
		if (driveTrain.driveInFeet(12.4, 0, 0.5)) {
			if (driveTrain.driveInFeet(8, -60, 0.5))
				return true;
		}

		return false;
	}

	public boolean overridenAutoShoot() {
		return false;
		// shooter.setShooterHigh();// brings the arm to the high pos
		// if (Timer.getFPGATimestamp() - time > 0.7) {
		// shooter.autoFire();
		// }
		//
		// return false;
	}

	public void resetAutonStepCounter() {
		counter = 0;
	}

	public enum defense { // uneven terrain applies to rough terrain,moat, etc.
		// ... things that challenge our drive train
		LOW_BAR, UNEVEN_TERRAIN
	}

	public enum autonPosition {// this is used to determine how the robot should
		// act after crossing the defense
		ONE, TWO, THREE, FOUR, FIVE
	}
}
