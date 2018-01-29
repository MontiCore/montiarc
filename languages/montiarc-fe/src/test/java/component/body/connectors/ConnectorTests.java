/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.body.connectors;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTConnectorCoCo;
import montiarc._cocos.MontiArcASTSimpleConnectorCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.ConnectorEndPointIsCorrectlyQualified;
import montiarc.cocos.SimpleConnectorSourceExists;

/**
 * This class checks all context conditions directly related to connector
 * definitions
 *
 * @author Andreas Wortmann
 */
public class ConnectorTests extends AbstractCoCoTest {
  
  private static final String MP = "";
  
  private static final String PACKAGE = "component.body.connectors";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSimpleConnectorSourceExists() {
    checkValid(MP, PACKAGE + "." + "SimpleConnectorSourceExists");
  }
  
  @Test
  public void testSimpleConnectorSourceExistsExternal() {
    checkValid(MP, PACKAGE + "." + "SimpleConnectorSourceExistsExternal");
  }
  
  @Test
  public void testSimpleConnectorSourceUnqualified() {
    checkValid(MP, PACKAGE + "." + "SimpleConnectorSourceUnqualified");
  }
  
  @Test
  public void testSimpleConnectorSourceNonExistant() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "SimpleConnectorSourceNonExistant");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SimpleConnectorSourceExists()),
        node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA072"));
  }
  
  
  
  // -------------------
  
  @Test
  public void testSimpleConnectorSourceInvalid() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "SimpleConnectorSourceFullyQualified");
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA008"));
  }
  
  @Test
  public void testConnectorNotPiercingThroughInterface() {
    checkValid(MP, PACKAGE + "." + "ConnectorNotPiercingThroughInterface");
  }
  
  @Test
  public void testConnectorSourceInvalid() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ConnectorPiercingOutwardsThroughInterface");
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA070"));
  }
  
  @Test
  public void testConnectorTargetInvalid() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ConnectorPiercingInwardsThroughInterface");
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA070"));
  }
  
  /* Checks multiple instances of wrong connectors with connectors piercing
   * through interfaces and qualified simple connector sources. */
  @Test
  public void testMultipleWrongConnectors() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "WrongConnectors");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
        .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
        .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified());
    checkInvalid(cocos, node, new AbstractCoCoTestExpectedErrorInfo(4, "xMA008", "xMA070"));
  }
}
