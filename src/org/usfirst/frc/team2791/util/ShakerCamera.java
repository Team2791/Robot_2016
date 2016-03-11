package org.usfirst.frc.team2791.util;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.GetImageSizeResult;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.Range;
import com.ni.vision.NIVision.StructuringElement;
import com.ni.vision.VisionException;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;
import org.usfirst.frc.team2791.commands.AutoLineUpShot;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

public class ShakerCamera implements Runnable {
	private static ShakerCamera cameraInstance;
	// camera used usb logitech c210
	private final double CAMERA_WIDTH_DEGREES = 54.6666;
	private final double CAMERA_FOV_VERTICAL = 41;
	private final double TARGET_HT_INCHES = 90.5;
	private final double CAMERA_HT_INCHES = 26;
	private final double CAMERA_PITCH_DEG = 24;
	private long currentFrameID = 0;
	private static ArrayList<ParticleReport> particles;
	private int CAMERA_WIDTH_PIXELS = 640;
	private int CAMERA_HEIGHT_PIXELS = 480;
	private NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
	private NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0, 0, 1, 1);
	private Image frame;
	private Image binaryFrame;
	private Image particleBinaryFrame;
	private USBCamera cam;
	private Servo cameraServo;
	private int cameraExposure = 1;
	private int cameraBrightness = 1;
	private boolean cameraAutoSettings = true;
	private boolean cameraValsOnlyOnce = false;
	private TreeMap<Double, Double> rangeTable;
	private boolean alreadyMeasuredImage = false;
	private double rangeOffset = 0.0;
	private Semaphore reading_particles;

	private ShakerCamera(String camPort) {
		cam = new USBCamera(camPort);
		cam.startCapture();
		cameraServo = new Servo(Constants.CAMERA_SERVO_PORT);
		frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
		binaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
		Image filteredImage = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
		particleBinaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
		cam.setExposureManual(1);
		cam.setBrightness(0);
		cam.setSize(CAMERA_WIDTH_PIXELS, CAMERA_HEIGHT_PIXELS);
		cam.updateSettings();
		StructuringElement box = new StructuringElement(6, 4, 1);
		double AREA_MINIMUM = 7;
		criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, AREA_MINIMUM,
				100.0, 0, 0);
		SmartDashboard.putBoolean("display targetting", false);
		SmartDashboard.putBoolean("Debug Image", false);
		SmartDashboard.putNumber("H min", 75);
		SmartDashboard.putNumber("H max", 140);
		SmartDashboard.putNumber("S min", 100);
		SmartDashboard.putNumber("S max", 255);
		SmartDashboard.putNumber("L min", 60);
		SmartDashboard.putNumber("L max", 184);
		SmartDashboard.putNumber("servo angle", 100);
		// rangeTable.put(DISTANCE, RPM);
		rangeTable = new TreeMap<Double, Double>();
		rangeTable.put(95.0, 850.0);
		rangeTable.put(100.0, 850.0);
		rangeTable.put(110.0, 850.0);
		reading_particles = new Semaphore(1, true);
	}

	public static ShakerCamera getInstance() {
		if (cameraInstance == null)
			cameraInstance = new ShakerCamera("cam0");
		return cameraInstance;
	}

	public void run() {
		System.out.println("Starting the camera thread....");
		while (true) {
			try {
				// calculate the time it takes to get the image
				double imageGetTime = Timer.getFPGATimestamp();
				cam.getImage(frame);
				currentFrameID++;
				SmartDashboard.putNumber("Image get Time", imageGetTime = Timer.getFPGATimestamp() - imageGetTime);
				alreadyMeasuredImage = false;
				if (frame != null) {
					// if should display the modified image to the
					// smartdashboard
					if (SmartDashboard.getBoolean("display targetting")) {
						// before we measure the particles aquire the semaphore
						reading_particles.acquire();
						measureAndGetParticles();
						reading_particles.release();

						CameraServer.getInstance().setImage(particleBinaryFrame);
					} else if (SmartDashboard.getBoolean("Debug Image") || AutoLineUpShot.isRunning()) {
						SmartDashboard.putNumber("Distance from target", getRange());
						double processingTime = Timer.getFPGATimestamp();
						// before we measure the particles aquire the semaphore
						reading_particles.acquire();
						measureAndGetParticles();
						reading_particles.release();

						processingTime = Timer.getFPGATimestamp() - processingTime;
						SmartDashboard.putNumber("FPS with processing", 1000 / (processingTime + imageGetTime));
						GetImageSizeResult x = NIVision.imaqGetImageSize(binaryFrame);
						SmartDashboard.putString("Current Image size is ", x.width + " by " + x.height);
						NIVision.imaqDrawLineOnImage(binaryFrame, binaryFrame, NIVision.DrawMode.DRAW_VALUE,
								new NIVision.Point(x.width / 2, 0), new NIVision.Point(x.width / 2, x.height), 125f);
						NIVision.imaqDrawLineOnImage(binaryFrame, binaryFrame, NIVision.DrawMode.DRAW_VALUE,
								new NIVision.Point(0, x.height / 2), new NIVision.Point(x.width, x.height / 2), 125f);
						CameraServer.getInstance().setImage(binaryFrame);
					} else {
						SmartDashboard.putNumber("FPS without processing", (imageGetTime) * 100);
						CameraServer.getInstance().setImage(frame);

					}
				}

				if (cameraAutoSettings && !cameraValsOnlyOnce) {
					// set the exposure and the brightness for when vision
					// targetting
					cam.setExposureAuto();
					cam.setBrightness(25);
					cam.setSize(CAMERA_WIDTH_PIXELS, CAMERA_HEIGHT_PIXELS);
					cam.updateSettings();
					cameraValsOnlyOnce = true;
				} else if (!cameraValsOnlyOnce) {
					// set the exposure and the brightness for when vision
					// targetting
					cam.setExposureManual(cameraExposure);
					cam.setBrightness(cameraBrightness);
					cam.setSize(CAMERA_WIDTH_PIXELS, CAMERA_HEIGHT_PIXELS);
					cam.updateSettings();
					cameraValsOnlyOnce = true;
				}
			} catch (VisionException | InterruptedException npe) {
				System.out.println("Vision ERROR: " + npe.getMessage());
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public long getCurrentFrameID() {
		return currentFrameID;
	}

	public double getRPMForRange(double range) {
		// taken from daisycvwidget
		double lowestKey = -1;
		double lowestVal = -1;
		for (double key : rangeTable.keySet()) {
			if (range < key) {
				double highVal = rangeTable.get(key);
				if (lowestKey > 0) {
					double slope = (range - lowestKey) / (key - lowestKey);
					return lowestVal + slope * (highVal - lowestVal);
				} else
					return highVal;
			}
			lowestKey = key;
			lowestVal = rangeTable.get(key);
		}
		return 850;
	}

	public double getRange() {
		double range = -10;
		if (getTarget() != null) {
			range = (TARGET_HT_INCHES - CAMERA_HT_INCHES) / Math.tan(
					Math.toRadians((-getNormalizedCenterOfMass(getTarget().CenterOfMassY) * CAMERA_FOV_VERTICAL / 2.0
							+ CAMERA_PITCH_DEG)));
			SmartDashboard.putNumber("angle value",
					(getNormalizedCenterOfMass(getTarget().CenterOfMassY) * CAMERA_FOV_VERTICAL / 2.0
							+ CAMERA_PITCH_DEG));
			SmartDashboard.putNumber("Normalized center of mass y",
					-getNormalizedCenterOfMass(getTarget().CenterOfMassY));
		}
		// Daisy cv's =
		// (kTopTargetHeightIn-kCameraHeightIn)/Math.tan((y*kVerticalFOVDeg/2.0
		// + kCameraPitchDeg)*Math.PI/180.0);

		return range + rangeOffset;

	}

	public ParticleReport getTarget() {
		// to prevent this being modified when we're coppying it aquire the
		// semephore
		ArrayList<ParticleReport> reports = null;
		try {
			reading_particles.acquire();
			reports = (ArrayList<ParticleReport>) measureAndGetParticles().clone();
			reading_particles.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// now that it's coppied we can release the semaphore
		if (reports == null || reports.size() == 0) {
			System.out.println("The camera reports are empty");
			return null;
		} else {
			System.out.println("reports are size " + reports.size());
			System.out.println("reports contents");
			for (ParticleReport par : reports) {
				System.out.println(par);
			}

		}

		int targetLoc = 0;
		if (reports.size() != 1) {
			double maxPercentArea = 0;
			int counter = 0;
			for (ParticleReport par : reports) {
				// this shouldn't be happening but it is
				if (par == null)
					continue;
				if (maxPercentArea < par.PercentAreaToImageArea) {
					maxPercentArea = par.PercentAreaToImageArea;
					targetLoc = counter;
				}
				counter++;
			}
		}
		ParticleReport par = reports.get(targetLoc);
		// this really shouldn't be happening but it
		if (par == null)
			return null;
		// creates a rectangle to cover the target
		NIVision.Rect r = new NIVision.Rect((int) par.BoundingRectTop, (int) par.BoundingRectLeft,
				Math.abs((int) (par.BoundingRectTop - par.BoundingRectBottom)),
				Math.abs((int) (par.BoundingRectLeft - par.BoundingRectRight)));
		// draws the rectangle on the binary image
		NIVision.imaqDrawShapeOnImage(binaryFrame, binaryFrame, r, NIVision.DrawMode.DRAW_VALUE,
				NIVision.ShapeMode.SHAPE_RECT, 20f);// highlight the choosen
		// target in a different
		// color
		return reports.get(targetLoc);
	}

	private ArrayList<ParticleReport> measureAndGetParticles() {
		if (!alreadyMeasuredImage) {
			// does a bunch of measurements on the image and its particles
			// particle information is stored into the arraylist particles
			particles = new ArrayList<ParticleReport>();
			// create and apply an hsl threshold on the current fame
			NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSL,
					new Range((int) SmartDashboard.getNumber("H min"), (int) SmartDashboard.getNumber("H max")),
					new Range((int) SmartDashboard.getNumber("S min"), (int) SmartDashboard.getNumber("S max")),
					new Range((int) SmartDashboard.getNumber("L min"), (int) SmartDashboard.getNumber("L max")));
			// set the lower threshold on area in the criteria filter
			criteria[0].lower = 0.3f;
			// use particle filter to remove unwanted particles
			int imaqError = NIVision.imaqParticleFilter4(particleBinaryFrame, binaryFrame, criteria, filterOptions,
					null);
			// System.out.println("I just did a filter to remove noise");
			// count the number of viable particles
			int numParticles = NIVision.imaqCountParticles(particleBinaryFrame, 1);
			// System.out.println("I just counted the number of particles "+
			// numParticles );
			// checks to make sure there is at least one particle
			if (numParticles > 0) {
				// Measure each of the particles
				for (int particleIndex = 0; particleIndex < numParticles; particleIndex++) {
					// iterates through the particles and measures them
					ParticleReport par = new ParticleReport();
					// adds the particle report to the arraylist
					particles.add(par);
					// area / total image area

					// first do a convex hull to fill in the pariticle
					par.PercentAreaToImageArea = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
					// area of the particle
					par.Area = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_AREA);
					// Y value of the upper part of box
					par.BoundingRectTop = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
					// X value of the left part of box
					par.BoundingRectLeft = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
					// Y value of the bottom part of box
					par.BoundingRectBottom = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
					// X value of the right part of box
					par.BoundingRectRight = NIVision.imaqMeasureParticle(particleBinaryFrame, particleIndex, 0,
							NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);

					par.Height = Math.abs((int) (par.BoundingRectTop - par.BoundingRectBottom));
					par.Width = Math.abs((int) (par.BoundingRectLeft - par.BoundingRectRight));
					// measure the center of mass in the x dir
					// par.CenterOfMassX =
					// NIVision.imaqMeasureParticle(particleBinaryFrame,
					// particleIndex, 0,
					// NIVision.MeasurementType.MT_CENTER_OF_MASS_X);

					par.CenterOfMassX = par.BoundingRectRight + par.BoundingRectLeft;
					par.CenterOfMassX /= 2;
					// par.CenterOfMassX =
					// (NIVision.imaqMeasureParticle(particleBinaryFrame,
					// particleIndex, 0,
					// NIVision.MeasurementType.MT_FIRST_PIXEL_X) +
					// par.Width) / 2;

					// measure the center of mass in the y dir

					par.CenterOfMassY = par.BoundingRectTop + par.BoundingRectBottom;
					par.CenterOfMassY /= 2;
					// calculate the angle from the middle
					double angleFromMiddle = CAMERA_WIDTH_DEGREES * getNormalizedCenterOfMass(par.CenterOfMassX);
					par.ThetaDifference = angleFromMiddle / 2;
					// put the important values to the dashboard
					SmartDashboard.putNumber("Theta diff", par.ThetaDifference);
					SmartDashboard.putNumber("center of mass x", par.CenterOfMassX);
					SmartDashboard.putNumber("Boudnding rect top", par.BoundingRectTop);
					SmartDashboard.putNumber("Normalized center of mass x",
							getNormalizedCenterOfMass(par.CenterOfMassX));
					// creates a rectangle to cover the target
					NIVision.Rect r = new NIVision.Rect((int) par.BoundingRectTop, (int) par.BoundingRectLeft,
							Math.abs((int) (par.BoundingRectTop - par.BoundingRectBottom)),
							Math.abs((int) (par.BoundingRectLeft - par.BoundingRectRight)));
					// draws the rectangle on the binary image
					NIVision.imaqDrawShapeOnImage(binaryFrame, binaryFrame, r, NIVision.DrawMode.DRAW_VALUE,
							NIVision.ShapeMode.SHAPE_RECT, 125f);

				}
			}
		}
		return particles;
	}

	private void servoSetAngle(double angle) {
		// 0 is camera down
		// 1 is camera up
		// we did this because camera servo is inverted and set
		// angle = SmartDashboard.getNumber("servo angle");
		cameraServo.set((180 - angle) / 180);
	}

	public void cameraUp() {
		// // bring servo arm up
		// cameraServo.set(SmartDashboard.getNumber("servo angle"));
		servoSetAngle(106);
	}

	public void cameraDown() {
		// bring servo arm down
		servoSetAngle(0);
	}

	public void setCameraValues(int exposure, int brightness) {
		this.cameraBrightness = brightness;
		this.cameraExposure = exposure;
		this.cameraAutoSettings = false;
		cameraValsOnlyOnce = false;
	}

	public void setCameraValuesAutomatic() {
		this.cameraAutoSettings = true;
		this.CAMERA_WIDTH_PIXELS = 320;
		this.CAMERA_HEIGHT_PIXELS = 240;
		cameraValsOnlyOnce = false;
	}

	public double getNormalizedCenterOfMass(double currentCenterInPixels) {
		return ((2 * currentCenterInPixels) / CAMERA_WIDTH_PIXELS) - 1;
	}

	public static class ParticleReport {
		// a class just to hold values of the particles
		public double ThetaDifference;
		public double PercentAreaToImageArea;
		public double Area;
		public double BoundingRectLeft;
		public double BoundingRectTop;
		public double BoundingRectRight;
		public double BoundingRectBottom;
		public double CenterOfMassX;
		public double CenterOfMassY;
		public double Range;
		public double Height;
		public double Width;
	}
}