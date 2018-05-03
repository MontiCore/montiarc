/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Symbol;
import infrastructure.AbstractGeneratorTest;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.JavaHelper;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * TODO
 *
 * @author (last commit) Michael Mutert
 * @version , 2018-04-27
 * @since TODO
 */
public class ComponentGenerationTest extends AbstractGeneratorTest {

  public static final String PACKAGE = "components";
  public static final String JAVA_FILE_ENDING = ".java";

  @Test
  public void testExample() {
    // 1. Specify the model to check
//    final String componentName = "ComponentWithEmptyComponent";
    final String componentName = "EmptyComponent";
    final String qualifiedName = PACKAGE + "." + componentName;


    // Load component symbol
    final ComponentSymbol symbol = loadComponentSymbol(qualifiedName);

    // 5. Determine all files which have to be checked
    List<File> filesToCheck = determineFilesToCheck(
        componentName,
        qualifiedName,
        symbol, TARGET_GENERATED_TEST_SOURCES_DIR);

    // 6. Determine if all files are present
    for (File file : filesToCheck) {
      assertTrue("Could not find expected generated file " + file.toString(),
          file.exists());
    }

    // Invoke Java compiler to see whether they are compiling
    AbstractGeneratorTest.isCompiling(filesToCheck.toArray(new File[filesToCheck.size()]));

    // Parse the files with the JavaDSL
    GlobalScope gs = initJavaDSLSymbolTable();

    final Optional<Symbol> emptyComponent
        = gs.resolve("components.EmptyComponent", JavaTypeSymbol.KIND);
    assertTrue(emptyComponent.isPresent());
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

    final Optional<String> deploy = component.getStereotype().get("deploy");

    // Add the special deployment file
    if (deploy != null && deploy.isPresent()) {
      filesToCheck.add(
          new File(TARGET_GENERATED_TEST_SOURCES_DIR + PACKAGE + "\\" +
                       "Deploy" + componentName + JAVA_FILE_ENDING));
    }

    for (String suffix : AbstractGeneratorTest.fileSuffixes) {
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

}
