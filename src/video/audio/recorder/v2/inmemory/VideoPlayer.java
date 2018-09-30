package video.audio.recorder.v2.inmemory;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import video.audio.recorder.v2.VideoRecorder;
import video.audio.recorder.v2.video.Screen;

public class VideoPlayer {

	private final VideoRecorder source;
	
	private volatile boolean isRecording = false;
	
	private List<BufferedImage> video = new ArrayList<>();

	private List<Long> time = new ArrayList<>();

	public VideoPlayer(VideoRecorder source) {
		this.source = source;		
	}
	
	public void play(AudioPlayer audioRecorder) {
		Screen screen = new Screen();
		try {
			System.out.println("Play video");
			long startTime = System.currentTimeMillis();
			int counter = 0;
			
			BufferedImage image;
			for (int i = 0; (image=getImage(i))!=null;i++) {
				screen.setImage(image);
				counter += 1;
				audioRecorder.play(0!=getTime(i+1)?getTime(i+1):getTime(i));
			}
			System.out.println("Frames " + counter + " was played in "
					+ TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startTime));
		} finally {
			screen.off();
		}

	}

	Screen screen;
	int currentImage = 0;
	public void play(long time) {
	
		try {
			System.out.println("Play video");
			long startTime = System.currentTimeMillis();
			int counter = 0;
			if(time>=getTime(currentImage)&&getImage(currentImage+1)!=null){
				screen.setImage(getImage(++currentImage));
				counter += 1;
			}
			System.out.println("Frames " + counter + " was played in "
					+ TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startTime));
		} finally {
			
		}
	}

	public void record() {
		isRecording=true;
		new Thread(){

			@Override
			public void run() {
				System.out.println("Start video recording");
				System.out.println("Go!");//help to know to start counting.
				
				int counter = source.currentBufferIndex();

				while(isRecording){					
					counter = source.readAudioData(counter,time,video);
				}
			}
			
		}.start();		
	}
	
	public void stop(){
		isRecording = false;
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

	public void play() {
		play(new AudioPlayer(null){

			@Override
			public void play(long timeBorder) {
				// TODO Auto-generated method stub
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		});		
	}

	public void openScreen() {
		screen = new Screen();		
	}

	public void closeScreen() {
		screen.off();
	}

	
}
