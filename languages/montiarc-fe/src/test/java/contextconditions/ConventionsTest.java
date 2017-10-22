/**
 *
 */
package contextconditions;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Optional;

import montiarc._ast.ASTComponent;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._parser.MontiArcParser;
import montiarc.cocos.*;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;

//XXX: https://git.rwth-aachen.de/montiarc/core/issues/53

/**
 * @author Arne Haber
 * @date 08.02.2010
 */
public class ConventionsTest extends AbstractCoCoTest {

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Ignore("First part of the test is working, but in real application the unresolvable " +
          "ComponentSymbol would lead to errors in other context conditions.")
  @Test
  public void testComponentConventions()
      throws RecognitionException, IOException {

    MontiArcParser parser = new MontiArcParser();
    Optional<ASTMACompilationUnit> node =
      parser.parse("src/test/resources/arc/coco/conventions/conv/violatesComponentNaming.arc");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ComponentNameIsCapitalized());
    if(node.isPresent())
    {
      cocos.checkAll((ASTMontiArcNode) node.get());
      assertEquals(1, Log.getFindings().size());
    } else {
      Log.error("No test model.");
    }

    // runChecker("arc/coco/conventions/conv/InnerViolatesComponentNaming.arc");
//    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
//        .count());
  }

  @Ignore("implement coco")
  @Test
  /*
    Checks whether all references of components start with a lower-case letter
   */
  public void testReferenceConventions() {
//    runCheckerWithSymTab("arc/coco/conventions", "conv.ReferencesViolateNamingConventions");
    //TODO Add CoCo for checking whether references of components start with a lower case letter
    //TODO Insert error codes and Coco name
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.ReferencesViolateNamingConventions");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ComponentNameIsCapitalized());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "0x"));
  }


  @Test
  /*
   * Checks whether all port names in the port definition start with a lower case letter
   */
  public void testPortConvention() {
    //TODO Add correct error code
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.PortViolatesNamingConventions");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortNameIsLowerCase());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xC0003"));
  }


  @Test
  /*
   * Checks whether there is a redundant import statements.
   * For example
   *  import a.*;
   *  import a.*;
   */
  public void testImportConvention() {
    //TODO Add correct error code
    ASTComponent node = (ASTComponent) getAstNode("arc/coco/conventions", "conv.UnuniqueImports");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ImportsAreUnique());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xC0004"));
  }

  @Test
  /*
    Checks whether there are connectors in a component that wrongly connect ports of the same component
   */
  public void testConnectorSourceAndTargetDifferentComponent() {
    //TODO Add correct error code
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.ConnectorSourceAndTargetSameComponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xC1001"));
  }

  @Test
  /*
    Checks whether the source and target of a connect statement exist.
   */
  public void testMissingSourceAndTargetDefinition() {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.MissingSourceTargetDefinition");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExist());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xC0001","xC0002"));
  }

  @Test
  /*
    Checks whether the source and target of a connect statement exist.
   */
  public void testMissingSourceAndTargetDefinitionInSubcomponent() {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.MissingSourceTargetDefinitionInSubcomponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExist());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xC0001","xC0002"));
  }

  @Test
  public void testUnusedPorts() {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.UnusedPorts");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortUsage());
    checkInvalid(cocos, node, new ExpectedErrorInfo(3, "xAC006", "xAC007"));

    cocos = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    checkInvalid(cocos, node, new ExpectedErrorInfo(3, "xAC009"));

    //TODO: Modify test model to check for xAC008?
  }
}
