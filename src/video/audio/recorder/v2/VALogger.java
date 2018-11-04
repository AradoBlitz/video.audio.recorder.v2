package video.audio.recorder.v2;

public class VALogger {
	public final static StringBuilder log2 = new StringBuilder();
	public final static StringBuilder log = new StringBuilder();
	public final static StringBuffer logCam = new StringBuffer();
	public final static StringBuilder logMic = new StringBuilder();
	
	public static volatile int readAudio=0;
	public static volatile int writeAudio=0;
	public static volatile int readVideo=0;
	public static volatile int writeVideo=0;
	public static volatile int writeVideoWait=0;
	
	public static volatile int pReadAudio=0;
	public static volatile int pWriteAudio=0;
	public static volatile int pReadVideo=0;
	public static volatile int pWriteVideo=0;
	
}
