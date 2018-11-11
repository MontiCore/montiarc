/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components.body.connectors;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTConnectorCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
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
 * @author Andreas Wortmann
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
  public void testConnectorSourceAndTargetTypeNotMatch() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "ConnectorSourceAndTargetTypeNotMatch");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit()), node,
        new ExpectedErrorInfo(1, "xMA033"));
  }

  @Test
  public void testPortCompatibilityTypeInheritance() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "PortCompatibilityTypeInheritance");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit()), node,
        new ExpectedErrorInfo(8, "xMA033"));
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
  public void testSimpleConnectorSourceInvalid() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "SimpleConnectorSourceFullyQualified");
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        node, new ExpectedErrorInfo(1, "xMA070"));
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
        new ExpectedErrorInfo(9, "xMA066", "xMA067", "xMA008"));
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
        = new ExpectedErrorInfo(10, "xMA066", "xMA067", "xMA008");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  public void testConnectorsWithStereotypes() {
    Scope symTab = this.loadDefaultSymbolTable();
    ConnectorSymbol connector = symTab
        .<ConnectorSymbol> resolve(PACKAGE + "." + "ConnectorsWithStereotypes.myOut",
            ConnectorSymbol.KIND)
        .orElse(null);
    assertNotNull(connector);
    
    assertEquals(1, connector.getStereotype().size());
    assertFalse(connector.getStereotype().get("realNews").isPresent());
    checkValid(PACKAGE + "." + "ConnectorsWithStereotypes");
  }
  
  @Test
  @Ignore("see issue #237. Change coco and/or fix errors codes in test")
  public void testConnectingInnerCompToIncomingPort() {
    final String modelName = PACKAGE + "." + "ConnectingInnerCompToIncomingPort";
    MontiArcCoCoChecker cocos = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(2);
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
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA033"));
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
    final Optional<? extends Symbol> componentSymbol = astMontiArcNode.getSymbolOpt();
    assertTrue(componentSymbol.isPresent());
    final ComponentSymbol symbol = (ComponentSymbol) componentSymbol.get();
    assertTrue(symbol.getConnector("sender.message").isPresent());
    assertTrue(symbol.getConnector("sender.ack").isPresent());
    assertTrue(symbol.getConnector("abpMessage").isPresent());
  }

  @Test
  public void testConnectsPortsWithIncompatibleTypes() {
    final ASTMontiArcNode astNode = loadComponentAST(
        PACKAGE + "." + "ConnectsPortsWithIncompatibleTypes");
    final ExpectedErrorInfo expectedErrorInfo = new ExpectedErrorInfo(5, "xMA033");
    final MontiArcCoCoChecker checker =
        new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checkInvalid(checker, astNode, expectedErrorInfo);
  }
  
  @Test
  public void testTypeHierarchyInConnector() {
    checkValid(PACKAGE + "." + "TypeHierarchyInConnector");
  }

  @Test
  public void testGenericSourceTypeIsSubtypeOfTargetType() {
    checkValid(PACKAGE + "." + "GenericSourceTypeIsSubtypeOfTargetType");
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
  public void testConnectsIncompatibleInheritedPorts2() {
    final String compName = "ConnectsIncompatibleInheritedPorts2";
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
