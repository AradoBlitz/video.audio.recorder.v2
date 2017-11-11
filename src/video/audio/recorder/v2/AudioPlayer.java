package video.audio.recorder.v2;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer extends AudioRecorder {

	
	int soundItem = 0;

	private AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

	
	private DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

	
	private SourceDataLine sourceLine;



	public AudioPlayer(int frameCount) {
		
		
		try {

			sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceLine.open(format);
			sourceLine.start();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public AudioPlayer() {
		
		this(1000);
	}

	

	public void play(Long timeSlot) {
		System.out.println("Play audio");
		while(soundItem<time.size()&&timeSlot>=(getTime(soundItem))){
			byte[] audio = getAudiouData(soundItem);
			sourceLine.write(audio, 0, audio.length);
			soundItem+=1;
		}		
	}
}
