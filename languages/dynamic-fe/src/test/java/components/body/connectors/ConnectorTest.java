/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import dynamicmontiarc._cocos.DynamicMontiArcCoCoChecker;
import dynamicmontiarc.cocos.PortUsage;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc.cocos.SubComponentsConnected;
import org.junit.Test;

/**
 * TODO
 */
public class ConnectorTest extends AbstractCoCoTest {

  private static final String PACKAGE = "components.body.connectors";

  @Test
  public void testConnectingInnerCompToIncomingPort() {
    final String modelName =
        PACKAGE + "." + "ConnectingInnerCompToIncomingPort";
    DynamicMontiArcCoCoChecker cocos =
        new DynamicMontiArcCoCoChecker().addCoCo(new PortUsage());
    cocos.addCoCo(new SubComponentsConnected());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(4, "xMA097", "xMA098", "xMA104", "xMA105");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
}
