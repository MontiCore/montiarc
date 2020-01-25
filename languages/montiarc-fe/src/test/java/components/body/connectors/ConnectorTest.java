/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import arc._cocos.ConnectorEndPointIsCorrectlyQualified;
import arc._cocos.ConnectorSourceAndTargetComponentDiffer;
import arc._cocos.ConnectorSourceAndTargetExistAndFit;
import arc._cocos.SubComponentsConnected;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTConnector;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTConnectorCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc.cocos.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * This class checks all context conditions directly related to connector
 * definitions
 *
 */
public class ConnectorTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body.connectors";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSimpleConnectorSourceExists() {
    checkValid(PACKAGE + "." + "SimpleConnectorSourceExists");
  }
  
  @Test
  public void testSimpleConnectorSourceExistsExternal() {
    checkValid(PACKAGE + "." + "SimpleConnectorSourceExistsExternal");
  }
  
  @Test
  public void testSimpleConnectorSourceUnqualified() {
    checkValid(PACKAGE + "." + "SimpleConnectorSourceUnqualified");
  }
  
  @Test
  /*
   3 errors, due to https://git.rwth-aachen.de/monticore/montiarc/core/issues/243
   Usually, 1 error
   */
  public void testConnectorSourceAndTargetTypeNotMatch() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ConnectorSourceAndTargetTypeNotMatch");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit()), node,
        new ExpectedErrorInfo(3, "xMA033"));
  }

  @Test
  /*
    2 additional errors due to #241, #243
   */
  public void testPortCompatibilityTypeInheritance() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "PortCompatibilityTypeInheritance");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit()), node,
        new ExpectedErrorInfo(10, "xMA033"));
  }

  @Test
  public void testConnectorSourceAndTargetNonExistant2() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectorSourceAndTargetNotExist");
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checkInvalid(checker, node, new ExpectedErrorInfo(3, "xMA066", "xMA067"));
  }
  
  @Test
  public void testSimpleConnectorSourceNonExistant() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "SimpleConnectorSourceNonExistent");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit()),
        node, new ExpectedErrorInfo(1, "xMA066"));
  }
  
  @Test
  @Ignore("Fix test and/or model")
  public void testSimpleConnectorSourceInvalid() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "SimpleConnectorSourceFullyQualified");
    final MontiArcCoCoChecker cocos =
