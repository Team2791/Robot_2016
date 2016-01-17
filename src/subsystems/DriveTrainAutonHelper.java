package subsystems;

/*
 * this class is desinged to be a support for drive train
 * it will use vision prossesing data and then give commands to bot to line up
 * 
 * STILL INCOMPLETE !!!!
 */
import java.util.ArrayList;

import javax.security.auth.login.ConfigurationSpi;

import edu.wpi.first.wpilibj.image.HSLImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import util.AnalyzeCamera;
import util.Configuration;

public class DriveTrainAutonHelper {
	private static AxisCamera cam;
	private static ArrayList<ParticleAnalysisReport> targets = new ArrayList<ParticleAnalysisReport>();

	public DriveTrainAutonHelper(AxisCamera camera) {
		cam = camera;
	}

	public void run() {
		try {
			AnalyzeCamera.determineTargets(cam.getImage());
			deepAnalyze(targets);

		} catch (NIVisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deepAnalyze(ArrayList<ParticleAnalysisReport> targets2) {
		if (targets2.size() > 1) {
			int counter = 0;
			double max = 0.0;
			for (ParticleAnalysisReport report : targets2) {
				double area = report.boundingRectWidth * report.boundingRectHeight;
				if (max < area && area < Configuration.maxArea) {
					max = area;
				}
				counter++;
			}
		} else {

		}

	}

}
