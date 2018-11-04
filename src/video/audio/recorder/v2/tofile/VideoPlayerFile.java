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
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import video.audio.recorder.v2.ThreadsRun;
import video.audio.recorder.v2.VALogger;
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

	private static class VideoItem {

		public volatile long time;
		public volatile BufferedImage data;

	}
	
	volatile int bufferIndex;
	volatile VideoItem[] bufferImage = new VideoItem[1024];
	{
		for (int i = 0; i < bufferImage.length; i++)
			bufferImage[i] = new VideoItem();
	}
	
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

		try {
			System.out.println("Play video");
			
			
			while (!isComplete) {
				for (int i = 0; i+1 < bufferImage.length && !isComplete; i++) {
					VideoItem videoItem = bufferImage[i];
					while((videoItem.data==null || videoItem.time==0) && !isComplete) {}
					long timeBorder = 0;
					synchronized (videoItem) {
						System.out.println("Index " + i + " time[" + videoItem.time + "] " + "img " + videoItem.data);
						
						VALogger.log.append("time[" + videoItem.time + "]");
						
						screen.setImage(videoItem.data);
						VALogger.pWriteVideo++;
						audioRecorder.play( videoItem.time);
					}					
				}
			}
		} finally {
			screen.off();
			System.out.println("Loaded video:" + uploaded + ", played videp " + played);
		}

		LOG.append("Recorded img    " + VALogger.logCam.toString() + "\n");
		LOG.append("Played img    " + VALogger.log.toString() + "\n");
	}

	public void startReadingVideoFiles() {
		videoFile = VIDEO.listFiles();
		List<File> list = Arrays.asList(videoFile);
		System.out.println("Img count " + list.size() + ", " + list);
		Arrays.sort(videoFile, new Comparator<File>() {

			@Override
			public int compare(File arg0, File arg1) {
				long time1 = Long.parseLong(arg0.getName().split("\\.")[0]);
				long time2 = Long.parseLong(arg1.getName().split("\\.")[0]);
				return Long.compare(time1, time2);
			}
		});

		ThreadsRun.executorPlayer.execute(() -> {
			StringBuilder log = new StringBuilder();
			for (int i = 0; i < videoFile.length;) {
				for (bufferIndex = 0; bufferIndex < bufferImage.length && i < videoFile.length; bufferIndex++) {
					File file = videoFile[i++];
					try {
						VideoItem videoItem = bufferImage[bufferIndex];
						synchronized (videoItem) {
							videoItem.data = ImageIO.read(file);
							videoItem.time = Long.parseLong(file.getName().split(".png")[0]);	
							
							log.append("time[" + videoItem.time + "]");
							VALogger.pReadVideo++;
							System.out.println(i + " " + bufferIndex + " Video file " + file + " time " + videoItem.time + " read " + videoItem.data);
						}
						uploaded++;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			isComplete = true;
			completeIndex = bufferIndex;
			LOG.append("Buffered " + log.toString() + "\n");
		});
	}

	public void record() {

		VIDEO.mkdirs();

		isRecording = true;
		ThreadsRun.executor.execute(() -> {
			System.out.println("Start video recording");
			source.write(VideoPlayerFile.this);

		});
	}

	public void record2() {

		VIDEO.mkdirs();

		isRecording = true;

		ThreadsRun.executor.execute(() -> {
			System.out.println("Start video recording");
			int counter = source.currentBufferIndex();

			List<BufferedImage> video = new ArrayList<>();

			List<Long> time = new ArrayList<>();

			while (isRecording) {

				time.clear();

				video.clear();

				counter = source.readVideoData(counter, time, video);

				addToDisk(time, video);

				System.out.println("Video counter: " + counter);

			}

			System.out.println("Stop video recording");
		});

	}

	private void addToDisk(List<Long> time, List<BufferedImage> video) {

		for (int i = 0; i < time.size() && i < video.size(); i++) {

			File videoFile = new File(VIDEO, time.get(i) + ".png");

			try {

				videoFile.createNewFile();

				ImageIO.write(video.get(i), "PNG", videoFile);

				System.out.println("File [" + videoFile.getAbsolutePath() + "] is not readable!");

			} catch (IOException e) {

				SimpleDateFormat dateFormater = new SimpleDateFormat("HH:mm:ss");

				System.out.println("File [" + videoFile.getAbsolutePath() + "]"

						+ " current time [" + dateFormater.format(new Date()) + "], "

						+ "last modified [" + dateFormater.format(new Date(videoFile.lastModified())) + "]");

				e.printStackTrace();

			}

		}
	}

	public void stop() {
		isRecording = false;
		// while(!ThreadsRun.executor.isTerminated()) {}
	}

	public BufferedImage getImage(int i) {

		if (i < videoFile.length)
			try {
				// System.out.println("Load image: " + videoFile[i]);
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
		ThreadsRun.executor.execute(() -> {
			if (data == null)
				return;
			File videoFile = new File(VIDEO, time + ".png");
			try {
				VALogger.log2.append("time[" + time + "]");
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
		});
	}
}
