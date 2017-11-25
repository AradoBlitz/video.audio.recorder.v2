package video.audio.recorder.v2;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer {

	
	private final AudioRecorder source;
	
	int soundItem = 0;

	private AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

	
	private DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

	private volatile boolean isRecording;
	
	private SourceDataLine sourceLine;
	
	List<Long> time = new ArrayList<>();
	private List<byte[]> audioCollector = new ArrayList<>();

	
	public AudioPlayer(AudioRecorder source) {
		
		this.source = source;
		try {

			sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceLine.open(format);
			sourceLine.start();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	

	public void play(long timeBorder) {
		System.out.println("Play audio");
		byte[] audio;
		while(timeBorder>getTime(soundItem)&&(audio=getAudiouData(soundItem))!=null){			
			sourceLine.write(audio, 0, audio.length);
			soundItem+=1;
		}		
	}
	
	public void record() throws Exception {	
		
		new Thread(){

			public void run(){
				System.out.println("Start audio recording");
				int counter = source.currentBufferIndex();
				isRecording=true;
				while(isRecording){					
					counter = source.readAudioData(counter,time,audioCollector);
					System.out.println("counter: " + counter);
				}
				System.out.println("Stop audio recording");
				System.out.println("End audio recording. Collected " + audioCollector + "bytes.");
				
			}
		}.start();
	}
	
	public void stop(){
		isRecording=false;
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
}
