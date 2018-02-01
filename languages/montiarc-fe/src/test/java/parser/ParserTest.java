/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package parser;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;

/**
 * @author Robert Heim
 */
public class ParserTest {
  public static final boolean ENABLE_FAIL_QUICK = true;
  
  private static final String MODEL_PATH = "src/test/resources";
  
  private static List<String> expectedParseErrorModels = Arrays.asList(
      MODEL_PATH + "/arc/context/a/CG12false.arc",

      // "component" is a keyword and may not be used as component name
      MODEL_PATH + "/arc/context/a/component.arc",

      // "connect" is a keyword
      MODEL_PATH + "/arc/context/a/S2.arc",
      
      // Multiple inheritance is not supported
      MODEL_PATH + "/components/head/inheritance/MultipleInheritance.arc",
      
      // The name of the component is not identical to the name of the file
      MODEL_PATH + "/arc/context/a/R3.arc",
      
      // The name of the component is not identical to the name of the file
      MODEL_PATH + "/arc/context/a/S3.arc",
      
   // The name of the component is not identical to the name of the file
      MODEL_PATH + "/components/head/name/NameClashB.arc",

      // The package declaration of the component must not differ from the package of the component file.
      MODEL_PATH + "/arc/context/a/S4.arc",
      
      // TODO we do not support OCL Expressions yet
      MODEL_PATH + "/arc/prettyPrint/example1/StatusControl.arc",

      // TODO we do not support OCL Expressions yet
      MODEL_PATH + "/arc/symtab/ocl/OCLFieldToPort.arc")

      .stream().map(s -> Paths.get(s).toString())
      .collect(Collectors.toList());

  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
  }

  @Test
  public void testArc() throws RecognitionException, IOException {
    test("arc");
  }

  @Test
  public void testArcD() throws RecognitionException, IOException {
    test("arcd");
  }

  private void test(String fileEnding) throws IOException {
    ParseTest parserTest = new ParseTest("." + fileEnding);
    Files.walkFileTree(Paths.get("src/test/resources"), parserTest);

    if (!parserTest.getModelsInError().isEmpty()) {
      Log.debug("Models in error", "ParserTest");
      for (String model : parserTest.getModelsInError()) {
        Log.debug("  " + model, "ParserTest");
      }
    }
    Log.info("Count of tested models: " + parserTest.getTestCount(), "ParserTest");
    Log.info("Count of correctly parsed models: "
        + (parserTest.getTestCount() - parserTest.getModelsInError().size()), "ParserTest");

    assertTrue("There were models that could not be parsed", parserTest.getModelsInError()
        .isEmpty());
  }

  /**
   * Visits files of the given file ending and checks whether they are parsable.
   *
   * @author Robert Heim
   * @see Files#walkFileTree(Path, java.nio.file.FileVisitor)
   */
  private static class ParseTest extends SimpleFileVisitor<Path> {

    private String fileEnding;

    private List<String> modelsInError = new ArrayList<>();

    private int testCount = 0;

    public ParseTest(String fileEnding) {
      super();
      this.fileEnding = fileEnding;
    }

    /**
     * @return testCount
     */
    public int getTestCount() {
      return this.testCount;
    }

    /**
     * @return modelsInError
     */
    public List<String> getModelsInError() {
      return this.modelsInError;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
      if (file.toFile().isFile()
          && (file.toString().toLowerCase().endsWith(fileEnding))) {

        Log.debug("Parsing file " + file.toString(), "ParserTest");
        testCount++;
        Optional<ASTMACompilationUnit> maModel = Optional.empty();
        boolean expectingError = ParserTest.expectedParseErrorModels.contains(file.toString());

        MontiArcParser parser = new MontiArcParser();
        try {
          if (expectingError) {
            Log.enableFailQuick(false);
          }
          maModel = parser.parse(file.toString());
        }
        catch (Exception e) {
          if (!expectingError) {
            Log.error("Exception during test", e);
          }
        }
        if (!expectingError && (parser.hasErrors() || !maModel.isPresent())) {
          modelsInError.add(file.toString());
          Log.error("There were unexpected parser errors");
        }
        else {
          Log.getFindings().clear();
        }
        Log.enableFailQuick(ParserTest.ENABLE_FAIL_QUICK);
      }
      return FileVisitResult.CONTINUE;
    }
  }

  ;

}
