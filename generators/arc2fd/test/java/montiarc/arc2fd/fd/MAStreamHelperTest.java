/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTComponentTypeTOP;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcFullPrettyPrinter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import variablearc._ast.ASTArcFeature;
import variablearc._ast.ASTArcIfStatement;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class MAStreamHelperTest {
  protected static final String TEST_RESOURCE_PATH = "test/resources";

  ASTMACompilationUnit ast;

  @BeforeEach
  public void setUp() throws Exception {
    MontiArcTool tool = new MontiArcTool();

    // Clear Global Scope
    MontiArcMill.globalScope().clear();
    MontiArcMill.reset();
    MontiArcMill.init();

    // Populate it with our .arc files we want to analyze
    tool.init();
    Collection<ASTMACompilationUnit> asts = tool.parse(".arc",
        Paths.get(TEST_RESOURCE_PATH, "StreamHelper").toAbsolutePath());
    tool.createSymbolTable(asts);
    tool.completeSymbolTable(asts);

    // And now get the .arc-File we're finally interested in
    Optional<ASTMACompilationUnit> optAst =
        tool.parse(Paths.get(TEST_RESOURCE_PATH, "StreamHelper" +
            "/MAStreamHelperTest.arc").toAbsolutePath());

    // Get the right AST...
    if (optAst.isEmpty()) {
      throw new Exception("No AST found!");
    }

    this.ast = optAst.get();

  }

  /**
   * Method under test
   * {@link MAStreamHelper#getArcFeatureDeclarationsFromElements(List)}
   */
  @Test
  void getArcFeatureDeclarationsFromElements() {
    // Given
    List<String> trueFeatures = List.of("a", "b", "c", "d", "e");
    List<String> falseFeatures = List.of("a", "b", "z");
    List<String> features =
        MAStreamHelper.getArcFeatureDeclarationsFromElements(
            MAStreamHelper.getArcElements(ast.getComponentType())).stream().map(ASTArcFeature::getName).collect(Collectors.toList());

    // When && Then
    Assertions.assertEquals(trueFeatures, features);
    Assertions.assertNotEquals(falseFeatures, features);
  }

  /**
   * Method under test
   * {@link MAStreamHelper#getConstraintExpressionsFromArcElements(List)}
   */
  @Test
  void getConstraintExpressionsFromArcElements() {
    // Given
    List<String> trueConstraints = List.of("a && b", "a || (b && c)");
    List<String> falseConstraints = List.of("a && b", "(b && c)");
    List<String> constraints =
        MAStreamHelper.getConstraintExpressionsFromArcElements(
                MAStreamHelper.getArcElements(ast.getComponentType())).stream()
            .map(t -> new MontiArcFullPrettyPrinter().prettyprint(t)).collect(Collectors.toList());

    // When && Then
    Assertions.assertEquals(trueConstraints, constraints);
    Assertions.assertNotEquals(falseConstraints, constraints);
  }

  /**
   * Method under test
   * {@link MAStreamHelper#getIfStatementsFromArcElements(List)}
   */
  @Test
  void getIfStatementsFromArcElements() {
    // Given
    int numberOfIfStatements = 2;
    List<ASTArcIfStatement> ifStatements =
        new ArrayList<>(MAStreamHelper.getIfStatementsFromArcElements(
            MAStreamHelper.getArcElements(ast.getComponentType())));

    // When && Then
    Assertions.assertEquals(numberOfIfStatements, ifStatements.size());
  }

  /**
   * Method under test
   * {@link MAStreamHelper#getInnerComponentsFromArcElements(List)}
   */
  @Test
  void getInnerComponentsFromArcElements() {
    // Given
    List<String> trueComponents = List.of("innerComp1", "innerComp2");
    List<String> falseComponents = List.of("innerComp3");
    List<String> innerComponents =
        MAStreamHelper.getInnerComponentsFromArcElements(
                MAStreamHelper.getArcElements(ast.getComponentType())).stream()
            .map(ASTComponentTypeTOP::getName).collect(Collectors.toList());

    // When && Then
    Assertions.assertEquals(trueComponents, innerComponents);
    Assertions.assertNotEquals(falseComponents, innerComponents);
  }

  /**
   * Method under test
   * {@link MAStreamHelper#getComponentInstancesFromArcElements(List)}
   */
  @Test
  void getComponentInstancesFromArcElements() {
    // Given
    List<String> trueCompInst = List.of("subCompTest");
    List<String> falseCompInst = List.of("subComp");
    List<String> componentInstances =
        MAStreamHelper.getComponentInstancesFromArcElements(
                MAStreamHelper.getArcElements(ast.getComponentType())).stream()
            .map(ASTComponentInstance::getName).collect(Collectors.toList());

    // When && Then
    Assertions.assertEquals(trueCompInst, componentInstances);
    Assertions.assertNotEquals(falseCompInst, componentInstances);
  }

  /**
   * Method under test {@link MAStreamHelper#getArcElements(ASTComponentType)}
   */
  @Test
  void getArcElements() {
    // Given
    int numberOfElements = 8;
    List<ASTArcElement> arcElements =
        MAStreamHelper.getArcElements(ast.getComponentType());

    // When && Then
    Assertions.assertEquals(numberOfElements, arcElements.size());
    Assertions.assertNotEquals(numberOfElements - 1, arcElements.size());
    Assertions.assertNotEquals(numberOfElements + 1, arcElements.size());
  }
}