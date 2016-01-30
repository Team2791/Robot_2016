package org.usfirst.frc.team2791.helpers;

import org.usfirst.frc.team2791.shakerJoystick.Operator;
import org.usfirst.frc.team2791.subsystems.ShakerIntake;
import org.usfirst.frc.team2791.subsystems.ShakerShooter;

/**
 * Created by Akhil on 1/28/2016.
 */
public class OperatorHelper extends ShakerHelper {
	private Operator opJoy;
	private ShakerShooter shooter;
	private ShakerIntake intake;
	private double[] speed = { 0.25, 0.5, 0.75, 1.0 };
	private int currentSpeedCounter = 0;

	public OperatorHelper(Operator operatorJoystick) {
		this.opJoy = operatorJoystick;
		init();
	}

	protected void init() {
		shooter = new ShakerShooter();
		intake = new ShakerIntake();
	}

	public void teleopRun() {

		if (opJoy.getButtonRB())
			intake.pullBall();
		if (opJoy.getButtonLB())
			intake.pushBall();
		
		if (opJoy.getDpadUp())
			intake.extendIntake();
		if (opJoy.getDpadDown())
			intake.retractIntake();
		
		if (opJoy.getButtonA())
			shooter.setShooterLow();
		if (opJoy.getButtonB())
			shooter.setShooterMiddle();
		if (opJoy.getButtonY())
			shooter.setShooterHigh();
		
//		if (opJoy.getButtonSt())
//			reset();
		if(opJoy.getButtonX())
			currentSpeedCounter = 0; 
		if (opJoy.getButtonSt()) {
			if(currentSpeedCounter > 3 )
				currentSpeedCounter = 3;
			shooter.run(speed[currentSpeedCounter]);
				
		}
		if (opJoy.getButtonSel()) {
			if(currentSpeedCounter < 0 )
				currentSpeedCounter = 0;
			shooter.run(speed[currentSpeedCounter]);
				
		}
		

		// if (opJoy.getDpadRight())
		// autoShootHigh();
	}

	public void disableRun() {
		intake.disable();
		shooter.disable();
	}

	public void update() {
		intake.update();
		shooter.update();
	}

	public void reset() {
		intake.reset();
		shooter.reset();
	}

	private void autoShootHigh() {
		intake.extendIntake();
		shooter.setShooterHigh();
		shooter.run();
	}
}
