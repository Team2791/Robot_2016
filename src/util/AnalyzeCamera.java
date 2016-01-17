package util;

/*
 * This class is designed to take in images and determine if the image is in the frame of view
 */
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ParticleFilterCriteria2;
import java.io.IOException;
import java.util.ArrayList;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import Configuration.Camera;

public class AnalyzeCamera {
	private static ColorImage frame;
	private static BinaryImage filterImage;
	private static BinaryImage finalImage;
	private static ParticleFilterCriteria2[] criteria;
	private static int numberOfTargets = 0;
	private static ArrayList<ParticleAnalysisReport> targets = new ArrayList<ParticleAnalysisReport>();

	public static ArrayList<ParticleAnalysisReport> determineTargets(ColorImage image) {
		try {
			criteria[0] = new ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA, Camera.minArea,
					Camera.maxArea, Camera.calibrated, Camera.exclude);

			// create the criteria for the particle filter
			numberOfTargets = 0;
			frame = image;
			finalImage = getFinalImage(frame);
			filterImage.free();

			if (finalImage == null)
				return null;
			numberOfTargets = getNumberParticles(finalImage);

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

	private static BinaryImage getFinalImage(ColorImage image) throws NIVisionException {
		BinaryImage threshImage = getThresholdImage(image);
		BinaryImage hullImage = getConvexHullImage(threshImage);
		filterImage = getFilteredImage(hullImage);
		threshImage.free();
		hullImage.free();
		return filterImage;
	}

	private static BinaryImage getThresholdImage(ColorImage image) {

		try {
			return image.thresholdRGB(Camera.redLow, Camera.redHigh, Camera.greenLow,
					Camera.greenHigh, Camera.blueHigh, Camera.blueHigh);
		} catch (NIVisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private static BinaryImage getFilteredImage(BinaryImage image) {
		try {
			// filter out unwanted particles
			return image.particleFilter(criteria);
		} catch (NIVisionException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private static BinaryImage getConvexHullImage(BinaryImage image) {
		try {
			image.convexHull(true);// Boolean value was connectivity8 had no
									// clue what it meant - left as true?
		} catch (NIVisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static int getNumberParticles(BinaryImage image) {
		try {
			return image.getNumberParticles();
		} catch (NIVisionException ex) {
			ex.printStackTrace();
			return 0;
		}
	}

}
