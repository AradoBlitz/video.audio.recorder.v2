package video.audio.recorder.v2;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import sun.security.util.Length;

public class AudioRecorder {

	public byte[] audio = new byte[0];

	AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

	DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
	DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

	TargetDataLine targetLine;
	SourceDataLine sourceLine;

	public AudioRecorder() {
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

	public void play() {
		// TODO Auto-generated method stub

	}

	public void record() throws Exception{
		byte[] buff = new byte[1024];
		int count = 0;
		ByteArrayOutputStream collector = new ByteArrayOutputStream();
		for(int i = 0;i<100;i++){
			count = targetLine.read(buff, 0, buff.length);
			collector.write(buff, 0, count);
		}
		audio = collector.toByteArray();
	}

}
