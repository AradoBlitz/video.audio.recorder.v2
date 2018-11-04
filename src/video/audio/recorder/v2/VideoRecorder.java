package video.audio.recorder.v2;

import java.awt.image.BufferedImage;
import java.util.List;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamResolution;

import video.audio.recorder.v2.inmemory.VideoPlayer;
import video.audio.recorder.v2.tofile.VideoPlayerFile;

public class VideoRecorder {

	private Webcam webcam = Webcam.getDefault();

	private volatile boolean isActive = true;

	private VideoItem[] rBuff;

	private volatile int buffIndex;

	private static class VideoItem {

		public long time;
		public BufferedImage data;

	}

	public VideoRecorder() {
		this(1000);
	}

	public VideoRecorder(int bufferSize) {
		initBuffer(bufferSize);
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

	private void initBuffer(int bufferSize) {
		rBuff = new VideoItem[bufferSize];
		{
			for (int i = 0; i < rBuff.length; i++)
				rBuff[i] = new VideoItem();
		}
	}

	public int currentBufferIndex() {
		return buffIndex;
	}

	public void activateCam() {
		webcam.open();
	}

	public void deactivateCam() {
		webcam.close();
	}

	public int readVideoData(int counter, List<Long> time, List<BufferedImage> videoCollector) {
		while (counter != currentBufferIndex()) {
			VALogger.writeVideo++;
			VideoItem videoItem = rBuff[counter];
			synchronized (videoItem) {
				time.add(videoItem.time);
				videoCollector.add(videoItem.data);
			}
			counter++;
			if (counter == rBuff.length)
				counter = 0;
		}
		return counter;
	}

	public void cameraOn() {

		activateCam();

		ThreadsRun.executor.execute(() -> {
			// Screen screen = new Screen();
			System.out.println("Video Start");
			StringBuilder log = new StringBuilder();
			while (isActive) {
				for (; buffIndex < rBuff.length && isActive; buffIndex++) {
					VALogger.readVideo++;
					BufferedImage image = webcam.getImage();
					synchronized (this) {
						if(Thread.currentThread().isInterrupted())
							break;
						VideoItem videoItem = rBuff[buffIndex];
						// synchronized (videoItem) {
						videoItem.time = System.currentTimeMillis();
						videoItem.data = image;
						// screen.setImage(image);
						log.append("time[" + videoItem.time + "]");
					}
					// }
				}
				buffIndex = 0;
				VALogger.logCam.append(log.toString());
				log = new StringBuilder();
			}
		});
	}

	public void cameraOff() {
		isActive = false;
		deactivateCam();

	}

	public void write(VideoPlayerFile videoPlayerFile) {
		int rIndex = buffIndex;
		while (videoPlayerFile.isActive()) {
			for (; rIndex < rBuff.length && videoPlayerFile.isActive(); rIndex++) {
				int i = buffIndex;
				while (rIndex == i && videoPlayerFile.isActive()) {
					i = buffIndex;
					VALogger.writeVideoWait++;
				}
				VALogger.writeVideo++;
				VideoItem videoItem = rBuff[rIndex];
				videoPlayerFile.put(videoItem.data, videoItem.time);

			}
			rIndex = 0;
		}

	}

	public void write(VideoPlayer videoPlayerFile) {
		int rIndex = buffIndex;
		while (videoPlayerFile.isActive()) {
			for (; rIndex < rBuff.length && videoPlayerFile.isActive(); rIndex++) {
				int i = buffIndex;
				while (rIndex == i && videoPlayerFile.isActive()) {
					i = buffIndex;
				}

				VideoItem videoItem = rBuff[rIndex];
				videoPlayerFile.put(videoItem.data, videoItem.time);

			}
			rIndex = 0;
		}

	}
}
