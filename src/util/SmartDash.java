package util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;

public class SmartDash {
	Preferences pref;
	
	public SmartDash() {
		pref = Preferences.getInstance();
	}
}
