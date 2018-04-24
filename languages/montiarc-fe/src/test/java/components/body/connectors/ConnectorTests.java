/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components.body.connectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTConnectorCoCo;
import montiarc._cocos.MontiArcASTSimpleConnectorCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ConnectorSymbol;
import montiarc.cocos.ConnectorEndPointIsCorrectlyQualified;
import montiarc.cocos.ConnectorSourceAndTargetComponentDiffer;
import montiarc.cocos.ConnectorSourceAndTargetExist;
import montiarc.cocos.ConnectorSourceAndTargetTypeFit;
import montiarc.cocos.SimpleConnectorSourceExists;

/**
 * This class checks all context conditions directly related to connector
 * definitions
 *
 * @author Andreas Wortmann
 */
public class ConnectorTests extends AbstractCoCoTest {
  
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
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetTypeFit()), node,
        new ExpectedErrorInfo(1, "xMA084"));
  }
  
  @Test
  public void testConnectorSourceAndTargetNonExistant() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectorSourceAndTargetNotExist");
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SimpleConnectorSourceExists());
    checker.addCoCo(new ConnectorSourceAndTargetExist());
    checkInvalid(checker, node, new ExpectedErrorInfo(3, "xMA072", "xMA066", "xMA067"));
  }
  
  @Test
  public void testSimpleConnectorSourceNonExistant() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "SimpleConnectorSourceNonExistent");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SimpleConnectorSourceExists()),
        node, new ExpectedErrorInfo(1, "xMA072"));
  }
  
  @Test
  public void testSimpleConnectorSourceInvalid() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "SimpleConnectorSourceFullyQualified");
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        node, new ExpectedErrorInfo(1, "xMA008"));
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
    String modelname = PACKAGE + "." + "ExistingReferenceInConnector";
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ConnectorSourceAndTargetExist()),
        loadComponentAST(modelname), new ExpectedErrorInfo(6, "xMA066", "xMA067", "xMA090"));
  }
  
  @Test
  public void testConnectorSourceInvalid() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectorPiercingOutwardsThroughInterface");
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        node, new ExpectedErrorInfo(1, "xMA070"));
  }
  
  @Test
  public void testConnectorTargetInvalid() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectorPiercingInwardsThroughInterface");
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        node, new ExpectedErrorInfo(1, "xMA070"));
  }
  
  /* Checks multiple instances of wrong connectors with connectors piercing
   * through interfaces and qualified simple connector sources. */
  @Test
  public void testMultipleWrongConnectors() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "WrongConnectors");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xMA008", "xMA070"));
  }
  
  @Test
  /* Checks whether there are connectors in a component that wrongly connect
   * ports of the same component */
  public void testConnectorSourceAndTargetDifferentComponent() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectorSourceAndTargetSameComponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checkInvalid(cocos, node, new ExpectedErrorInfo(3, "xMA075"));
  }
  
  @Test
  /* Checks whether the source and target of a connect statement exist. */
  public void testMissingSourceAndTargetDefinition() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "MissingSourceTargetDefinitionInSubcomponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetExist());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA066", "xMA067"));
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
  }
}
