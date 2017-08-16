/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTConnectorCoCo;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTSimpleConnectorCoCo;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcCoCoChecker;
import de.se_rwth.commons.logging.Log;

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
        new ExpectedErrorInfo(1, "x44B7E"));
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
        new ExpectedErrorInfo(1, "xDB61C"));
  }
  
  @Test
  public void testConnectorTargetInvalid() {
    checkInvalid(
        new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointIsCorrectlyQualified()),
        getAstNode("contextconditions", "invalid.ConnectorPiercingInwardsThroughInterface"),
        new ExpectedErrorInfo(1, "xDB61C"));
  }
}
