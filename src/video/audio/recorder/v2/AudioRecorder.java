package video.audio.recorder.v2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder {

	private final int frameCount;

	public byte[] audio = new byte[0];

	AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

	DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
	DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

	TargetDataLine targetLine;
	SourceDataLine sourceLine;

	private List<Long> time = new ArrayList<>();

	private int soundItem = 0;

	private List<byte[]> audioCollector = new ArrayList<>();

	public AudioRecorder(int frameCount) {
		
		this.frameCount = frameCount;
		try {

			targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
			targetLine.open(format);
			targetLine.start();

			sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceLine.open(format);
			sourceLine.start();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public AudioRecorder() {
		
		this(1000);
	}

	public void play() {
		System.out.println("Play audio");
		long startTime = System.currentTimeMillis();
		sourceLine.write(audio, 0, audio.length);
		System.out.println("Audio was played in " + TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startTime));

	}
	
	public void play(byte[] audio) {
		System.out.println("Play audio");
		long startTime = System.currentTimeMillis();
		sourceLine.write(audio, 0, audio.length);
		System.out.println("Audio was played in " + TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startTime));

	}

	public void record() throws Exception{
		byte[] buff = new byte[1024];
		int count = 0;
		ByteArrayOutputStream collector = new ByteArrayOutputStream();
		for(int i = 0;i<frameCount;i++){
			count = targetLine.read(buff, 0, buff.length);
			collector.write(buff, 0, count);
			time.add(System.currentTimeMillis());
			ByteArrayOutputStream convertor = new ByteArrayOutputStream();
			convertor.write(buff, 0, count);
			audioCollector.add(convertor.toByteArray());
		}
		audio = collector.toByteArray();
		System.out.println("End audio recording. Collected " + audio.length + "bytes.");
	}
	
	public static void main(String[] args) {
		AudioRecorder recorder = new AudioRecorder();
		try{
		while (true) {
			recorder.record();
			recorder.play();
		}
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}

	public void play(Long timeSlot) {
		System.out.println("Play audio");
		while(soundItem<time.size()&&timeSlot>=(time.get(soundItem))){			
			long startTime = System.currentTimeMillis();
			byte[] audio = audioCollector.get(soundItem);
			sourceLine.write(audio, 0, audio.length);
			soundItem+=1;
			System.out.println("Audio was played in " + TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startTime));
		}		
	}

}
