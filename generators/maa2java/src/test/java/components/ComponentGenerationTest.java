/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components;

import de.monticore.java.javadsl._ast.ASTClassDeclaration;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import generation.ComponentElementsCollector;
import generation.GeneratedComponentClassVisitor;
import infrastructure.AbstractGeneratorTest;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
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
    final ComponentSymbol symbol = generatorTool.loadComponentSymbolWithCocos(qualifiedName, Paths.get(MODEL_PATH).toFile()).orElse(null);
    assertNotNull(symbol);

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
    assertTrue(AbstractGeneratorTest.isCompiling(
        filesToCheck.toArray(new File[filesToCheck.size()])
    ));

    // Parse the files with the JavaDSL
    GlobalScope gs = initJavaDSLSymbolTable();

    Optional<Symbol> optinalClassTypeSymbol;
    ASTClassDeclaration javaDSLNode;

    optinalClassTypeSymbol = gs.resolve("components.EmptyComponent", JavaTypeSymbol.KIND);
    assertTrue(optinalClassTypeSymbol.isPresent());
    javaDSLNode = (ASTClassDeclaration) optinalClassTypeSymbol.get().getAstNode().get();

    // Collect information about expected features per file
    ASTComponent compUnit = (ASTComponent) symbol.getAstNode().get();
    ComponentElementsCollector compCollector
        = new ComponentElementsCollector(symbol, "EmptyComponent");
    compCollector.handle(compUnit);

    // Check if all expected elements are present and no other errors occured
    Log.getFindings().clear();
    GeneratedComponentClassVisitor visitor;
    visitor = compCollector.getClassVisitor();
    visitor.handle(javaDSLNode);
    visitor.allExpectedPresent();


    // Input class
    optinalClassTypeSymbol = gs.resolve("components.EmptyComponentInput", JavaTypeSymbol.KIND);
    assertTrue(optinalClassTypeSymbol.isPresent());
    javaDSLNode = (ASTClassDeclaration) optinalClassTypeSymbol.get().getAstNode().get();
    visitor = compCollector.getInputVisitor();
    visitor.handle(javaDSLNode);
    visitor.allExpectedPresent();

    // Result class
    optinalClassTypeSymbol = gs.resolve("components.EmptyComponentResult", JavaTypeSymbol.KIND);
    assertTrue(optinalClassTypeSymbol.isPresent());
    javaDSLNode = (ASTClassDeclaration) optinalClassTypeSymbol.get().getAstNode().get();
    visitor = compCollector.getResultVisitor();
    visitor.handle(javaDSLNode);
    visitor.allExpectedPresent();

    // Log checking
//    assertEquals(0, Log.getFindings().size());
    Log.debug("Found no errors while checking the generated files", "ComponentGenerationTest");
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
