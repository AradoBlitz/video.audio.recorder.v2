package video.audio.recorder.v2;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadsRun {
	
	public final static ExecutorService executor = Executors.newFixedThreadPool(100);
	
	public final static ExecutorService executorPlayer = Executors.newFixedThreadPool(100);

}
