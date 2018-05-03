/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package infrastructure;

import de.montiarcautomaton.generator.MontiArcGeneratorTool;
import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.JavaHelper;
import org.junit.Before;

import javax.tools.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * TODO
 *
 * @author (last commit)
 * @version ,
 * @since TODO
 */
public class AbstractGeneratorTest {

  public static final String[] fileSuffixes = new String[]{
    "Result", "Input", ""
  };

  public static final String IMPLEMENTATION_SUFFIX = "Impl";

  public static final String outputPath = "target/generated-test-sources/";
  public static final String MODEL_PATH = "src/test/resources/";
  public static final String GENERATED_TEST_SOURCES = "generated-test-sources";
  public static final String TARGET_GENERATED_TEST_SOURCES_DIR
      = GENERATED_TEST_SOURCES + "/";
  protected MontiArcGeneratorTool generatorTool;

  /**
   * Invokes the Java compiler on the given files.
   *
   * @param files Files to compile
   * @return true, if there are no compiler errors
   */
  public static boolean isCompiling(File[] files){

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager
        = compiler.getStandardFileManager(null, null, null);
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

    Iterable<? extends JavaFileObject> compilationUnits1 =
        fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
    compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits1).call();

    try {
      fileManager.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for ( Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
      System.out.format("Error on line %d in %s%n",
          diagnostic.getLineNumber(),
          diagnostic.getSource().toUri());
    }
    return diagnostics.getDiagnostics().size() <= 0;
  }

  @Before
  public void setUp() throws Exception {
    Log.getFindings().clear();
    Log.enableFailQuick(false);

    // Clear output folder
    File outputFolder = Paths.get(TARGET_GENERATED_TEST_SOURCES_DIR).toFile();
    if (outputFolder.exists()) {
      try {
        delete(outputFolder);
      } catch (IOException e) {
        Log.error("Could not delete output directory");
      }
    } else {
      Log.info("Folder to delete does not exist", "GeneratorTest");
    }

    // 4. Generate models (at specified location)
    generatorTool = new MontiArcGeneratorTool();
    generatorTool.generate(
        Paths.get(MODEL_PATH).toFile(),
        Paths.get(TARGET_GENERATED_TEST_SOURCES_DIR).toFile(),
        Paths.get("src/main/java").toFile());
  }

  /**
   * Recursively deletes files from the given file object (folder)
   * @param file {@link File} object to delete
   * @throws IOException
   */
  protected void delete(File file) throws IOException {
    if (file.isDirectory()) {
      for (File c : file.listFiles())
        delete(c);
    }
    if (!file.delete())
      throw new FileNotFoundException("Failed to delete file: " + file);
  }

  protected ComponentSymbol loadComponentSymbol(String qualifiedName) {
    Scope symTab = generatorTool.initSymbolTable(Paths.get(MODEL_PATH).toFile(),
        Paths.get(MODEL_PATH + MontiArcGeneratorTool.DEFAULT_TYPES_FOLDER).toFile(),
        Paths.get(MODEL_PATH + MontiArcGeneratorTool.LIBRARY_MODELS_FOLDER).toFile());
    final Optional<Symbol> comp = symTab.resolve(qualifiedName, ComponentSymbol.KIND);
    assertTrue("Component Symbol is not present", comp.isPresent());
    return (ComponentSymbol) comp.get();
  }

  protected GlobalScope initJavaDSLSymbolTable() {
    ModelingLanguageFamily family = new ModelingLanguageFamily();
    family.addModelingLanguage(new JavaDSLLanguage());

    Set<Path> paths = new HashSet<>();
    paths.add(Paths.get(GENERATED_TEST_SOURCES));

    ModelPath modelPath = new ModelPath(paths);

    GlobalScope gs = new GlobalScope(modelPath, family);
    JavaHelper.addJavaPrimitiveTypes(gs);
    return gs;
  }
}
