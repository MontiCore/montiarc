/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import dynamicmontiarc._cocos.DynamicMontiArcCoCoChecker;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.InPortUniqueSender;
import org.junit.Test;

/**
 * TODO
 *
 * @author (last commit)
 * @version ,
 * @since TODO
 */
public class PortTest extends AbstractCoCoTest {

  private static final String PACKAGE = "components.body.ports";

  @Test
  public void testPortsWithAmbiguousSenders() {
    ASTMontiArcNode node =
        loadComponentAST(PACKAGE + "." + "PortsWithAmbiguousSenders");
    final DynamicMontiArcCoCoChecker cocos =
        new DynamicMontiArcCoCoChecker().addCoCo(new InPortUniqueSender());
    final ExpectedErrorInfo errors =
        new ExpectedErrorInfo(4, "xMA005");
    checkInvalid(cocos, node, errors);
  }
}
