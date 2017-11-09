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


	private AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

	private DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
	private DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

	private TargetDataLine targetLine;
	private SourceDataLine sourceLine;

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

	public void record() throws Exception{
		byte[] buff = new byte[1024];
		int count = 0;		
		for(int i = 0;i<frameCount;i++){
			count = targetLine.read(buff, 0, buff.length);
			time.add(System.currentTimeMillis());
			ByteArrayOutputStream convertor = new ByteArrayOutputStream();
			convertor.write(buff, 0, count);
			audioCollector.add(convertor.toByteArray());
		}

		System.out.println("End audio recording. Collected " + audioCollector + "bytes.");
	}

	public void play(Long timeSlot) {
		System.out.println("Play audio");
		while(soundItem<time.size()&&timeSlot>=(time.get(soundItem))){
			byte[] audio = audioCollector.get(soundItem);
			sourceLine.write(audio, 0, audio.length);
			soundItem+=1;
		}		
	}

}
