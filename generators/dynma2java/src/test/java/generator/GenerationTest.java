/* (c) https://github.com/MontiCore/monticore */
package generator;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.montiarcautomaton.runtimes.timesync.delegation.Port;
import org.junit.Test;

import dynamicmontiarc.DynamicMontiArcGeneratorTool;

import static junit.framework.TestCase.assertEquals;

/**
 * TODO: Write me!
 *
 *          $Date$
 *
 */
public class GenerationTest {
  
  private final static Path MODEL_PATH = Paths.get("src/test/resources");
  private final static Path TARGET_PATH = Paths.get("target/generated-resources");
  
  
  @Test
  public void generate() {
    // Generate the files for the model
    DynamicMontiArcGeneratorTool script = new DynamicMontiArcGeneratorTool();
    script.generate(MODEL_PATH.toFile(), TARGET_PATH.toFile(), Paths.get("src/main/java").toFile());
  }

  /**
   * This test only works after the generate test is executed, as otherwise
   * the generated classes for the test are missing.
   *
   * Uncomment the test after executing GenerationTest::generate
   */
//  @Test
//  public void testReconnection() {
//    components.body.mode.ReconnectionTest comp = new components.body.mode.ReconnectionTest();
//    comp.setUp();
//    comp.init();
//
//    final components.body.mode.ReconnectionTestgen.Inner a = comp.getComponentA();
//    final components.body.mode.ReconnectionTestgen.Inner b = comp.getComponentB();
//
//    final Port<String> initialInPort
//        = comp.getPortStrIn();
//    final Port<String> initialOutPort
//        = comp.getPortStrOut();
//
//    assertEquals(initialInPort, a.getPortStrIn());
//    assertEquals(initialOutPort, b.getPortStrOut());
//    assertEquals(a.getPortStrOut(), b.getPortStrIn());
//
//    initialInPort.setNextValue("adlfkj");
//    initialInPort.update();
//    comp.update();
//
//    comp.compute();
//    comp.reconfigure();
//    comp.update();
//    initialInPort.setNextValue("M2");
//    initialInPort.update();
//
//    assertEquals(initialInPort, a.getPortStrIn());
//    assertEquals(comp.getPortStrOut(), b.getPortStrOut());
//    assertEquals(a.getPortStrOut(), b.getPortStrIn());
//
//    comp.compute();
//    comp.reconfigure();
//    comp.update();
//
//    assertEquals(initialInPort, b.getPortStrIn());
//    assertEquals(comp.getPortStrOut(), a.getPortStrOut());
//    assertEquals(b.getPortStrOut(), a.getPortStrIn());
//    assertEquals(a.getComponentInnerinner().getPortStrIn(), b.getPortStrOut());
//
//    assertEquals(
//        b.getComponentInnerinner().getPortStrOut(),
//        a.getComponentInnerinner().getPortStrIn());
//
//    assertEquals(comp.getPortStrIn(), b.getComponentInnerinner().getPortStrIn());
//
//  }
}
