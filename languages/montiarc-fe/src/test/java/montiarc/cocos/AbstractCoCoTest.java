/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._cocos.MontiArcCoCoChecker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractCoCoTest extends AbstractTest {

  protected MontiArcTool tool;
  protected MontiArcCoCoChecker checker;

  @BeforeAll
  public static void init() {
    MontiArcMill.init();
    Log.enableFailQuick(false);
  }

  protected MontiArcTool getTool() {
    return this.tool;
  }

  @BeforeEach
  public void setUpTool() {
    this.tool = new MontiArcTool();
  }

  protected MontiArcCoCoChecker getChecker() {
    return this.checker;
  }

  @BeforeEach
  public void setUpChecker() {
    this.checker = new MontiArcCoCoChecker();
    this.registerCoCos();
  }

  /**
   * Provider for CoCos to execute during tests.
   */
  abstract protected void registerCoCos();

  @BeforeEach
  public void setUpLog() {
    Log.getFindings().clear();
  }

  @BeforeEach
  public void clearGlobalScope() {
    MontiArcMill.globalScope().clear();
  }
}