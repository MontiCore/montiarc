/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public abstract class TestBase {

  @BeforeAll
  public static void initLog() {
    LogStub.init();
  }

  @AfterEach
  public void clearLog() {
    LogStub.clearFindings();
    LogStub.clearPrints();
  }
}
