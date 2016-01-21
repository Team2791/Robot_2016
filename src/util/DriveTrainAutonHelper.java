package util;

/*
 * this class is desinged to be a support for drive train
 * it will use vision prossesing data and then give commands to bot to line up
 * 
 * STILL INCOMPLETE !!!!
 */
import java.util.ArrayList;

import javax.security.auth.login.ConfigurationSpi;

import configuration.*;
import edu.wpi.first.wpilibj.image.HSLImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import edu.wpi.first.wpilibj.vision.AxisCamera;

public class DriveTrainAutonHelper {
	private static AxisCamera cam;
	private static ArrayList<ParticleAnalysisReport> targets = new ArrayList<ParticleAnalysisReport>();

	public DriveTrainAutonHelper(AxisCamera camera) {
		cam = camera;
	}

	public String run() {
		try {
			AnalyzeCamera.determineTargets(cam.getImage());
			return deepAnalyze(targets, AnalyzeCamera.getImageWidth());

		} catch (NIVisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private String deepAnalyze(ArrayList<ParticleAnalysisReport> targets2, int imageWidth) {
		int counter = 0;
		int maxLoc = -1;
		double max = 0.0;
		if (targets2.size() > 1) {
			for (ParticleAnalysisReport report : targets2) {
				double area = report.boundingRectWidth * report.boundingRectHeight;
				if (max < area && area < Camera.maxArea) {
					max = area;
					maxLoc = counter;
				}
				counter++;
			}
		}
		if (targets2.size() == 1) {
			return findRelationToCenterOfCamera(targets2.get(1), imageWidth);
		}
		if (maxLoc != -1)
			return findRelationToCenterOfCamera(targets2.get(maxLoc), imageWidth);
		return null;
	}

	private String findRelationToCenterOfCamera(ParticleAnalysisReport par, int ImageWidth) {//returns direction robot should move
		int center = ImageWidth / 2;
		int centerMin = center - configuration.Camera.centeringDeadzone;
		int centerMax = center + configuration.Camera.centeringDeadzone;
		int locOfParticle = par.center_mass_x;
		if (locOfParticle > centerMax) {
			return "Robot:left";
		} else if (locOfParticle < centerMin) {
			return "Robot:right";
		} else if (locOfParticle > centerMin && locOfParticle < centerMax) {
			return "Robot:center";
		} else
			return null;
	}

}
