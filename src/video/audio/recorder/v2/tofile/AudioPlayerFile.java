package video.audio.recorder.v2.tofile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.junit.Assert;

import video.audio.recorder.v2.AudioRecorder;

public class AudioPlayerFile {
	
private final AudioRecorder source;
	
    private int soundItem = 0;

	private AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

	private File[] audioFiles;
	
	private DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

	private volatile boolean isRecording;
	
	private SourceDataLine sourceLine;

	protected int totalAudioOnDisk;

	public static File AUDIO;

	
	public AudioPlayerFile(AudioRecorder source) {
		
		this.source = source;
		try {

			sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceLine.open(format);
			sourceLine.start();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private class AudioItem{
		volatile List<byte[]> audio;
		volatile long time;
		
	}
	
	AudioItem[] buffer = new AudioItem[30000];
	{
		for(int i = 0;i<buffer.length;i++){
			buffer[i]=new AudioItem();
		}		
	}
	
	volatile int bufferIndex;
	volatile boolean isComplete;
	
	public void startBuffering(){
		audioFiles = AUDIO.listFiles();
		Arrays.sort(audioFiles,new Comparator<File>() {

			@Override
			public int compare(File arg0, File arg1) {
				long time1 = Long.parseLong(arg0.getName());
				long time2 = Long.parseLong(arg1.getName());
				return Long.compare(time1, time2);
			}
		});
		
		Executors.newFixedThreadPool(1).submit(() -> {
			List<byte[]> audio;
			for (int i = 0; (audio = getAudiouData(i)) != null; i++) {
				buffer[bufferIndex].audio=audio;
				buffer[bufferIndex].time=getTime(i);
				bufferIndex++;
				if(bufferIndex==buffer.length) {
					
					bufferIndex=0;		
				}
				while(bufferIndex ==soundItem) {System.out.println("Waite for continue buffering.");}
					System.out.println("Audio data is bufferd.");		
			}
			isComplete=true;
		});
		//while(bufferIndex<1000){}
	}

	public void play(long timeBorder) {
		System.out.println("Play audio");
		
		
		System.out.println("Audio file list sorted [" + Arrays.asList(audioFiles) +"]");
		List<byte[]> fromDisc = new ArrayList<>();
		List<byte[]> audio;

		while(timeBorder>buffer[soundItem].time){
			
			audio = buffer[soundItem].audio;
			for(int i = 0;i<audio.size();i++){				
				sourceLine.write(audio.get(i), 0, audio.get(i).length);
				fromDisc.add(audio.get(i));
			}
			soundItem+=1;
			if(soundItem==buffer.length) {
				soundItem=0;
					
			}
		}
	}
	
	public void record() throws Exception {
		
		AUDIO = new File("audio");
		AUDIO.mkdirs();
		
		new Thread(){

			public void run(){
				System.out.println("Start audio recording");
				int counter = source.currentBufferIndex();
				isRecording=true;
				List<Long> timeBuff = new ArrayList<>();
				List<byte[]> audioBuff = new ArrayList<>();
				int totalAudio=0;
				totalAudioOnDisk=0;
				while(isRecording){					
					counter = source.readAudioData(counter,timeBuff,audioBuff);
					addToDisk(timeBuff, audioBuff);
					totalAudio+=timeBuff.size();					
					timeBuff.clear();
					audioBuff.clear();
					System.out.println("counter: " + counter);					
				}
				System.out.println("Stop audio recording. Total: "  + totalAudio + ". Stored to disk: " + totalAudioOnDisk);
			
				
			}

			
		}.start();
	}
	
	private void addToDisk(List<Long> time, List<byte[]> audioCollectorLocal) {
		long previousTime=0;
		for(int i = 0; i<time.size() && i < audioCollectorLocal.size();){

			previousTime = time.get(i);
			File audioSetDir = new File(AUDIO,time.get(i)+"");
			if(!audioSetDir.exists()){
						audioSetDir.mkdir();
			}
			while(i<time.size()&&previousTime == time.get(i)){
				File audioFile = new File(audioSetDir,audioSetDir.list().length + ".snd");		
				try {
					System.out.println("File " + audioFile.getName() + " is exists: " + audioFile.exists());
					FileOutputStream out = new FileOutputStream(audioFile);
					try{						
						out.write(audioCollectorLocal.get(i));
						System.out.println("File [" + audioFile.getName() + "] Stored bytes: " + Arrays.asList(audioCollectorLocal.get(i)));
					}finally {
						out.close();
					}
					if(!audioFile.canRead()) {
						System.out.println("File [" + audioFile.canRead() + "] is not readable! Before control upload.");						
					}
					FileInputStream in = new FileInputStream(audioFile);
					ByteArrayOutputStream collector = new ByteArrayOutputStream();
					try{
						
						int counter = 0;
						byte[] buff = new byte[1024];
							
						while((counter=in.read(buff))>0){
							collector.write(buff, 0,counter);
						}						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					byte[] actual = collector.toByteArray();
					Assert.assertArrayEquals("On index " + i 
							+ " Expected " + Arrays.asList(audioCollectorLocal.get(i))
							+ " Actual " + Arrays.asList(actual),audioCollectorLocal.get(i), actual);
					System.out.println("Audio file path: " + audioFile.getAbsolutePath());
					if(!audioFile.canRead()) {
						System.out.println("File [" + audioFile.canRead() + "] is not readable! After control upload.");						
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
		}
		
	}
	
	public void stop(){
		isRecording=false;
	}
	
	public List<byte[]> getAudiouData(int soundItem) {
		if(soundItem<audioFiles.length){
			try {
				List<byte[]> collector = new ArrayList<byte[]>();
				File[] listFiles = audioFiles[soundItem].listFiles();
				Arrays.sort(listFiles,new Comparator<File>() {

					@Override
					public int compare(File arg0, File arg1) {
						int time1 = Integer.parseInt(arg0.getName().split("\\.")[0]);
						int time2 = Integer.parseInt(arg1.getName().split("\\.")[0]);
						return Integer.compare(time1, time2);
					}
				});
				for(int i = 0; i < listFiles.length;i++){
					FileInputStream in = new FileInputStream(listFiles[i]);				
					try{
						
						int counter = 0;
						byte[] buff = new byte[1024];
							
						while((counter=in.read(buff))>0){
							collector.add(Arrays.copyOf(buff, counter));
						}
						System.out.println("Uploaded from file: " + listFiles[i].getAbsolutePath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}					
				}
				return collector;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
	}

	public long getTime(int soundItem) {		
		if(soundItem<audioFiles.length)
			return Long.parseLong(audioFiles[soundItem].getName());
		return 0;
	}
}