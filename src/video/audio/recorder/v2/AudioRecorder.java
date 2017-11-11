package video.audio.recorder.v2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder {
	
	private TargetDataLine targetLine;
	
	private AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
	private DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
	List<Long> time = new ArrayList<>();
	private List<byte[]> audioCollector = new ArrayList<>();
	private final int frameCount = 1000;

	public AudioRecorder() {
		try {

			targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
			targetLine.open(format);
			targetLine.start();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
	
	public byte[] getAudiouData(int soundItem) {
		// TODO Auto-generated method stub
		return audioCollector.get(soundItem);
	}

	public Long getTime(int soundItem) {

		return time.get(soundItem);
	}
}
