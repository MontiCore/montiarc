/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package infrastructure;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * TODO
 *
 * @author (last commit)
 * @version ,
 * @since TODO
 */
public class GeneratorChecker {


  private final String model;

  public static final String[] fileSuffixes = new String[]{
    "Result", "Input", ""
  };

  public static final String[] filePrefixes = new String[]{
      "Deploy"
  };

  public static final String outputPath = "target/generated-test-sources/";


  /**
   *
   * @param model Qualified name of the component which should be checked
   */
  public GeneratorChecker(final String model) {
    this.model = model;
  }

  public String getModel() {
    return model;
  }

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
}
