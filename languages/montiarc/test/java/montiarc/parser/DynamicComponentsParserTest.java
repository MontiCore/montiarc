/* (c) https://github.com/MontiCore/monticore */
package montiarc.parser;

import montiarc.AbstractTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

/**
 * Tests the grammar of MontiArc's Dynamic-Reconfiguration-Language-Components
 */
public class DynamicComponentsParserTest extends AbstractTest {
  @ParameterizedTest
  @ValueSource(strings = {"ShiftController.arc"})
  public void shouldParseValidComponent(String fileName) {
    ParserTest.parse(Paths.get(RELATIVE_MODEL_PATH, "montiarc", "parser", fileName).toString(), false);
  }
}