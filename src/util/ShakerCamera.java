package util;

import com.ni.vision.NIVision;
import configuration.Camera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.vision.AxisCamera;

import static com.ni.vision.NIVision.*;


public class ShakerCamera extends AxisCamera {
    private int hueStart;
    private int saturationStart;
    private int valueStart;
    private int hueStop;
    private int saturationStop;
    private int valueStop;
    private CameraServer camServer;
    private Image frame;
    private int session;
    private Range hue;
    private Range sat;
    private Range val;
    private ParticleFilterCriteria2 criteria;
    private ParticleFilterOptions options;
    private ROI roi;


    public ShakerCamera() {
        super("cam0");
        this.hueStart = Camera.HUE_LOW; //put these values on smartdasboard --> possibly changeable via sliders
        this.hueStop = Camera.HUE_HIGH;
        this.saturationStart = Camera.SATURATION_LOW;
        this.saturationStop = Camera.SATURATION_HIGH;
        this.valueStart = Camera.VALUE_LOW;
        this.valueStop = Camera.VALUE_HIGH;
        this.hue = new Range(hueStart, hueStop);
        this.sat = new Range(saturationStart, saturationStop);
        this.val = new Range(valueStart, valueStop);
        criteria = new ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA, Camera.minArea,
                Camera.maxArea, Camera.calibrated, Camera.exclude);
        options = new ParticleFilterOptions();
        update();
    }

    public void update() {//send next camera image

        super.getImage(frame);
        //use colorizer.org
        imaqColorThreshold(frame, frame, 1, ColorMode.HSL, hue, sat, val);
        imaqParticleFilter3(frame, frame, criteria, 5, options, roi);
        imaqConvexHull(frame, frame, 4);//int is unknown and never will be known
        
        camServer.setImage(frame);

        // BinaryImage thresholdImage = frame.thresholdHSV(105, 137, 230, 255, 133, 183);C

    }


    public void getModifiedImage() {


    }

//    public void CalculateDist(Target targets) {
//        //vertical target is 32 inches fixed
//        double targetHeight = 32.0;
//
//        //get vertical pixels from targets
//        int height = targets.VerticalTarget.height;
//
//        //d = Tft*FOVpixel/(2*Tpixel*tanΘ)
//        targets.targetDistance = Y_IMAGE_RES * targetHeight
//                / (height * 12 * 2 * math.tan(VIEW_ANGLE * PI / (180 * 2)));
//    }
}
