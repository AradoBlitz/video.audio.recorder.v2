package video.audio.recorder.v2;

import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder {

	private TargetDataLine targetLine;

	private AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
	private DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);

	private int buffIndex;
	private volatile boolean isRecording = true;

	private volatile AudioItem[] rBuff;

	private static class AudioItem {

		public long time;
		public byte[] data;

	}

	public AudioRecorder() {

		this(1000);
	}

	public AudioRecorder(int bufferSize) {
		initBuffer(bufferSize);
		try {

			targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
			targetLine.open(format);
			targetLine.start();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void initBuffer(int bufferSize) {
		rBuff = new AudioItem[bufferSize];

		for (int i = 0; i < rBuff.length; i++)
			rBuff[i] = new AudioItem();

	}

	public int currentBufferIndex() {
		return buffIndex;
	}

	public int readAudioData(int counter, List<Long> time, List<byte[]> audioCollector) {
		while (counter != currentBufferIndex()) {
			time.add(rBuff[counter].time);
			audioCollector.add(rBuff[counter].data);
			counter++;
			if (counter == rBuff.length)
				counter = 0;
		}
		return counter;
	}

	public void micOn() {
		System.out.println("Audio Start");
		byte[] buff = new byte[1024];
		int count = 0;
		isRecording = true;
		while (isRecording) {
			count = targetLine.read(buff, 0, buff.length);
			rBuff[currentBufferIndex()].time = System.currentTimeMillis();
			ByteArrayOutputStream convertor = new ByteArrayOutputStream();
			convertor.write(buff, 0, count);
			rBuff[currentBufferIndex()].data = convertor.toByteArray();

			buffIndex++;
			if (currentBufferIndex() == rBuff.length) {
				buffIndex = 0;
			}

			System.out.println("buffIndex: " + buffIndex);
		}
	}

	public void micOff() {
		isRecording = false;
	}
}
