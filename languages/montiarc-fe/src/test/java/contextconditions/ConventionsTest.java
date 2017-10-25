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

  @Test
  /*
   * Checks whether all port names in the port definition start with a lower case letter
   */
  public void testPortConvention() {
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
    ASTComponent node = (ASTComponent) getAstNode("arc/coco/conventions", "conv.UnuniqueImports");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ImportsAreUnique());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xC0004"));
  }

  @Test
  /*
    Checks whether there are connectors in a component that wrongly connect ports of the same component
   */
  public void testConnectorSourceAndTargetDifferentComponent() {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.ConnectorSourceAndTargetSameComponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xC0005"));
  }

  @Test
  /*
    Checks whether the source and target of a connect statement exist.
   */
  public void testMissingSourceAndTargetDefinition() {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.MissingSourceTargetDefinitionInSubcomponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExist());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xC0001", "xC0002"));
  }

  @Test
  public void testUnusedPorts() {
    runCheckerWithSymTab("arc/coco/conventions", "conv.UnusedPorts");
    Collection<String> findings = Log.getFindings().stream().map(f -> f.buildMsg())
        .filter(s -> s.contains("xMA057") || s.contains("xMA058"))
        .collect(Collectors.toList());
    assertEquals(findings.stream().collect(Collectors.joining("\n")), 3, findings.size());

    findings = Log.getFindings().stream().map(f -> f.buildMsg())
        .filter(s -> s.contains("xMA059") || s.contains("xMA060"))
        .collect(Collectors.toList());
    assertEquals(findings.stream().collect(Collectors.joining("\n")), 3, findings.size());

}
