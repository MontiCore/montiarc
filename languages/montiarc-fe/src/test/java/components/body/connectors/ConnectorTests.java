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
import org.junit.Ignore;
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
import montiarc.cocos.SimpleConnectorSourceExists;

/**
 * This class checks all context conditions directly related to connector definitions
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
  public void testSimpleConnectorSourceNonExistant() {
    ASTMontiArcNode node = loadComponentAST( PACKAGE + "." + "SimpleConnectorSourceNonExistent");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SimpleConnectorSourceExists()),
        node, new ExpectedErrorInfo(1, "xMA072"));
  }
  
  @Test
  public void testSimpleConnectorSourceInvalid() {
    ASTMontiArcNode node = loadComponentAST( PACKAGE + "." + "SimpleConnectorSourceFullyQualified");
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
  
  /* Checks multiple instances of wrong connectors with connectors piercing through interfaces and
   * qualified simple connector sources. */
  @Test
  public void testMultipleWrongConnectors() {
    ASTMontiArcNode node = loadComponentAST( PACKAGE + "." + "WrongConnectors");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xMA008", "xMA070"));
  }

  /* Checks multiple instances of wrong connectors with connectors piercing through interfaces and
   * qualified simple connector sources. */
  @Test
  public void testMultipleWrongConnectors2() {
    ASTMontiArcNode node = loadComponentAST( PACKAGE + "." + "WrongConnectors2");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2,"xMA070"));
  }
  
  @Test
  /* Checks whether there are connectors in a component that wrongly connect ports of the same
   * component */
  public void testConnectorSourceAndTargetDifferentComponent() {
    ASTMontiArcNode node = loadComponentAST( PACKAGE + "." + "ConnectorSourceAndTargetSameComponent");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA075"));
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
  @Ignore("The missing subcomponents throw an exeption in the symbol table creation.")
  /* Checks whether the source and target of a connect statement exist. */
  public void testConnectorReferenceDoesNotExist() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectorReferenceDoesNotExist");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo(new ConnectorSourceAndTargetExist());
    checkInvalid(cocos, node, new ExpectedErrorInfo(3, "xMA066", "xMA067"));
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

  @Test
  public void testConnectsNonExistingPorts() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectsNonExistingPorts");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
                                    .addCoCo(new ConnectorSourceAndTargetExist());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA066", "xMA067"));
  }

  @Test
  public void testConnectsNonExistingPorts2() {
    ASTMontiArcNode node = loadComponentAST(
        PACKAGE + "." + "ConnectsNonExistingPorts2");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
                                    .addCoCo(new ConnectorSourceAndTargetExist());
    checkInvalid(cocos, node, new ExpectedErrorInfo(3, "xMA066", "xMA067"));
  }
}
