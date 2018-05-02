/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components;

import de.montiarcautomaton.generator.MontiArcGeneratorTool;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.GeneratorChecker;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * TODO
 *
 * @author (last commit) Michael Mutert
 * @version , 2018-04-27
 * @since TODO
 */
public class ComponentGenerationTest {

  public static final String PACKAGE = "components";
  public static final String FILE_PATH = "src/test/resources/";
  public static final String TARGET_GENERATED_TEST_SOURCES = "generated-test-sources/";
  protected static final String FAKE_JAVA_TYPES_PATH = "target/javaLib/";
  public static final String JAVA_FILE_ENDING = ".java";

  @Before
  public void setUp() throws Exception {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }

  @Test
  public void testExample() {
    // 1. Specify the model to check
    final String componentName = "ComponentWithEmptyComponent";
//    final String componentName = "EmptyComponent";
    final String qualifiedName = PACKAGE + "." + componentName;

    // Clear output folder
    File outputFolder = Paths.get(TARGET_GENERATED_TEST_SOURCES).toFile();
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
    MontiArcGeneratorTool generatorTool = new MontiArcGeneratorTool();
    generatorTool.generate(
        Paths.get(FILE_PATH).toFile(),
        Paths.get(TARGET_GENERATED_TEST_SOURCES).toFile(),
        Paths.get("src/main/java").toFile());

    // Load component symbol
    Scope symTab = generatorTool.initSymbolTable(Paths.get(FILE_PATH).toFile(),
        Paths.get(FILE_PATH + MontiArcGeneratorTool.DEFAULT_TYPES_FOLDER).toFile(),
        Paths.get(FILE_PATH + MontiArcGeneratorTool.LIBRARY_MODELS_FOLDER).toFile());
    final Optional<Symbol> comp = symTab.resolve(qualifiedName, ComponentSymbol.KIND);
    assertTrue("Component Symbol is not present", comp.isPresent());
    final ComponentSymbol symbol = (ComponentSymbol) comp.get();

    // 5. Determine all files which have to be checked
    List<File> filesToCheck = new ArrayList<>();
    final Optional<String> deploy = symbol.getStereotype().get("deploy");

    // Add the special deployment file
    if (deploy != null && deploy.isPresent()) {
      filesToCheck.add(
          new File(TARGET_GENERATED_TEST_SOURCES + PACKAGE + "\\" +
                       "Deploy" + componentName + JAVA_FILE_ENDING));
    }

    // Recursive check for the component and all subcomponents
    filesToCheck.addAll(
        determineFilesToCheck(
            componentName,
            qualifiedName,
            symbol, TARGET_GENERATED_TEST_SOURCES)
    );

    // 6. Determine if all files are present
    for (File file : filesToCheck) {
      assertTrue(file.exists());
    }

    // Invoke Java compiler to see whether they are compiling
    GeneratorChecker.isCompiling(filesToCheck.toArray(new File[filesToCheck.size()]));

    // Parse the files with the JavaDSL

    // Collect information about expected features per file

    // Check if the expected features are present
    // Alternatively check if only expected features are present
  }

  /**
   * Recursively determines a list of File objects which correspond to the
   * locations of the generated files of the component given by the parameters.
   *
   * @param componentName Name of the component
   * @param qualifiedName Qualified name of the component (package.componentName)
   * @param component ComponentSymbol of the Component
   * @param basedir Location of the generated files
   * @return A list of generated files for the specified component
   */
  private List<File> determineFilesToCheck(String componentName, String qualifiedName, ComponentSymbol component, String basedir) {
    List<File> filesToCheck = new ArrayList<>();
    for (String suffix : GeneratorChecker.fileSuffixes) {
      final String qualifiedFileName
          = basedir + qualifiedName.replace('.', '\\') + suffix;
      filesToCheck.add(
          new File(qualifiedFileName + JAVA_FILE_ENDING));
    }

    if (component.getSubComponents().isEmpty()) {

      // Determine if an automaton or a compute block is present
      final ASTComponent astNode
          = (ASTComponent) component.getAstNode().get();

      for (ASTElement element : astNode.getBody().getElements()) {
        if (element instanceof ASTBehaviorElement) {
          final String qualifiedFileName
              = basedir + qualifiedName.replace('.', '\\') + "Impl";
          filesToCheck.add(
              new File(qualifiedFileName + JAVA_FILE_ENDING));
          break;
        }
      }
    } else {

      //Recursively add files for subcomponents
      for (ComponentInstanceSymbol instanceSymbol : component.getSubComponents()) {
        final ComponentSymbol referencedSymbol
            = instanceSymbol.getComponentType().getReferencedSymbol();
        filesToCheck.addAll(
            determineFilesToCheck(
                referencedSymbol.getName(),
                referencedSymbol.getFullName(),
                referencedSymbol, basedir));
      }

    }

    return filesToCheck;
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
}
