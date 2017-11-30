package video.audio.recorder.v2;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import video.audio.recorder.v2.tofile.AudioPlayerFile;
import video.audio.recorder.v2.tofile.VideoPlayerFile;

public class VideoAudioRecordingToFileTest {
	private AudioRecorder audioSource = new AudioRecorder(5000);
	private AudioPlayerFile audio = new AudioPlayerFile(audioSource);
	
	private VideoRecorder videoSource = new VideoRecorder();
	private VideoPlayerFile video = new VideoPlayerFile(videoSource);

	
	@Test
	public void captureAudio() throws Exception {
	
		audio.record();
		TimeUnit.SECONDS.sleep(10);
		audio.stop();
		//audio.playFromBuffer();
		audio.addToDisk();
		//audio.play(System.currentTimeMillis());
		audio.play(System.currentTimeMillis());
		

		
	}

	@Test
	public void test(){
		File[] listFiles = new File("audio").listFiles();
		String before = listFiles[0].getName();
		Arrays.sort(listFiles,new Comparator<File>() {

			@Override
			public int compare(File arg0, File arg1) {
				long time1 = Long.parseLong(arg0.getName());
				long time2 = Long.parseLong(arg1.getName());
				if(time1>time2){
					return -1;
				}else if(time2<time1){
					return 1;
				} else {
					return 0;
				}
			}
		});
		System.out.println(Arrays.asList(listFiles));
		assertEquals(317, listFiles.length);
		assertEquals("0", listFiles[0].listFiles()[0].getName().split("\\.")[0]);
		
	}
	
	@Test
	public void captureVideo() throws Exception {

		video.record();
		TimeUnit.SECONDS.sleep(15);
		video.stop();
		video.play();		
	}
	
	@Before
	public void startCam() throws Exception {
					
		videoSource.cameraOn();
		
		audioSource.micOn();		
	}

	@Test
	public void captureAudioAndVideo() throws Exception {
	
		video.record();
		audio.record();
		TimeUnit.SECONDS.sleep(25);
		video.stop();
		audio.stop();
		
		video.play(audio);

	}
	
	@After
	public void stopCam() throws Exception{
		
		videoSource.cameraOff();
		audioSource.micOff();
	}
}
