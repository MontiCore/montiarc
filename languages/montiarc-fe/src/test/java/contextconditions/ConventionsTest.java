/**
 *
 */
package contextconditions;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.ComponentNameIsCapitalized;
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

  @Ignore("Symboltable throws an exception on creation, as the name of the component artifact " +
          "starts with a lowercase letter. The node of the model can then not be resolved in " +
          "getASTNode().")
  @Test
  public void testComponentConventions()
      throws RecognitionException, IOException {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv" +
            ".ViolatesComponentNaming");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ComponentNameIsCapitalized());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "0xAC004"));
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

  @Ignore("implement coco")
  @Test
  /*
   * Checks whether all port names in the port definition start with a lower case letter
   */
  public void testPortConvention() {
    // runChecker("arc/coco/conventions/conv/PortViolatesNamingConventions.arc");
    //TODO Implement CoCo
    //TODO Add error code
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.ReferencesViolateNamingConventions");
//    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo();
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "0x"));
  }

  @Ignore("implement coco")
  @Test
  /*
   * 
   */
  public void testImportConvention() {
    // runChecker("arc/coco/conventions/conv/UnuniqueImports.arc");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testWrongConnector() {
    // runChecker("arc/coco/conventions/conv/WrongConnector.arc");
    assertEquals(4, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testConnectorSourceAndTargetDifferentComponent() {
    // runChecker("arc/coco/conventions/conv/ConnectorSourceAndTargetSameComponent.arc");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testMissingSourceAndTargetDefinition() {
    runCheckerWithSymTab("arc/coco/conventions", "conv.MissingSourceTargetDefinition");
    assertEquals(4, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Test
  public void testUnusedPorts() {
    runCheckerWithSymTab("arc/coco/conventions", "conv.UnusedPorts");
    Collection<String> findings = Log.getFindings().stream().map(f -> f.buildMsg())
        .filter(s -> s.contains("xAC006") || s.contains("xAC007"))
        .collect(Collectors.toList());
    assertEquals(findings.stream().collect(Collectors.joining("\n")), 3, findings.size());

    findings = Log.getFindings().stream().map(f -> f.buildMsg())
        .filter(s -> s.contains("xAC008") || s.contains("xAC009"))
        .collect(Collectors.toList());
    assertEquals(findings.stream().collect(Collectors.joining("\n")), 3, findings.size());
  }

  @Ignore("implement coco")
  @Test
  public void testOuterComponentWithInstanceName() {
    // runChecker("arc/coco/conventions/conv/OuterComponentWithInstanceName.arc");
    assertEquals(1, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

}
