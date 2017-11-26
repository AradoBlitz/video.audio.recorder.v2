package video.audio.recorder.v2;

import java.awt.image.BufferedImage;
import java.util.List;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamResolution;



public class VideoRecorder {

	private Webcam webcam = Webcam.getDefault();
	
	private volatile boolean isActive = true;
	
	private volatile VideoItem[] rBuff;

	private volatile int buffIndex;	
	
	private static class VideoItem{

		public long time;
		public BufferedImage data;
		
	}
	
	public VideoRecorder(){
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
			for(int i = 0; i<rBuff.length;i++)
				rBuff[i]=new VideoItem();
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
	
	public int readAudioData(int counter,List<Long> time, List<BufferedImage> videoCollector) {
		while(counter!=currentBufferIndex()){
				time.add(rBuff[counter].time);
				videoCollector.add(rBuff[counter].data);		
				counter++;
				if(counter==rBuff.length)
					counter = 0;
		}
		return counter;
	}
	
	public void cameraOn() {

		activateCam();
		new Thread() {

			@Override
			public void run() {
				System.out.println("Video Start");
				while (isActive) {
					BufferedImage image = webcam.getImage();
					rBuff[currentBufferIndex()].time = System.currentTimeMillis();
					rBuff[currentBufferIndex()].data = image;

					buffIndex++;
					if (currentBufferIndex() == rBuff.length) {
						buffIndex = 0;
					}

					System.out.println("Video buffIndex: " + buffIndex);
				}
			}
		}.start();
	}

	public void cameraOff() {
		isActive=false;
		deactivateCam();
		
	}
}
