package util;

/*
 * This class is designed to take in images and determine if the image is in the frame of view
 */
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ParticleFilterCriteria2;

import configuration.Camera;

import java.io.IOException;
import java.util.ArrayList;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;

public class AnalyzeCamera {
	private static ColorImage frame;
	private static BinaryImage finalImage;
	private static ParticleFilterCriteria2[] criteria;
	private static int numberOfTargets = 0;
	private static int imageWidth;
	private static ArrayList<ParticleAnalysisReport> targets = new ArrayList<ParticleAnalysisReport>();

	public static ArrayList<ParticleAnalysisReport> determineTargets(ColorImage image) {
		try {
			criteria[0] = new ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA, Camera.minArea,
					Camera.maxArea, Camera.calibrated, Camera.exclude);

			// create the criteria for the particle filter
			numberOfTargets = 0;
			frame = image;
			getFinalImage(frame);
			imageWidth = finalImage.getWidth();
			if (finalImage == null)
				return null;
			numberOfTargets = finalImage.getNumberParticles();

			if (numberOfTargets == 0)
				return null;

			for (int i = 0; i < numberOfTargets; i++) {
				ParticleAnalysisReport report = finalImage.getParticleAnalysisReport(i);
				if (report == null)
					continue; // if unable to get report skip
				double WToHRat = report.boundingRectWidth / report.boundingRectHeight;
				// the target is roughly 10 : 7 ratio
				// skip particles that have a ratio of less than 21:20 ~= 1:1
				if (WToHRat < Camera.minWidHighRat)
					continue;
				// make sure the target is at least a certain number of pixels
				// wide
				if (report.boundingRectWidth > Camera.minWidth) {
					// found target that meets criteria
					targets.add(report);
				}
			}

			return targets;
		}

		catch (NIVisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static void getFinalImage(ColorImage image) throws NIVisionException {
		try {
			finalImage = image.thresholdRGB(Camera.redLow, Camera.redHigh, Camera.greenLow,
					Camera.greenHigh, Camera.blueHigh, Camera.blueHigh);
			finalImage.convexHull(true);
			finalImage.particleFilter(criteria);
			
		} catch (NIVisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getImageWidth(){
		return imageWidth;
	}
	
	public static int getDistanceRobotToGoal(){return 0;
		
	}
	public static int getDistanceRobotToTarget(){
		return 0;
	}

}
