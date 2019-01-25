package componentinstantiation;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.montiarcautomaton.runtimes.componentinstantiation.LoaderManager;
import de.montiarcautomaton.runtimes.timesync.delegation.Port;
import portTest.DynamicPortTest;      

public class DynamicDeployPortTest {      	
  final static int CYCLE_TIME = 50; // in ms
  
  //Insert own Store and class path here
	static String STOREPATH = "src/test/resources/store/models/";
	static String CLASSPATH = "src/test/resources/store/classes/";

    
  public static void main(String[] args) {
    LoaderManager loman = new LoaderManager();
    final DynamicPortTest cmp = new DynamicPortTest();
    
    cmp.setUp();
    cmp.setLoaderConfiguration("portTest", STOREPATH, CLASSPATH, loman);
    cmp.init();
             
    long time;
    List<Port> changedPorts;
	ConsoleOutputCapturer stdoutCapturer = new ConsoleOutputCapturer();
	
	for (int i = 0; i < 160; i++) {
		time = System.currentTimeMillis();
		cmp.compute();
		changedPorts = cmp.reconfigure();
		cmp.propagatePortChanges(changedPorts);
		cmp.checkForCmp();
		cmp.update();
		while((System.currentTimeMillis()-time) < CYCLE_TIME){
			Thread.yield();
		}
		System.out.print(i+":");
		if (i == 15) {
			stdoutCapturer.start();
			try {
				FileUtils.copyDirectory(Paths.get("src/test/resources/models/exchange/").toFile(),
						Paths.get("src/test/resources/store/models").toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (i == 190){
			String string = stdoutCapturer.stop();
			assertTrue(stdoutCapturer.stop().contains("Test successful"));
		}


	}

}
}

