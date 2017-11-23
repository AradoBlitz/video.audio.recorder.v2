package video.audio.recorder.v2;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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

	public void record(int frameCount) {

		/*BufferedImage image = webcam.getImage();
		video.add(image);
		time.add(System.currentTimeMillis());*/
		
		int counter = buffIndex;

		for(int i =0;i<frameCount*frameCount;i++){					
			counter = readAudioData(counter,time,video);
			System.out.println("Video counter: " + counter);
		}

		System.out.println("Recorded images: " + video.size());
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
	
	private volatile VideoItem[] rBuff = new VideoItem[1000];

	private volatile int buffIndex;
	{
		for(int i = 0; i<rBuff.length;i++)
			rBuff[i]=new VideoItem();
	}
	
	private static class VideoItem{

		public long time;
		public BufferedImage data;
		
	}
	
	private int readAudioData(int counter,List<Long> time, List<BufferedImage> videoCollector) {
		while(counter!=buffIndex){
				time.add(rBuff[counter].time);
				videoCollector.add(rBuff[counter].data);		
				counter++;
				if(counter==rBuff.length)
					counter = 0;
		}
		return counter;
	}

	public volatile boolean isRecording = true;
	
	public void startVideoRecording() {
		System.out.println("Audio Start");		
		while(isRecording){
			BufferedImage image = webcam.getImage();
			rBuff[buffIndex].time=System.currentTimeMillis();		
			rBuff[buffIndex].data=image;
			
			buffIndex++;				
			if(buffIndex==rBuff.length){
				buffIndex=0;
			}

			System.out.println("Video buffIndex: " + buffIndex);
		}
	}
}
