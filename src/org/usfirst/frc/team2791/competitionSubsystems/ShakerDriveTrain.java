package org.usfirst.frc.team2791.competitionSubsystems;

import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team2791.abstractSubsystems.AbstractShakerDriveTrain;
import org.usfirst.frc.team2791.util.Constants;

public class ShakerDriveTrain extends AbstractShakerDriveTrain {
    private Solenoid shifterSolenoid;

    public ShakerDriveTrain() {
        super();
        // shifting solenoid
        this.shifterSolenoid = new Solenoid(Constants.PCM_MODULE, Constants.DRIVE_SHIFTING_PISTON);
    }

    public GearState getCurrentGear() {
        if (shifterSolenoid.get())
            return GearState.HIGH;
        else if (!shifterSolenoid.get())
            return GearState.LOW;
        else
            return GearState.LOW;
    }

    public void setHighGear() {
        // put the gear into the high state
        shifterSolenoid.set(true);
    }

    public void setLowGear() {
        // put gear into the low state
        shifterSolenoid.set(false);
    }
}