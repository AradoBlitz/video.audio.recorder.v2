package video.audio.recorder.v2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
	


	private volatile AudioItem[] rBuff = new AudioItem[frameCount];

	private volatile int buffIndex;
	{
		for(int i = 0; i<rBuff.length;i++)
			rBuff[i]=new AudioItem();
	}
	
	private static class AudioItem{

		public long time;
		public byte[] data;
		
	}
	
	public void record() throws Exception{
		
		
		
	
			//	System.out.println("Thread Collector run");
				int counter = buffIndex;

				for(int i =0;i<frameCount*frameCount;i++){					
					counter = readAudioData(counter,time,audioCollector);
					System.out.println("counter: " + counter);
				}
			
			
		

		

		System.out.println("End audio recording. Collected " + audioCollector + "bytes.");
	}
	
	public byte[] getAudiouData(int soundItem) {
		if(soundItem<audioCollector.size())
			return audioCollector.get(soundItem);
		return null;
	}

	public long getTime(int soundItem) {
		if(soundItem<time.size())
			return time.get(soundItem);
		return 0;
	}

	private int readAudioData(int counter,List<Long> time, List<byte[]> audioCollector) {
		while(counter!=buffIndex){
				time.add(rBuff[counter].time);
				audioCollector.add(rBuff[counter].data);		
				counter++;
				if(counter==rBuff.length)
					counter = 0;
		}
		return counter;
	}

	public volatile boolean isRecording = true;
	
	public void startAudioRecording() {
		System.out.println("Audio Start");
		byte[] buff = new byte[1024];
		int count = 0;		
		while(isRecording){
			count = targetLine.read(buff, 0, buff.length);
			rBuff[buffIndex].time=System.currentTimeMillis();		
			ByteArrayOutputStream convertor = new ByteArrayOutputStream();
			convertor.write(buff, 0, count);
			rBuff[buffIndex].data=convertor.toByteArray();
			
			buffIndex++;				
			if(buffIndex==rBuff.length){
				buffIndex=0;
			}

			System.out.println("buffIndex: " + buffIndex);
		}
	}
}
