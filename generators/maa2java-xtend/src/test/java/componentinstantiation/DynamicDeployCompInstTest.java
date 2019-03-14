//TODO: These only work after executing SimpleGenerationTest

//package componentinstantiation;
//
//import static org.junit.Assert.assertTrue;
//
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.util.List;
//
//import org.apache.commons.io.FileUtils;
//import org.junit.Test;
//
//import de.montiarcautomaton.generator.codegen.componentinstantiation.FileSystemLoader;
//import de.montiarcautomaton.runtimes.componentinstantiation.ILoader;
//import de.montiarcautomaton.runtimes.componentinstantiation.LoaderManager;
//import de.montiarcautomaton.runtimes.timesync.delegation.Port;
//import genTest.DynamicCompInstTest;      
//
//public class DynamicDeployCompInstTest {      	
//	final static int CYCLE_TIME = 50; // in ms
//	static String STOREPATH = "src/test/resources/store/models/";
//	static String CLASSPATH = "src/test/resources/store/classes/";
//
//	@Test
//	public void deployTest() {
//		LoaderManager loman = new LoaderManager((ILoader) new FileSystemLoader());
//		final DynamicCompInstTest cmp = new DynamicCompInstTest();
//
//		cmp.setUp();
//		cmp.setLoaderConfiguration("compInstTest", STOREPATH, CLASSPATH, loman);
//		cmp.init();
//
//		long time;
//		List<Port> changedPorts;
//		//ConsoleOutputCapturer stdoutCapturer = new ConsoleOutputCapturer();
//		
//		for (int i = 0; i < 160; i++) {
//			time = System.currentTimeMillis();
//			cmp.compute();
//			changedPorts = cmp.reconfigure();
//			cmp.propagatePortChanges(changedPorts);
//			cmp.checkForCmp();
//			cmp.update();
//			while((System.currentTimeMillis()-time) < CYCLE_TIME){
//				Thread.yield();
//			}
//			System.out.print(i+":");
//			if (i == 15) {
//				//stdoutCapturer.start();
//				try {
//					FileUtils.copyDirectory(Paths.get("src/test/resources/models/exchange/").toFile(),
//							Paths.get("src/test/resources/store/models").toFile());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if (i == 190){
//				//String string = stdoutCapturer.stop();
//				//assertTrue(stdoutCapturer.stop().contains("Test successful"));
//			}
//
//
//		}
//
//	}
//}
//
