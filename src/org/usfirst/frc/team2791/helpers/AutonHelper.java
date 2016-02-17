package org.usfirst.frc.team2791.helpers;

import static org.usfirst.frc.team2791.robot.Robot.driveTrain;

import org.usfirst.frc.team2791.configuration.PID;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Created by Akhil on 1/28/2016.
 */
public class AutonHelper extends ShakerHelper {
	private int counter = 0;
	private double setPoint = 0;

	public AutonHelper() {

		SmartDashboard.putNumber("Angle P", PID.DRIVE_ANGLE_P);
		SmartDashboard.putNumber("Angle I", PID.DRIVE_ANGLE_I);
		SmartDashboard.putNumber("Angle D", PID.DRIVE_ANGLE_D);

		SmartDashboard.putNumber("DISTANCE P", PID.DRIVE_DISTANCE_P);
		SmartDashboard.putNumber("DISTANCE I", PID.DRIVE_DISTANCE_I);
		SmartDashboard.putNumber("Distance D", PID.DRIVE_DISTANCE_D);
		SmartDashboard.putNumber("Angle setpoint", setPoint);

		SmartDashboard.putNumber("pid distance travel", 1.0);
		SmartDashboard.putNumber("Auton step counter", counter);
		counter = 0;
	}

	public void run() {
		double pidSweetSpotEnterTime = 0;
		
		switch (counter) {
		default:
		case 999:
			driveTrain.setLeftRight(0, 0);
			break;
		case 0:
			driveTrain.resetGyro();
			driveTrain.resetEncoders();
			counter++;
			break;
		case 1:
			driveTrain.driveInFeet(SmartDashboard.getNumber("pid distance travel"), 
					SmartDashboard.getNumber("Angle setpoint")/12);
//			driveTrain.setAngle(SmartDashboard.getNumber("Angle setpoint")/12);
			
//		case 1:
//			SmartDashboard.getNumber("pid distance travel")
//			if (!driveTrain.driveInFeet(18*12, 0)) {
//				pidSweetSpotEnterTime = Timer.getFPGATimestamp();
//			}
//				
//			if(Timer.getFPGATimestamp() - pidSweetSpotEnterTime > 0.5)
//				counter++;
//			break;
//		case 2:
////			SmartDashboard.getNumber("pid distance travel")
//			if (!driveTrain.setAngle(60/12)) {
//				pidSweetSpotEnterTime = Timer.getFPGATimestamp();
//			}
//				
//			if(Timer.getFPGATimestamp() - pidSweetSpotEnterTime > 0.5) {
//				driveTrain.resetEncoders();
//				counter++;
//			}
//				
//			break;
//		case 3:
////			SmartDashboard.getNumber("pid distance travel")
//			if (!driveTrain.driveInFeet(5*12, 60/12)) {
//				pidSweetSpotEnterTime = Timer.getFPGATimestamp();
//			}
//				
//			if(Timer.getFPGATimestamp() - pidSweetSpotEnterTime > 0.5)
//				counter++;
//			break;


		}
//		counter = (int) SmartDashboard.getNumber("Auton step counter");
		SmartDashboard.putNumber("Auton step counter", counter);
	}

	@Override
	public void disableRun() {
		driveTrain.disable();
		counter = 0;
	}

	@Override
	public void updateSmartDash() {

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
}
