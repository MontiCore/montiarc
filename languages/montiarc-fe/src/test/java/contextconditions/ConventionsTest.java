/**
 *
 */
package contextconditions;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.ConnectorSourceAndTargetComponentDiffer;
import montiarc.cocos.ConnectorSourceAndTargetExist;
import montiarc.cocos.ImportsAreUnique;
import montiarc.cocos.PortNameIsLowerCase;
import montiarc.cocos.PortUsage;
import montiarc.cocos.SubComponentsConnected;

/**
 * @author Arne Haber, Michael Mutert
 * @date 08.02.2010
 */
public class ConventionsTest extends AbstractCoCoTest {

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Test
  /* Checks whether all port names in the port definition start with a lower
   * case letter */
  public void testPortConvention() {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.PortViolatesNamingConventions");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortNameIsLowerCase());
    checkInvalid(cocos, node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA077"));
  }

  @Test
  /* Checks whether there is a redundant import statements. For example import
   * a.*; import a.*; */
  public void testImportConvention() {
    ASTComponent node = (ASTComponent) getAstNode("arc/coco/conventions", "conv.UnuniqueImports");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ImportsAreUnique());
    checkInvalid(cocos, node, new AbstractCoCoTestExpectedErrorInfo(2, "xMA074"));
  }

  @Test
  /* Checks whether there are connectors in a component that wrongly connect
   * ports of the same component */
  public void testConnectorSourceAndTargetDifferentComponent() {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions",
        "conv.ConnectorSourceAndTargetSameComponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checkInvalid(cocos, node, new AbstractCoCoTestExpectedErrorInfo(2, "xMA075"));
  }

  @Test
  /* Checks whether the source and target of a connect statement exist. */
  public void testMissingSourceAndTargetDefinition() {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions",
        "conv.MissingSourceTargetDefinitionInSubcomponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetExist());
    checkInvalid(cocos, node, new AbstractCoCoTestExpectedErrorInfo(2, "xMA066", "xMA067"));
  }

  @Test
  public void testUnusedPorts() {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.UnusedPorts");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortUsage());
    checkInvalid(cocos, node, new AbstractCoCoTestExpectedErrorInfo(3, "xMA057", "xMA058"));

    cocos = new MontiArcCoCoChecker().addCoCo(new SubComponentsConnected());
    checkInvalid(cocos, node, new AbstractCoCoTestExpectedErrorInfo(4, "xMA059", "xMA060"));
  }
}