//        new MontiArcCoCoChecker()
//        .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified());
        MontiArcCoCos.createChecker();
    checkInvalid(
        cocos, node,
        new ExpectedErrorInfo(1, "xMA070"));
  }
  
  @Test
  public void testConnectorNotPiercingThroughInterface() {
    checkValid(PACKAGE + "." + "ConnectorNotPiercingThroughInterface");
  }
  
  @Test
  public void testConnectingInheritedPorts() {
    checkValid(PACKAGE + "." + "ExistingPortInConnectorFromSuperComponent");
  }
  
  @Test
  public void testConnectors() {
    // Tests Coco ConnectorSourceAndTargetExistAndFit
    String modelname = PACKAGE + "." + "ExistingReferenceInConnector";
    ASTMontiArcNode node = loadComponentAST(modelname);
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checkInvalid(cocos, node,
        new ExpectedErrorInfo(3, "xMA066", "xMA067"));
  }
  
  @Test
  public void testConnectorSourceInvalid() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectorPiercingOutwardsThroughInterface");
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo(new ConnectorEndPointIsCorrectlyQualified()),
        node, new ExpectedErrorInfo(1, "xMA070"));
  }
  
  @Test
  public void testConnectorTargetInvalid() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectorPiercingInwardsThroughInterface");
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo(new ConnectorEndPointIsCorrectlyQualified()),
        node, new ExpectedErrorInfo(1, "xMA070"));
  }
  
  /* Checks multiple instances of wrong connectors with connectors piercing
   * through interfaces and qualified simple connector sources. */
  @Test
  public void testMultipleWrongConnectors() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "WrongConnectors");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorEndPointIsCorrectlyQualified());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xMA070"));
  }
  
  /* Checks multiple instances of wrong connectors with connectors piercing
   * through interfaces and qualified simple connector sources. */
  @Test
  public void testMultipleWrongConnectors2() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "WrongConnectors2");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorEndPointIsCorrectlyQualified());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA070"));
  }
  
  @Test
  /* Checks whether there are connectors in a component that wrongly connect
   * ports of the same component */
  public void testConnectorSourceAndTargetDifferentComponent() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectorSourceAndTargetSameComponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xMA075"));
  }
  
  @Test
  public void testConnectorSourceAndTargetFromSameInnerComponent() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ConnectorSourceAndTargetFromSameInnerComponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA075"));
  }
  
  @Test
  /* Checks whether the source and target of a connect statement exist. */
  public void testMissingSourceAndTargetDefinitionInSubcomponent() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "MissingSourceTargetDefinitionInSubcomponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA066", "xMA067"));
  }
  
  @Test
  /* Checks whether the source and target of a connect statement exist. */
  public void testMissingSourceAndTargetDefinition() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "MissingSourceTargetDefinition");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xMA066", "xMA067"));
  }
  
  @Test
  /* Checks whether the source and target of a connect statement exist. */
  public void testConnectorReferenceDoesNotExist() {
    final String modelName = PACKAGE + "." + "ConnectorReferenceDoesNotExist";
    MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(4, "xMA066", "xMA067");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  public void testConnectorsWithStereotypes() {
    final ComponentSymbol comp = loadComponentSymbol(PACKAGE, "ConnectorsWithStereotypes");
    assertTrue(comp.getAstNode().isPresent());
    assertTrue(comp.getAstNode().get() instanceof ASTComponent);
    ASTComponent astComp = (ASTComponent) comp.getAstNode().get();
    ASTConnector connector = astComp.getConnector("myOut").orElse(null);
    assertNotNull(connector);
    
    assertEquals(1, connector.getStereotype().sizeValuess());
    assertTrue(connector.getStereotype().containsStereoValue("realNews"));
    checkValid(PACKAGE + "." + "ConnectorsWithStereotypes");
  }
  
  @Test
  public void testConnectingInnerCompToIncomingPort() {
    final String modelName = PACKAGE + "." + "ConnectingInnerCompToIncomingPort";
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PortUsage());
    cocos.addCoCo(new SubComponentsConnected());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(4, "xMA097", "xMA098", "xMA104", "xMA105");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }

  @Test
  public void testConnectsNonExistingPorts() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectsNonExistingPorts");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA066", "xMA067"));
  }
  
  @Test
  public void testConnectsNonExistingPorts2() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectsNonExistingPorts2");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checkInvalid(cocos, node, new ExpectedErrorInfo(3, "xMA066", "xMA067"));
  }

  @Test
  public void testGenericIfUsage() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "GenericIfUsage");
    MontiArcCoCoChecker cocos = MontiArcCoCos.createChecker();
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA033"));
  }

  @Test
  public void testABP() {
    checkValid(PACKAGE + "." + "ABP");
  }

  @Test
  public void testABPReceiver() {
    checkValid(PACKAGE + "." + "ABPReceiver");
  }

  @Test
  public void testABPSender() {
    final String modelName = "ABPSender";
    checkValid(PACKAGE + "." + modelName);
    final ASTMontiArcNode astMontiArcNode = loadComponentAST(PACKAGE + "." + modelName);
    assertNotNull(astMontiArcNode);
    assertTrue(astMontiArcNode instanceof ASTComponent);
    ASTComponent astComponent = (ASTComponent) astMontiArcNode;
    final Optional<? extends Symbol> componentSymbol = astMontiArcNode.getSymbolOpt();
    assertTrue(componentSymbol.isPresent());
    assertTrue(astComponent.getConnector("sender.message").isPresent());
    assertTrue(astComponent.getConnector("sender.ack").isPresent());
    assertTrue(astComponent.getConnector("abpMessage").isPresent());
  }

  @Test
  public void testConnectsPortsWithIncompatibleTypes() {
    final ASTMontiArcNode astNode = loadComponentAST(
        PACKAGE + "." + "ConnectsPortsWithIncompatibleTypes");
    final ExpectedErrorInfo expectedErrorInfo = new ExpectedErrorInfo(6, "xMA033");
    final MontiArcCoCoChecker checker =
        new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checkInvalid(checker, astNode, expectedErrorInfo);
  }
  
  @Test
  /*
    1 error, due to issue #241, #243
   */
  public void testTypeHierarchyInConnector() {
    final String modelName = PACKAGE + "." + "TypeHierarchyInConnector";
    MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit());
    ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA033");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }

  @Test
  public void testGenericSourceTypeIsSubtypeOfTargetType() {
    final String modelName = PACKAGE + "." + "GenericSourceTypeIsSubtypeOfTargetType";
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ProhibitGenericsWithBounds());
    ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA072");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }

  @Test
  public void testGenericSourceTypeNotSubtypeOfTargetType() {
    final String modelName = PACKAGE + "." + "GenericSourceTypeNotSubtypeOfTargetType";
    final ExpectedErrorInfo expectedErrorInfo
        = new ExpectedErrorInfo(1, "xMA033");
    final MontiArcCoCoChecker checker
        = MontiArcCoCos.createChecker();
    checkInvalid(checker, loadComponentAST(modelName), expectedErrorInfo);
  }

  @Test
  public void testConnectsCompatibleInheritedPorts2() {
    final String compName = "ConnectsCompatibleInheritedPorts2";
    final ComponentSymbol componentSymbol = loadComponentSymbol(PACKAGE, compName);

    final Optional<ComponentInstanceSymbol> subComp
        = componentSymbol.getSubComponent("subComp");
    assertTrue(subComp.isPresent());

    final ComponentSymbol subCompSymbol
        = subComp.get().getComponentType().getReferencedSymbol();

    final Optional<PortSymbol> subCompOutTOpt = subCompSymbol.getPort("outT");
    assertTrue(subCompOutTOpt.isPresent());

    final Optional<PortSymbol> outTOpt = componentSymbol.getPort("outT", true);
    assertTrue(outTOpt.isPresent());


    checkValid(PACKAGE + "." + compName);
  }

  @Test
  public void testConnectsIncompatibleInheritedPorts() {
    final String modelName = PACKAGE + "." + "ConnectsIncompatibleInheritedPorts";
    final ExpectedErrorInfo expectedErrorInfo
        = new ExpectedErrorInfo(4, "xMA033", "xMA067");
    final MontiArcCoCoChecker checker
        = new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checkInvalid(checker, loadComponentAST(modelName), expectedErrorInfo);
  }
  
}
