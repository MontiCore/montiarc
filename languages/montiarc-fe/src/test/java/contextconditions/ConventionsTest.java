/**
 *
 */
package contextconditions;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import de.monticore.java.javadsl._ast.ASTCompilationUnit;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Scope;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTConnectorCoCo;
import montiarc._cocos.MontiArcASTSimpleConnectorCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc.cocos.*;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import symboltable.AbstractSymboltableTest;

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


  @Test
  /*
   * Checks whether all port names in the port definition start with a lower case letter
   */
  public void testPortConvention() {
    //TODO Add correct error code
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.PortViolatesNamingConventions");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortNameIsLowerCase());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xC0001"));
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
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xC000A", "xC000A"));
  }

  @Ignore("Duplicate of ConnectorEndpointCorrectlyQualifiedTest.java")
  @Test
  /*
   * Checks whether
   */
  public void testWrongConnector() {
    //TODO Remove duplicate test?
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.WrongConnector");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
            .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xDB61C"));
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


  @Test
  /*
   * Checks that the outer component definition has no instance name.
   */
  public void testOuterComponentWithInstanceName() {
    //TODO Add correct error code
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.OuterComponentWithInstanceName");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new OuterComponentIsUnnamed());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xC0003"));
  }

  @Test
  /*
   * Checks that outer component definitions with no instance name are not flagged.
   */
  public void testOuterComponentWithoutInstanceName() {
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.OuterComponentWithoutInstanceName");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new OuterComponentIsUnnamed());
    checkInvalid(cocos, node, new ExpectedErrorInfo());
  }

}
