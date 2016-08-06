package video.audio.recorder.v2;

import java.io.ByteArrayOutputStream;
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
		}
		audio = collector.toByteArray();
		System.out.println("End audio recording. Collected " + audio.length + "bytes.");
	}

}
