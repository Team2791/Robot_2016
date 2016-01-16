package subsystems;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;
import util.Configuration;

/**
 *
 */
public class DriveTrain{
    
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

	public void init() {
		// TODO Auto-generated method stub
		//instantiated speed controller here 
		Talon leftDriveA = new Talon(Configuration.leftDrivePortA);
		Talon leftDriveB = new Talon(Configuration.leftDrivePortB);
		Talon rightDriveA = new Talon(Configuration.rightDrivePortA);
		Talon rightDriveB = new Talon(Configuration.rightDrivePortB);
	}


	public void initTeleop() {
		// TODO Auto-generated method stub
		
	}


	public void initDisabled() {
		// TODO Auto-generated method stub
		
	}

	
	public void initAutonomous() {

		// TODO Auto-generated method stub
		
	}
	
	public void shift_up(){
		
	}
	
	public void shift_down(){
		
	}
	

	public void runTeleop() {
		// TODO Auto-generated method stub
//		switch(Configuration.drive_state)
//		case AUTO:
//		
	}


	public void runDisabled() {
		// TODO Auto-generated method stub
		
	}

	
	public void runAutonomous() {
		// TODO Auto-generated method stub
		
	}


	public void reset() {
		// TODO Auto-generated method stub
		
	}
}

