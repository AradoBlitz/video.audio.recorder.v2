package video.audio.recorder.v2.tofile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import video.audio.recorder.v2.VideoRecorder;
import video.audio.recorder.v2.inmemory.AudioPlayer;
import video.audio.recorder.v2.video.Screen;

public class VideoPlayerFile {

	private final VideoRecorder source;
	
	private volatile boolean isRecording = false;
	
	private List<BufferedImage> video = new ArrayList<>();

	private List<Long> time = new ArrayList<>();

	public static File VIDEO;
	
	public static File[] videoFile;

	public VideoPlayerFile(VideoRecorder source) {
		this.source = source;		
	}
	
	public void play(AudioPlayer audioRecorder) {
		Screen screen = new Screen();
		videoFile = VIDEO.listFiles();
		Arrays.sort(videoFile,new Comparator<File>() {

			@Override
			public int compare(File arg0, File arg1) {
				long time1 = Long.parseLong(arg0.getName().split("\\.")[0]);
				long time2 = Long.parseLong(arg1.getName().split("\\.")[0]);
				if(time1<time2){
					return -1;
				}else if(time2>time1){
					return 1;
				} else {
					return 0;
				}
			}
		});
		try {
			System.out.println("Play video: " + videoFile.length);
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
	
	public void play(AudioPlayerFile audioRecorder) {
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


	public void record() {

		VIDEO = new File("video");
		VIDEO.mkdirs();
		
		isRecording=true;
		new Thread(){

			@Override
			public void run() {
				System.out.println("Start video recording");
				System.out.println("Go!");//help to know to start counting.
				
				int counter = source.currentBufferIndex();

				while(isRecording){					
					counter = source.readAudioData(counter,time,video);
					addToDisk(time,video);
					System.out.println("Video counter: " + counter);
				}
				
				System.out.println("Stop video recording");
				System.out.println("Recorded images: " + video.size());
			}

			
			
		}.start();		
	}
	
	private void addToDisk(List<Long> time, List<BufferedImage> video) {
		for(int i = 0; i<time.size() && i < video.size();i++){
			File videoFile = new File(VIDEO,time.get(i) + ".png");
				try {
					ImageIO.write(video.get(i), "PNG", videoFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
		}
		
	}
	
	public void stop(){
		isRecording = false;
	}

	public BufferedImage getImage(int i) {
		videoFile = VIDEO.listFiles();
		if(i<videoFile.length)
			try {
				return ImageIO.read(videoFile[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}
	
	public long getTime(int i) {
		if(i<videoFile.length)
			return Long.parseLong(videoFile[i].getName().split(".png")[0]);
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
}
