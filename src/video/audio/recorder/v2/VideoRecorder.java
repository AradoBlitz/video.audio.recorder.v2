package video.audio.recorder.v2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamResolution;

public class VideoRecorder {

	private Webcam webcam = Webcam.getDefault();

	private List<BufferedImage> video = new ArrayList<>();

	private List<Long> time = new ArrayList<>();

	public VideoRecorder() {
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		WebcamListener camListener = new WebcamListener() {

			@Override
			public void webcamOpen(WebcamEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.toString() + "webcamOpen");
			}

			@Override
			public void webcamImageObtained(WebcamEvent arg0) {

			}

			@Override
			public void webcamDisposed(WebcamEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.toString() + "webcamDisposed");
			}

			@Override
			public void webcamClosed(WebcamEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.toString() + "wbcamClosed");
			}
		};
		webcam.addWebcamListener(camListener);
	}

	public void record() {

		BufferedImage image = webcam.getImage();
		synchronized (this) {
			video.add(image);
			time.add(System.currentTimeMillis());
		}

		System.out.println("Recorded frames: " + video.size());
	}

	public void activateCam() {
		webcam.open();
	}

	public void deactivateCam() {
		webcam.close();
	}

	public void clearBuffer() {
		video.clear();
	}
	
	public BufferedImage getImage(int i) {
		if(i<video.size())
			return video.get(i);
		return null;
	}
	
	public long getTime(int i) {
		if(i<time.size())
			return time.get(i);
		return 0;
	}
}
