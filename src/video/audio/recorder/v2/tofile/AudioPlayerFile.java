package video.audio.recorder.v2.tofile;

import java.io.ByteArrayInputStream;
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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.junit.Assert;

import com.sun.xml.internal.ws.encoding.MtomCodec.ByteArrayBuffer;

import video.audio.recorder.v2.AudioRecorder;

public class AudioPlayerFile {
	
private final AudioRecorder source;
	
	int soundItem = 0;

	private AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

	private File[] audioFiles;
	
	private DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

	private volatile boolean isRecording;
	
	private SourceDataLine sourceLine;
	
	List<Long> time = new ArrayList<>();
	private List<byte[]> audioCollector = new ArrayList<>();

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

	public void playFromBuffer() {
		play(System.currentTimeMillis());
		System.out.println("Buffered audio: " + audioCollector.size());
		for(int i = 0; i<audioCollector.size();i++){
			sourceLine.write(audioCollector.get(i), 0, audioCollector.get(i).length);
			System.out.println("Played from buffer: " + audioCollector.get(i).length);
		}
	}

	public void play(long timeBorder) {
		System.out.println("Play audio");
		audioFiles = AUDIO.listFiles();
		Arrays.sort(audioFiles,new Comparator<File>() {

			@Override
			public int compare(File arg0, File arg1) {
				long time1 = Long.parseLong(arg0.getName());
				long time2 = Long.parseLong(arg1.getName());
				if(time1<time2){
					return -1;
				}else if(time2>time1){
					return 1;
				} else {
					return 0;
				}
			}
		});
		System.out.println("Audio file list size[" + audioFiles.length +"]");
	//	Assert.assertArrayEquals(time.toArray(), audioFiles);

		List<byte[]> audio;
		while(timeBorder>getTime(soundItem)&&(audio=getAudiouData(soundItem))!=null){
	//		System.out.println("Time from buff[" + time.get(soundItem) + "], from disk[" + getTime(soundItem) + "]");
		//	Assert.assertArrayEquals(audioCollector.get(soundItem), audio);
			for(int i = 0;i<audio.size();i++){				
				sourceLine.write(audio.get(i), 0, audio.get(i).length);
				//audioCollector.add(audio.get(i));
			}
			soundItem+=1;
		}
		/*for(int i = 0;i<audioCollector.size();i++)
			Assert.assertArrayEquals("On index " + i 
					+ " Expected " + Arrays.asList(audioCollector)
					+ " Actual " + Arrays.asList(test),audioCollector.get(i), test.get(i));*/
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
					time.addAll(timeBuff);
					audioCollector.addAll(audioBuff);
					timeBuff= new ArrayList<>();
					audioBuff= new ArrayList<>();
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
				//	Assert.assertArrayEquals("Audio file " + audioFile.getAbsolutePath(),audioCollectorLocal.get(i), collector.toByteArray());
					System.out.println("Audio file path: " + audioFile.getAbsolutePath());
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
						if(time1<time2){
							return -1;
						}else if(time2>time1){
							return 1;
						} else {
							return 0;
						}
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



	public void addToDisk() {
		addToDisk(time,audioCollector);
		System.out.println("Stored to disk: " + totalAudioOnDisk);
		System.out.println("Stored to disk(Audio): " + AUDIO.list().length);
		System.out.println("Stored to disk(Bufferd): " + audioCollector.size());
	}

}
