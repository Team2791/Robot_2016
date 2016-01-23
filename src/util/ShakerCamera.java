package util;

import configuration.Camera;
import edu.wpi.first.wpilibj.CameraServer;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ShakerCamera {
    private static int hueStart;
    private static int saturationStart;
    private static int valueStart;
    private static int hueStop;
    private static int saturationStop;
    private static int valueStop;
    private static Scalar minValues;
    private static Scalar maxValues;
    private static Mat HsvImage = new Mat();
    private static Mat maskedImage = new Mat();
    private static Mat outputImage = new Mat();
    private CameraServer camServer;

    public ShakerCamera() {
        hueStart = Camera.hueLow; //put these values on smartdasboard --> possibly changeable via sliders
        hueStop = Camera.hueHigh;
        saturationStart = Camera.saturationLow;
        saturationStop = Camera.saturationHigh;
        valueStart = Camera.valueLow;
        valueStop = Camera.valueHigh;
        minValues = new Scalar(hueStart, saturationStart, valueStart);
        maxValues = new Scalar(hueStop, saturationStop, valueStop);
        // camServer = new CameraServer();


    }

    public static void update() {

    }

    public static void getModifiedImage(Mat frame) {
        //remove image noise
        Imgproc.blur(frame, HsvImage, new Size(7, 7));
        //convert to hsv image
        Imgproc.cvtColor(HsvImage, HsvImage, Imgproc.COLOR_BGR2HSV);

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
