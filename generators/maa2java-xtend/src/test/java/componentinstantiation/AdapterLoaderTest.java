package componentinstantiation;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.montiarcautomaton.generator.codegen.componentinstantiation.AdapterLoader;

public class AdapterLoaderTest {
	static final String SRCPATH = "src/test/resources/loaderTestStore/";
	static final String TARPATH = "target/test/loaderTestStore/";
	static final String MODELPATH  = "src/test/resources/models/initial/";
	static final String GENPATH  = "target/test/genTest/";
	static final String GENPATH2  = "target/test/genTest2/";

	


	
	@Test
	public void compilationTest() throws IOException {
		FileUtils.copyDirectory(Paths.get(SRCPATH).toFile(), Paths.get(TARPATH).toFile());
		AdapterLoader aLoader = new AdapterLoader();
		aLoader.compileClasses(TARPATH);
		assertTrue(Paths.get(TARPATH + "DynamicConsoleOutputCapturer.class").toFile().isFile());
		FileUtils.cleanDirectory(Paths.get(TARPATH).toFile());
	}
	
	@Test
	public void getObjectTest() throws IOException {
		FileUtils.copyDirectory(Paths.get(SRCPATH).toFile(), Paths.get(TARPATH).toFile());
		AdapterLoader aLoader = new AdapterLoader();
		aLoader.compileClasses(TARPATH);
		assertTrue(Paths.get(TARPATH + "DynamicConsoleOutputCapturer.class").toFile().isFile());
		Object classObj = aLoader.getClassObject(TARPATH, "loaderTestStore.", "target/test/");
		assertTrue(classObj != null);
		FileUtils.cleanDirectory(Paths.get(TARPATH).toFile());
	}
	
	@Test
	public void generationTest() throws IOException {
		FileUtils.copyDirectory(Paths.get(MODELPATH).toFile(), Paths.get(GENPATH).toFile());
		AdapterLoader aLoader = new AdapterLoader();
		aLoader.generateJavaFiles(GENPATH, GENPATH2);
		FileUtils.cleanDirectory(Paths.get(GENPATH).toFile());
		//assertTrue(Paths.get(TARPATH + "portTest/DynamicPortTest.java").toFile().isFile());

		FileUtils.cleanDirectory(Paths.get(GENPATH2).toFile());


	
	}
}
