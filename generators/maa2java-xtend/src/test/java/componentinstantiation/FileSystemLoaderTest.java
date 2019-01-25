package componentinstantiation;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.montiarcautomaton.generator.codegen.componentinstantiation.FileSystemLoader;

public class FileSystemLoaderTest {
	static String STOREPATH = "src/test/resources/store/models/";
	static String CLASSPATH = "src/test/resources/store/classes/";
	
	@Test
	public void loadCompTest() throws InterruptedException, IOException {
		List<String> subcomps = new ArrayList<>();
		subcomps.add("compInstTest.sc");
		FileSystemLoader loader = new FileSystemLoader("compInstTest", STOREPATH, CLASSPATH, subcomps);
		loader.start();
		FileUtils.copyDirectory(Paths.get("src/test/resources/models/exchange/").toFile(),
				Paths.get("src/test/resources/store/models").toFile());
		Thread.sleep(5000);
		assertTrue(loader.hasNewSubComponent("compInstTest.sc").isPresent());
		FileUtils.cleanDirectory(Paths.get(CLASSPATH).toFile());  

	}
}
