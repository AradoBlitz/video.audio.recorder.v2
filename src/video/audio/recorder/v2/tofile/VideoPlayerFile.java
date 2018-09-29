package video.audio.recorder.v2.tofile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import video.audio.recorder.v2.VideoRecorder;
import video.audio.recorder.v2.video.Screen;

public class VideoPlayerFile {

	private final VideoRecorder source;

	private volatile boolean isRecording = false;

	public static File VIDEO = new File("video");

	public static File[] videoFile;

	public VideoPlayerFile(VideoRecorder source) {
		this.source = source;
	}

	public final StringBuilder LOG = new StringBuilder();
	
	volatile int bufferIndex;
	volatile BufferedImage[] bufferImage = new BufferedImage[150000];
	volatile long[] bufferTime = new long[150000];
	volatile boolean isComplete;
	volatile int completeIndex = -1;
	volatile long uploaded = 0;
	volatile long played = 0;

	public void play(AudioPlayerFile audioRecorder) {
		Screen screen = new Screen();
		startReadingVideoFiles();

		audioRecorder.startBuffering();
		while (uploaded < 20) {
		}
		StringBuilder log = new StringBuilder();
		try {
			System.out.println("Play video");
			for (int i = 0; bufferImage[i] != null; i++) {
				if (i == bufferImage.length) {
					i = 0;
				}
				BufferedImage image = bufferImage[i];
				long timeBorder = i + 1 < bufferTime.length ? bufferTime[i + 1] : bufferTime[i];
				bufferImage[i] = null;
				log.append("time[" + timeBorder + "]");
				screen.setImage(image);				
				audioRecorder.play(timeBorder);		
			}
		} finally {
			screen.off();
			System.out.println("Loaded video:" + uploaded + ", played videp " + played);
		}

		LOG.append("Played img    " + log.toString() + "\n");
	}

	public void startReadingVideoFiles() {
		videoFile = VIDEO.listFiles();
		Arrays.sort(videoFile, new Comparator<File>() {

			@Override
			public int compare(File arg0, File arg1) {
				long time1 = Long.parseLong(arg0.getName().split("\\.")[0]);
				long time2 = Long.parseLong(arg1.getName().split("\\.")[0]);
				return Long.compare(time1, time2);
			}
		});

		Executors.newFixedThreadPool(1).submit(() -> {
			BufferedImage image;
			StringBuilder log = new StringBuilder();
			for (int i = 0; (image = getImage(i)) != null; i++) {
				bufferImage[bufferIndex] = image;
				long time = getTime(i);
				bufferTime[bufferIndex] = time;
				log.append("time[" + time + "]");
				bufferIndex++;
				System.out.println("Video data is buffered");
				uploaded++;
			}
			isComplete = true;
			completeIndex = bufferIndex;
			LOG.append("Buffered " + log.toString() + "\n");
		});
	}

	public void record() {

		VIDEO.mkdirs();

		isRecording = true;
		new Thread() {

			@Override
			public void run() {
				System.out.println("Start video recording");
				source.write(VideoPlayerFile.this);
			}
		}.start();
	}
	StringBuilder log2 = new StringBuilder();
	private void addToDisk(List<Long> time, List<BufferedImage> video) {
		
		for (int i = 0; i < time.size() && i < video.size(); i++) {
			File videoFile = new File(VIDEO, time.get(i) + ".png");
			try {
				log2.append("time[" + time.get(i) + "]");
				videoFile.createNewFile();
				ImageIO.write(video.get(i), "PNG", videoFile);
				System.out.println("File [" + videoFile.getAbsolutePath() + "] is not readable!\n");
			} catch (IOException e) {
				SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm:ss");
				System.out.println("File [" + videoFile.getAbsolutePath() + "]" + " current time ["
						+ dateFormater.format(new Date()) + "], " + "last modified ["
						+ dateFormater.format(new Date(videoFile.lastModified())) + "]");
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		isRecording = false;
	}

	public BufferedImage getImage(int i) {

		if (i < videoFile.length)
			try {
				System.out.println("Load image: " + videoFile[i]);
				return ImageIO.read(videoFile[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}

	public long getTime(int i) {
		if (i < videoFile.length)
			return Long.parseLong(videoFile[i].getName().split(".png")[0]);
		return 0;
	}

	public void play() {
		play(new AudioPlayerFile(null) {

			@Override
			public void play(long timeBorder) {
				// TODO Auto-generated method stub
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return isRecording;
	}

	public void put(BufferedImage data, long time) {
		File videoFile = new File(VIDEO, time + ".png");
		try {
			log2.append("time[" + time + "]");
			videoFile.createNewFile();
			ImageIO.write(data, "PNG", videoFile);
			System.out.println("File [" + videoFile.getAbsolutePath() + "] is not readable!\n");
		} catch (IOException e) {
			SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm:ss");
			System.out.println("File [" + videoFile.getAbsolutePath() + "]" + " current time ["
					+ dateFormater.format(new Date()) + "], " + "last modified ["
					+ dateFormater.format(new Date(videoFile.lastModified())) + "]");
			e.printStackTrace();
		}
	}
}
