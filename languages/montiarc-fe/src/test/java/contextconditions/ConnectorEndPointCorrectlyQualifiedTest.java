/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package contextconditions;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTConnectorCoCo;
import montiarc._cocos.MontiArcASTSimpleConnectorCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.ConnectorEndPointIsCorrectlyQualified;

/**
 * @author Crispin Kirchner
 */
public class ConnectorEndPointCorrectlyQualifiedTest extends AbstractCoCoTest {
  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSimpleConnectorSourceValid() {
    checkValid("contextconditions", "valid.SimpleConnectorSourceUnqualified");
  }
  
  @Test
  public void testSimpleConnectorSourceInvalid() {
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        getAstNode("contextconditions", "invalid.SimpleConnectorSourceFullyQualified"),
        new ExpectedErrorInfo(1, "xMA008"));
  }
  
  @Test
  public void testConnectorNotPiercingThroughInterface() {
    checkValid("contextconditions", "valid.ConnectorNotPiercingThroughInterface");
  }
  
  @Test
  public void testConnectorSourceInvalid() {
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        getAstNode("contextconditions", "invalid.ConnectorPiercingOutwardsThroughInterface"),
        new ExpectedErrorInfo(1, "xMA070"));
  }
  
  @Test
  public void testConnectorTargetInvalid() {
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        getAstNode("contextconditions", "invalid.ConnectorPiercingInwardsThroughInterface"),
        new ExpectedErrorInfo(1, "xMA070"));
  }

  /*
    Checks multiple instances of wrong connectors with connectors piercing through interfaces and
     qualified simple connector sources.
   */
  @Test
  public void testMultipleWrongConnectors(){
    ASTMontiArcNode node = getAstNode("arc/coco/conventions", "conv.WrongConnector");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified())
            .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified());
    checkInvalid(cocos, node, new ExpectedErrorInfo(4, "xMA008","xMA070"));
  }
}
