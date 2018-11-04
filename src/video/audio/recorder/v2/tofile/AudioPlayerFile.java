package video.audio.recorder.v2.tofile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.junit.Assert;

import video.audio.recorder.v2.AudioRecorder;
import video.audio.recorder.v2.ThreadsRun;
import video.audio.recorder.v2.VALogger;

public class AudioPlayerFile {

	private final AudioRecorder source;

	private volatile int soundItem = 0;

	private AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

	private File[] audioFiles;

	private DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

	private volatile boolean isRecording;

	private SourceDataLine sourceLine;

	protected int totalAudioOnDisk;

	public static File AUDIO = new File("audio");

	public AudioPlayerFile(AudioRecorder source) {

		this.source = source;
		try {

			sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceLine.open(format);
			sourceLine.start();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private class AudioItem {
		volatile List<byte[]> audio;
		volatile long time;

	}

	AudioItem[] buffer = new AudioItem[2048];
	{
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = new AudioItem();
		}
	}

	volatile int bufferIndex;
	volatile boolean isComplete;

	public void startBuffering() {
		audioFiles = AUDIO.listFiles();
		Arrays.sort(audioFiles, new Comparator<File>() {

			@Override
			public int compare(File arg0, File arg1) {
				long time1 = Long.parseLong(arg0.getName());
				long time2 = Long.parseLong(arg1.getName());
				return Long.compare(time1, time2);
			}
		});

		ThreadsRun.executorPlayer.execute(() -> {
			System.out.println("audioFiles.length " + audioFiles.length);
			for (int i = 0; i < audioFiles.length;) {
				for (bufferIndex = 0; bufferIndex < buffer.length
						&& i < audioFiles.length; bufferIndex++) {
					File file = audioFiles[i++];
					buffer[bufferIndex].audio = getAudioData(file);
					buffer[bufferIndex].time = Long.parseLong(file.getName());
					System.out.println(i + " " + bufferIndex + " Audio file " + file.getName());
					VALogger.pReadAudio++;
				}
				
			}
			isComplete = true;
		});
	}
	long lastTime=0;
	public void play(long timeBorder) {

		List<byte[]> audio;
		long nextTime = nextTime();
		long count = 0;
		long prefix = 0;
		long lastTime=0;
		
		System.out.println("" + timeBorder + "<" + nextTime + "++++++++++++++++++++++++++++++++++++");
		
		
		while (nextTime != 0 && lastTime<nextTime
				&& 	timeBorder > nextTime) {			
			
			audio = buffer[soundItem].audio;
			prefix++;
			for (int i = 0; i < audio.size(); i++) {
				sourceLine.write(audio.get(i), 0, audio.get(i).length);
				System.out.println(
						"Src " + prefix + ":" + count++ + " Time border: " + timeBorder + " Current time " + nextTime);
			}
			VALogger.pWriteAudio++;
			lastTime=nextTime;
			soundItem += 1;
			if (soundItem == buffer.length) {
				soundItem = 0;
			}
			nextTime = nextTime();
		}
	}

	public long nextTime() {
		long time = buffer[soundItem].time;

		return time;
	}

	public void record() throws Exception {

		AUDIO.mkdirs();

		ThreadsRun.executor.execute(() -> {
			System.out.println("Start audio recording");
			int counter = source.currentBufferIndex();
			isRecording = true;
			List<Long> timeBuff = new ArrayList<>();
			List<byte[]> audioBuff = new ArrayList<>();

			while (isRecording) {

				timeBuff.clear();
				audioBuff.clear();
				counter = source.readAudioData(counter, timeBuff, audioBuff);
				addToDisk(timeBuff, audioBuff);
			}
			// System.out.println("Stop audio recording. Last recorded sound: " +
			// timeBuff.get(timeBuff.size() - 1));
		});

	}

	private void addToDisk(List<Long> timeList, List<byte[]> audioByteArrayList) {
		List<Long> time = new LinkedList<>(timeList);
		List<byte[]> audioCollectorLocal = new LinkedList<>(audioByteArrayList);
		ThreadsRun.executor.execute(() -> {
			long previousTime = 0;
			for (int i = 0; i < time.size() && i < audioCollectorLocal.size();) {

				previousTime = time.get(i);
				File audioSetDir = new File(AUDIO, time.get(i) + "");
				if (!audioSetDir.exists()) {
					audioSetDir.mkdir();
				}
				while (i < time.size() && previousTime == time.get(i)) {
					final byte[] bs = audioCollectorLocal.get(i);
					final int soundNumber = audioSetDir.list().length;

					VALogger.writeAudio++;

					File audioFile = new File(audioSetDir, soundNumber + ".snd");
					try {
						// System.out.println("File " + audioFile.getName() + " is exists: " +
						// audioFile.exists());
						FileOutputStream out = new FileOutputStream(audioFile);

						try {
							out.write(bs);
							// System.out.println("File [" + audioFile.getName() + "] Stored bytes: " +
							// Arrays.asList(audioCollectorLocal.get(i)));
						} finally {
							out.close();
						}
						if (!audioFile.canRead()) {
							System.out.println(
									"File [" + audioFile.getName() + "] is not readable! Before control upload.");
						}
						FileInputStream in = new FileInputStream(audioFile);
						ByteArrayOutputStream collector = new ByteArrayOutputStream();
						try {

							int counter = 0;
							byte[] buff = new byte[1024];

							while ((counter = in.read(buff)) > 0) {
								collector.write(buff, 0, counter);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							try {
								in.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						byte[] actual = collector.toByteArray();
						/*
						 * Assert.assertArrayEquals("On index " + i + " Expected " + Arrays.asList(bs) +
						 * " Actual " + Arrays.asList(actual), bs, actual);
						 */
						// System.out.println("Audio file path: " + audioFile.getAbsolutePath());
						if (!audioFile.canRead()) {
							System.out.println(
									"File [" + audioFile.canRead() + "] is not readable! After control upload.");
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					i++;
				}
			}
		});

	}

	public void stop() {
		isRecording = false;
	}

	public List<byte[]> getAudiouData(int soundItem) {
		if (soundItem < audioFiles.length) {
			return getAudioData(audioFiles[soundItem]);

		}
		return null;
	}

	private List<byte[]> getAudioData(File file) {
		List<byte[]> collector = new ArrayList<byte[]>();
		try {			 
			File[] listFiles = file.listFiles();							
			Arrays.sort(listFiles, new Comparator<File>() {

				@Override
				public int compare(File arg0, File arg1) {
					int time1 = Integer.parseInt(arg0.getName().split("\\.")[0]);
					int time2 = Integer.parseInt(arg1.getName().split("\\.")[0]);
					return Integer.compare(time1, time2);
				}
			});
			for (int i = 0; i < listFiles.length; i++) {
				FileInputStream in = new FileInputStream(listFiles[i]);
				try {

					int counter = 0;
					byte[] buff = new byte[1024];

					while ((counter = in.read(buff)) > 0) {
						collector.add(Arrays.copyOf(buff, counter));
					}
					// System.out.println("Uploaded from file: " + listFiles[i].getAbsolutePath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return collector;
	}

	public long getTime(int soundItem) {
		if (soundItem < audioFiles.length)
			return Long.parseLong(audioFiles[soundItem].getName());
		return 0;
	}

	public long lastPlayedSound() {
		int lastImage;
		soundItem += 1;
		if (soundItem == 0) {
			lastImage = buffer.length - 1;

		} else {
			lastImage = soundItem - 1;
		}
		return buffer[lastImage].time;
	}
}