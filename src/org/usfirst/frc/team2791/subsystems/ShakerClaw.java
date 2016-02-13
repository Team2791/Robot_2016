package org.usfirst.frc.team2791.subsystems;

import org.usfirst.frc.team2791.configuration.Ports;

import edu.wpi.first.wpilibj.Solenoid;

public class ShakerClaw extends ShakerSubsystem {
	private Solenoid levelOneSolenoid;
	private Solenoid levelTwoSolenoid;

	public ShakerClaw() {
		levelOneSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.CLAW_LEVEL_ONE_SOLENOID_PORT);
		levelTwoSolenoid = new Solenoid(Ports.PCM_MODULE, Ports.CLAW_LEVEL_TWO_SOLENOID_PORT);
	}

	@Override
	public void run() {
		levelOneSolenoid.set(true);
		levelTwoSolenoid.set(true);
	}

	@Override
	public void updateSmartDash() {

	}

	public void set(boolean toggle) {
		levelOneSolenoid.set(toggle);
		levelTwoSolenoid.set(toggle);
	}

	@Override
	public void reset() {
		levelOneSolenoid.set(false);
		levelTwoSolenoid.set(false);
	}

	@Override
	public void disable() {

	}
}
