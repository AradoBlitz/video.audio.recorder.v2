package video.audio.recorder.v2;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer {

	
	private final AudioRecorder source;
	
	int soundItem = 0;

	private AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

	
	private DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

	
	private SourceDataLine sourceLine;

	
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
		while(timeBorder>source.getTime(soundItem)&&(audio=source.getAudiouData(soundItem))!=null){			
			sourceLine.write(audio, 0, audio.length);
			soundItem+=1;
		}		
	}
}
