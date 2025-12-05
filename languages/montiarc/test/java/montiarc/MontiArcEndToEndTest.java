/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.io.File;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static de.se_rwth.commons.logging.Finding.Type.ERROR;
import static de.se_rwth.commons.logging.Finding.Type.WARNING;
import static montiarc.util.ArcAutomataError.CANT_FIND_MSG_EVENT_SYMBOL;
import static montiarc.util.ArcError.CIRCULAR_INHERITANCE;
import static montiarc.util.ArcError.COMPONENT_REFERENCE_CYCLE;
import static montiarc.util.ArcError.CONNECTOR_TIMING_MISMATCH;
import static montiarc.util.ArcError.CONNECTOR_TYPE_MISMATCH;
import static montiarc.util.ArcError.IN_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.IN_PORT_UNUSED;
import static montiarc.util.ArcError.MISSING_PORT;
import static montiarc.util.ArcError.MISSING_SUBCOMPONENT;
import static montiarc.util.ArcError.OUT_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.OUT_PORT_UNUSED;
import static montiarc.util.ArcError.PORT_MULTIPLE_SENDER;
import static montiarc.util.ArcError.SOURCE_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.TARGET_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.UNIQUE_IDENTIFIER_NAMES;
import static montiarc.util.MCError.CANT_FIND_SYMBOL;
import static montiarc.util.MCError.CANT_FIND_SYMBOL_IN_EXPRESSION;
import static montiarc.util.MCError.MISSING_COMPONENT;
import static montiarc.util.SCError.CANT_FIND_SOURCE;
import static montiarc.util.SCError.CANT_FIND_TARGET;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class groups the end-to-end tests for MontiArc. These tests mainly
 * ensure that the integration of the parser, symbol table, and cocos produces
 * the correct error messages for invalid models. That is, they verify that the
 * appropriate errors are raised, with properly formatted messages, at the
 * correct source positions.
 */
public class MontiArcEndToEndTest extends MontiArcTestBase {

  private final static String TEST_DIR = "endtoend";

  private final static String PKG_AUT = "automata";

  private final static String PKG_COMP = "components";

  private final static String PKG_CPOS = "composition";

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("invalidModelAndErrorProvider")
  @DisableIfDisplayName(contains = {
    "MissingComponentTest7",
    "MissingComponentTest8",
    "MissingComponentTest9",
    "NameClash",
    "SelfReferentialComponentWithCompositionTest",
    "CircularInheritanceWithCompositionTest3"
  })
  void invalidModelsShouldFailEndToEnd(@NotNull String name,
                                       @NotNull String modelPath,
                                       @NotNull Finding[] findings) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(modelPath);
    Preconditions.checkNotNull(findings);
    Preconditions.checkArgument(!name.isBlank());

    String symbolPath = Paths.get(TEST_RESOURCE, TEST_DIR).toString();

    // Given
    String[] args = new String[]{"-i", modelPath, "-path", symbolPath, "-novar"};
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.run(args);

    // Then
    assertThat(Log.getFindings())
      .containsExactlyInAnyOrder(findings);
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("invalidModelAndErrorProvider")
  @MethodSource("invalidModelAndError4VariabilityProvider")
  @DisableIfDisplayName(contains = {
    "CircularInheritanceTest7",
    "MissingComponentTest7",
    "MissingComponentTest8",
    "MissingComponentTest9",
    "NameClash",
    "SelfReferentialComponentWithCompositionTest",
    "PortNotConnectedTest",
    "PortMultipleSender",
    "ConnectorMismatchDirectionTest4",
    "MissingSymbolsInConnectorTest6",
    "MissingSymbolsInConnectorTest7",
    "CircularInheritanceWithCompositionTest",
    "MissingEventTest5",
    "MissingEventTest6",
    "MissingSymbolsInGuardTest3",
    "MissingSymbolsInTransitionActionTest",
    "MissingSymbolsInEntryAction"
  })
  void invalidModelsShouldFailEndToEnd4Variability(@NotNull String name,
                                                   @NotNull String modelPath,
                                                   @NotNull Finding[] findings) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(modelPath);
    Preconditions.checkNotNull(findings);
    Preconditions.checkArgument(!name.isBlank());

    String symbolPath = Paths.get(TEST_RESOURCE, TEST_DIR).toString();

    // Given
    String[] args = new String[]{"-i", modelPath, "-path", symbolPath};
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.run(args);

    // Then
    assertThat(Log.getFindings())
      .containsExactlyInAnyOrder(findings);
  }

  protected static Stream<Arguments> invalidModelAndErrorProvider() {
    return Stream.of(
      arg("MissingEventTest1",
        mpk(PKG_AUT, "MissingEvent1.arc"),
        fn(ERROR, PKG_AUT, "MissingEvent1.arc", 11, 12, 11, 15, CANT_FIND_MSG_EVENT_SYMBOL, "msg")
      ),
      arg("MissingEventTest2",
        mpk(PKG_AUT, "MissingEvent2.arc"),
        fn(ERROR, PKG_AUT, "MissingEvent2.arc", 12, 12, 12, 16, CANT_FIND_MSG_EVENT_SYMBOL, "msg1"),
        fn(ERROR, PKG_AUT, "MissingEvent2.arc", 13, 12, 13, 16, CANT_FIND_MSG_EVENT_SYMBOL, "msg2")
      ),
      arg("MissingEventTest3",
        mpk(PKG_AUT, "MissingEvent3.arc"),
        fn(ERROR, PKG_AUT, "MissingEvent3.arc", 16, 12, 16, 13, CANT_FIND_MSG_EVENT_SYMBOL, "o")
      ),
      arg("MissingEventTest4",
        mpk(PKG_AUT, "MissingEvent4.arc"),
        fn(ERROR, PKG_AUT, "MissingEvent4.arc", 16, 12, 16, 13, CANT_FIND_MSG_EVENT_SYMBOL, "p"),
        fn(ERROR, PKG_AUT, "MissingEvent4.arc", 17, 12, 17, 13, CANT_FIND_MSG_EVENT_SYMBOL, "v")
      ),
      arg("MissingEventTest5",
        mpk(PKG_AUT, "MissingEvent5.arc"),
        fn(ERROR, PKG_AUT, "MissingEvent5.arc", 12, 10, 12, 13, CANT_FIND_MSG_EVENT_SYMBOL, "msg")
      ),
      arg("MissingEventTest6",
        mpk(PKG_AUT, "MissingEvent6.arc"),
        fn(ERROR, PKG_AUT, "MissingEvent6.arc", 12, 16, 12, 20, CANT_FIND_MSG_EVENT_SYMBOL, "msg1"),
        fn(ERROR, PKG_AUT, "MissingEvent6.arc", 16, 16, 16, 20, CANT_FIND_MSG_EVENT_SYMBOL, "msg2"),
        fn(ERROR, PKG_AUT, "MissingEvent6.arc", 18, 18, 18, 22, CANT_FIND_MSG_EVENT_SYMBOL, "msg3")
      ),
      arg("MissingStateTest1",
        mpk(PKG_AUT, "MissingState1.arc"),
        fn(ERROR, PKG_AUT, "MissingState1.arc", 12, 5, 12, 12, CANT_FIND_SOURCE, "M")
      ),
      arg("MissingStateTest2",
        mpk(PKG_AUT, "MissingState2.arc"),
        fn(ERROR, PKG_AUT, "MissingState2.arc", 12, 5, 12, 12, CANT_FIND_TARGET, "M")
      ),
      arg("MissingStateTest3",
        mpk(PKG_AUT, "MissingState3.arc"),
        fn(ERROR, PKG_AUT, "MissingState3.arc", 12, 5, 12, 14, CANT_FIND_SOURCE, "M1"),
        fn(ERROR, PKG_AUT, "MissingState3.arc", 12, 5, 12, 14, CANT_FIND_TARGET, "M2")
      ),
      arg("MissingStateTest4",
        mpk(PKG_AUT, "MissingState4.arc"),
        fn(ERROR, PKG_AUT, "MissingState4.arc", 12, 5, 12, 13, CANT_FIND_SOURCE, "M1"),
        fn(ERROR, PKG_AUT, "MissingState4.arc", 13, 5, 13, 13, CANT_FIND_TARGET, "M2")
      ),
      arg("MissingStateTest5",
        mpk(PKG_AUT, "MissingState5.arc"),
        fn(ERROR, PKG_AUT, "MissingState5.arc", 12, 7, 12, 16, CANT_FIND_SOURCE, "M1"),
        fn(ERROR, PKG_AUT, "MissingState5.arc", 12, 7, 12, 16, CANT_FIND_TARGET, "M2"),
        fn(ERROR, PKG_AUT, "MissingState5.arc", 16, 7, 16, 16, CANT_FIND_SOURCE, "M3"),
        fn(ERROR, PKG_AUT, "MissingState5.arc", 16, 7, 16, 16, CANT_FIND_TARGET, "M4"),
        fn(ERROR, PKG_AUT, "MissingState5.arc", 18, 9, 18, 18, CANT_FIND_SOURCE, "M5"),
        fn(ERROR, PKG_AUT, "MissingState5.arc", 18, 9, 18, 18, CANT_FIND_TARGET, "M6")
      ),
      arg("MissingSymbolsInEntryActionTest1",
        mpk(PKG_AUT, "MissingSymbolsInEntryAction1.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction1.arc", 12, 15, 12, 16, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInEntryActionTest2",
        mpk(PKG_AUT, "MissingSymbolsInEntryAction2.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction2.arc", 12, 17, 12, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInEntryActionTest3",
        mpk(PKG_AUT, "MissingSymbolsInEntryAction3.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction3.arc", 12, 17, 12, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "a1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction3.arc", 13, 17, 13, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "a2")
      ),
      arg("MissingSymbolsInEntryActionTest4",
        mpk(PKG_AUT, "MissingSymbolsInEntryAction4.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction4.arc", 12, 17, 12, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "a1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction4.arc", 16, 17, 16, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "a2"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction4.arc", 18, 19, 18, 21, CANT_FIND_SYMBOL_IN_EXPRESSION, "a3")
      ),
      arg("MissingSymbolsInGuardTest1",
        mpk(PKG_AUT, "MissingSymbolsInGuard1.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard1.arc", 12, 13, 12, 14, CANT_FIND_SYMBOL_IN_EXPRESSION, "g")
      ),
      arg("MissingSymbolsInGuardTest2",
        mpk(PKG_AUT, "MissingSymbolsInGuard2.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard2.arc", 12, 13, 12, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "g1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard2.arc", 13, 13, 13, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "g2")
      ),
      arg("MissingSymbolsInGuardTest3",
        mpk(PKG_AUT, "MissingSymbolsInGuard3.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard3.arc", 12, 11, 12, 12, CANT_FIND_SYMBOL_IN_EXPRESSION, "g")
      ),
      arg("MissingSymbolsInGuardTest4",
        mpk(PKG_AUT, "MissingSymbolsInGuard4.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard4.arc", 12, 17, 12, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "g1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard4.arc", 16, 17, 16, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "g2"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard4.arc", 18, 19, 18, 21, CANT_FIND_SYMBOL_IN_EXPRESSION, "g3")
      ),
      arg("MissingSymbolsInTransitionActionTest1",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction1.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction1.arc", 12, 14, 12, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInTransitionActionTest2",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction2.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction2.arc", 12, 16, 12, 17, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInTransitionActionTest3",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction3.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction3.arc", 12, 16, 12, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "a1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction3.arc", 13, 16, 13, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "a2")
      ),
      arg("MissingSymbolsInTransitionActionTest4",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction4.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction4.arc", 12, 12, 12, 13, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInTransitionActionTest5",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction5.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction5.arc", 12, 14, 12, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInTransitionActionTest6",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction6.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction6.arc", 12, 20, 12, 22, CANT_FIND_SYMBOL_IN_EXPRESSION, "a1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction6.arc", 16, 20, 16, 22, CANT_FIND_SYMBOL_IN_EXPRESSION, "a2"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction6.arc", 18, 22, 18, 24, CANT_FIND_SYMBOL_IN_EXPRESSION, "a3")
      ),
      arg("CircularInheritanceTest1",
        mpk(PKG_COMP, "CircularInheritance1.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance1.arc", 7, 32, 7, 60, CIRCULAR_INHERITANCE, "CircularInheritance1")
      ),
      arg("CircularInheritanceTest2",
        mpk(PKG_COMP, "CircularInheritance2A.arc", "CircularInheritance2B.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance2A.arc", 8, 33, 8, 62, CIRCULAR_INHERITANCE, "CircularInheritance2A"),
        fn(ERROR, PKG_COMP, "CircularInheritance2B.arc", 8, 33, 8, 62, CIRCULAR_INHERITANCE, "CircularInheritance2B")
      ),
      arg("CircularInheritanceTest3",
        mpk(PKG_COMP, "CircularInheritance3.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance3.arc", 8, 19, 8, 32, CIRCULAR_INHERITANCE, "Inner")
      ),
      arg("CircularInheritanceTest4",
        mpk(PKG_COMP, "CircularInheritance4.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance4.arc", 9, 20, 9, 34, CIRCULAR_INHERITANCE, "Inner1"),
        fn(ERROR, PKG_COMP, "CircularInheritance4.arc", 10, 20, 10, 34, CIRCULAR_INHERITANCE, "Inner2")
      ),
      arg("CircularInheritanceTest5",
        mpk(PKG_COMP, "CircularInheritance5.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance5.arc", 10, 26, 10, 44, CIRCULAR_INHERITANCE, "InnerInner")
      ),
      arg("CircularInheritanceTest6",
        mpk(PKG_COMP, "CircularInheritance6.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance6.arc", 10, 27, 10, 46, CIRCULAR_INHERITANCE, "InnerInner1"),
        fn(ERROR, PKG_COMP, "CircularInheritance6.arc", 11, 27, 11, 46, CIRCULAR_INHERITANCE, "InnerInner2")
      ),
      arg("CircularInheritanceTest7",
        mpk(PKG_COMP, "CircularInheritance7A.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance7A.arc", 8, 33, 8, 62, CIRCULAR_INHERITANCE, "CircularInheritance7A")
      ),
      arg("MissingComponentTest1",
        mpk(PKG_COMP, "MissingComponent1.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent1.arc", 9, 3, 9, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest2",
        mpk(PKG_COMP, "MissingComponent2.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent2.arc", 9, 3, 9, 14, MISSING_COMPONENT, "a.b.Missing")
      ),
      arg("MissingComponentTest3",
        mpk(PKG_COMP, "MissingComponent3.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent3.arc", 9, 3, 9, 10, MISSING_COMPONENT, "Missing"),
        fn(ERROR, PKG_COMP, "MissingComponent3.arc", 10, 3, 10, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest4",
        mpk(PKG_COMP, "MissingComponent4.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent4.arc", 10, 5, 10, 12, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest5",
        mpk(PKG_COMP, "MissingComponent5.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent5.arc", 14, 5, 14, 12, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest7",
        mpk(PKG_COMP, "MissingComponent7.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent7.arc", 10, 3, 10, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest8",
        mpk(PKG_COMP, "MissingComponent8.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent8.arc", 9, 3, 9, 10, MISSING_COMPONENT, "boolean")
      ),
      arg("MissingComponentTest9",
        mpk(PKG_COMP, "MissingComponent9.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 9, 3, 9, 10, MISSING_COMPONENT, "boolean"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 10, 3, 10, 7, MISSING_COMPONENT, "byte"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 11, 3, 11, 8, MISSING_COMPONENT, "short"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 12, 3, 12, 6, MISSING_COMPONENT, "int"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 13, 3, 13, 7, MISSING_COMPONENT, "long"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 14, 3, 14, 8, MISSING_COMPONENT, "float"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 15, 3, 16, 9, MISSING_COMPONENT, "double")
      ),
      arg("MissingPortTypeTest1",
        mpk(PKG_COMP, "MissingPortType1.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType1.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest2",
        mpk(PKG_COMP, "MissingPortType2.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType2.arc", 11, 12, 11, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest3",
        mpk(PKG_COMP, "MissingPortType3.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType3.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_COMP, "MissingPortType3.arc", 11, 12, 11, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest4",
        mpk(PKG_COMP, "MissingPortType4.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType4.arc", 14, 13, 14, 20, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_COMP, "MissingPortType4.arc", 15, 14, 15, 21, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest9",
        mpk(PKG_COMP, "MissingPortType9.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType9.arc", 11, 11, 11, 18, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest10",
        mpk(PKG_COMP, "MissingPortType10.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType10.arc", 11, 11, 11, 18, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest11",
        mpk(PKG_COMP, "MissingPortType11.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType11.arc", 12, 12, 12, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest12",
        mpk(PKG_COMP, "MissingPortType12.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType12.arc", 11, 11, 11, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_COMP, "MissingPortType12.arc", 12, 12, 12, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("PortUnusedTest1",
        mpk(PKG_COMP, "PortUnused1.arc"),
        fn(WARNING, PKG_COMP, "PortUnused1.arc", 9, 15, 9, 17, IN_PORT_UNUSED, "i1")
      ),
      arg("PortUnusedTest2",
        mpk(PKG_COMP, "PortUnused2.arc"),
        fn(WARNING, PKG_COMP, "PortUnused2.arc", 9, 19, 9, 21, IN_PORT_UNUSED, "i2")
      ),
      arg("PortUnusedTest3",
        mpk(PKG_COMP, "PortUnused3.arc"),
        fn(WARNING, PKG_COMP, "PortUnused3.arc", 10, 16, 10, 18, OUT_PORT_UNUSED, "o1")
      ),
      arg("PortUnusedTest4",
        mpk(PKG_COMP, "PortUnused4.arc"),
        fn(WARNING, PKG_COMP, "PortUnused4.arc", 10, 20, 10, 22, OUT_PORT_UNUSED, "o2")
      ),
      arg("PortUnusedTest5",
        mpk(PKG_COMP, "PortUnused5.arc"),
        fn(WARNING, PKG_COMP, "PortUnused5.arc", 14, 17, 14, 19, IN_PORT_UNUSED, "i1"),
        fn(WARNING, PKG_COMP, "PortUnused5.arc", 14, 21, 14, 23, IN_PORT_UNUSED, "i2"),
        fn(WARNING, PKG_COMP, "PortUnused5.arc", 15, 18, 15, 20, OUT_PORT_UNUSED, "o1"),
        fn(WARNING, PKG_COMP, "PortUnused5.arc", 15, 22, 15, 24, OUT_PORT_UNUSED, "o2")
      ),
      arg("SelfReferentialComponentTest1",
        mpk(PKG_COMP, "SelfReferentialComponent1.arc"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent1.arc", 10, 29, 10, 32, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent1", "SelfReferentialComponent1 -> SelfReferentialComponent1")
      ),
      arg("SelfReferentialComponentTest2",
        mpk(PKG_COMP, "SelfReferentialComponent2.arc"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent2.arc", 10, 29, 10, 33, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent2", "SelfReferentialComponent2 -> SelfReferentialComponent2"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent2.arc", 11, 29, 11, 33, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent2", "SelfReferentialComponent2 -> SelfReferentialComponent2")
      ),
      arg("SelfReferentialComponentTest3",
        mpk(PKG_COMP, "SelfReferentialComponent3A.arc"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent3A.arc", 14, 30, 14, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent3A", "SelfReferentialComponent3A -> SelfReferentialComponent3B -> SelfReferentialComponent3A"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent3A.arc", 17, 32, 17, 36, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent3B", "SelfReferentialComponent3B -> SelfReferentialComponent3A -> SelfReferentialComponent3B")
      ),
      arg("SelfReferentialComponentTest4",
        mpk(PKG_COMP, "SelfReferentialComponent4A.arc", "SelfReferentialComponent4B.arc"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent4A.arc", 12, 30, 12, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent4A", "SelfReferentialComponent4A -> SelfReferentialComponent4B -> SelfReferentialComponent4A"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent4B.arc", 12, 30, 12, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent4B", "SelfReferentialComponent4B -> SelfReferentialComponent4A -> SelfReferentialComponent4B")
      ),
      arg("SelfReferentialComponentTest5",
        mpk(PKG_COMP, "SelfReferentialComponent5A.arc"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent5A.arc", 13, 30, 13, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent5A", "SelfReferentialComponent5A -> SelfReferentialComponent5B -> SelfReferentialComponent5A")
      ),
      arg("CircularInheritanceWithCompositionTest1",
        mpk(PKG_CPOS, "CircularInheritanceWithComposition1.arc"),
        fn(ERROR, PKG_CPOS, "CircularInheritanceWithComposition1.arc", 8, 47, 8, 90, CIRCULAR_INHERITANCE, "CircularInheritanceWithComposition1"),
        fn(ERROR, PKG_CPOS, "CircularInheritanceWithComposition1.arc", 13, 20, 13, 34, CIRCULAR_INHERITANCE, "Inner1"),
        fn(ERROR, PKG_CPOS, "CircularInheritanceWithComposition1.arc", 18, 20, 18, 34, CIRCULAR_INHERITANCE, "Inner2")
      ),
      arg("CircularInheritanceWithCompositionTest2",
        mpk(PKG_CPOS, "CircularInheritanceWithComposition2A.arc", "CircularInheritanceWithComposition2B.arc", "CircularInheritanceWithComposition2C.arc"),
        fn(ERROR, PKG_CPOS, "CircularInheritanceWithComposition2B.arc", 7, 48, 7, 92, CIRCULAR_INHERITANCE, "CircularInheritanceWithComposition2B"),
        fn(ERROR, PKG_CPOS, "CircularInheritanceWithComposition2C.arc", 7, 48, 7, 92, CIRCULAR_INHERITANCE, "CircularInheritanceWithComposition2C")
      ),
      arg("CircularInheritanceWithCompositionTest3",
        mpk(PKG_CPOS, "CircularInheritanceWithComposition3A.arc"),
        new Finding[]{}
      ),
      arg("ConnectorMismatchDirectionTest1",
        mpk(PKG_CPOS, "ConnectorMismatchDirection1.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection1.arc", 25, 3, 25, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection1.arc", 25, 8, 25, 13, TARGET_DIRECTION_MISMATCH, "sub.o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection1.arc", 26, 3, 26, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection1.arc", 26, 12, 26, 13, TARGET_DIRECTION_MISMATCH, "i")
      ),
      arg("ConnectorMismatchDirectionTest2",
        mpk(PKG_CPOS, "ConnectorMismatchDirection2.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 27, 3, 27, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 27, 8, 27, 14, TARGET_DIRECTION_MISMATCH, "sub.o1"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 27, 16, 27, 22, TARGET_DIRECTION_MISMATCH, "sub.o2"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 28, 3, 28, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 28, 12, 28, 14, TARGET_DIRECTION_MISMATCH, "i1"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 28, 16, 28, 18, TARGET_DIRECTION_MISMATCH, "i2")
      ),
      arg("ConnectorMismatchDirectionTest3",
        mpk(PKG_CPOS, "ConnectorMismatchDirection3A.arc", "ConnectorMismatchDirection3B.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection3A.arc", 20, 3, 20, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection3A.arc", 20, 8, 20, 13, TARGET_DIRECTION_MISMATCH, "sub.o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection3A.arc", 21, 3, 21, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection3A.arc", 21, 12, 21, 13, TARGET_DIRECTION_MISMATCH, "i")
      ),
      arg("ConnectorMismatchDirectionTest4",
        mpk(PKG_CPOS, "ConnectorMismatchDirection4A.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection4A.arc", 21, 3, 21, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection4A.arc", 21, 8, 21, 13, TARGET_DIRECTION_MISMATCH, "sub.o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection4A.arc", 22, 3, 22, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection4A.arc", 22, 12, 22, 13, TARGET_DIRECTION_MISMATCH, "i")
      ),
      arg("ConnectorMismatchTimingTest1",
        mpk(PKG_CPOS, "ConnectorMismatchTiming1.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming1.arc", 27, 8, 27, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming1.arc", 29, 13, 29, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming1.arc", 31, 13, 31, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTimingTest2",
        mpk(PKG_CPOS, "ConnectorMismatchTiming2.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 27, 8, 27, 15, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 27, 17, 27, 24, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 29, 13, 29, 20, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 29, 22, 29, 29, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 31, 13, 31, 15, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 31, 17, 31, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTimingTest3",
        mpk(PKG_CPOS, "ConnectorMismatchTiming3A.arc", "ConnectorMismatchTiming3B.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming3A.arc", 22, 8, 22, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming3A.arc", 24, 13, 24, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming3A.arc", 26, 13, 26, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTimingTest4",
        mpk(PKG_CPOS, "ConnectorMismatchTiming4A.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming4A.arc", 23, 8, 23, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming4A.arc", 25, 13, 25, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming4A.arc", 27, 13, 27, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTypeTest1",
        mpk(PKG_CPOS, "ConnectorMismatchType1.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType1.arc", 27, 8, 27, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType1.arc", 29, 13, 29, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType1.arc", 31, 13, 31, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("ConnectorMismatchTypeTest2",
        mpk(PKG_CPOS, "ConnectorMismatchType2.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 33, 8, 33, 15, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 33, 17, 33, 24, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 35, 13, 35, 20, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 35, 22, 35, 29, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 37, 13, 37, 15, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 37, 17, 37, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("ConnectorMismatchTypeTest3",
        mpk(PKG_CPOS, "ConnectorMismatchType3A.arc", "ConnectorMismatchType3B.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType3A.arc", 22, 8, 22, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType3A.arc", 24, 13, 24, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType3A.arc", 26, 13, 26, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("ConnectorMismatchTypeTest4",
        mpk(PKG_CPOS, "ConnectorMismatchType4A.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType4A.arc", 23, 8, 23, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType4A.arc", 25, 13, 25, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType4A.arc", 27, 13, 27, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("MissingSymbolsInConnectorTest1",
        mpk(PKG_CPOS, "MissingSymbolsInConnector1.arc"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector1.arc", 13, 3, 13, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingSymbolsInConnectorTest2",
        mpk(PKG_CPOS, "MissingSymbolsInConnector2.arc"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector2.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector2.arc", 11, 12, 11, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingSymbolsInConnectorTest3",
        mpk(PKG_CPOS, "MissingSymbolsInConnector3.arc"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector3.arc", 15, 13, 15, 20, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector3.arc", 16, 14, 16, 21, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingSymbolsInConnectorTest4",
        path(mpk(PKG_CPOS, "MissingSymbolsInConnector4.arc"), mpk(PKG_COMP, "MissingPortType3.arc")),
        fn(ERROR, PKG_COMP, "MissingPortType3.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_COMP, "MissingPortType3.arc", 11, 12, 11, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingSymbolsInConnectorTest5",
        mpk(PKG_CPOS, "MissingSymbolsInConnector5A.arc"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector5A.arc", 19, 8, 19, 13, CONNECTOR_TYPE_MISMATCH, "Missing", "int"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector5A.arc", 20, 12, 20, 13, CONNECTOR_TYPE_MISMATCH, "int", "Missing")
      ),
      arg("MissingSymbolsInConnectorTest6",
        mpk(PKG_CPOS, "MissingSymbolsInConnector6.arc"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector6.arc", 17, 8, 17, 14, MISSING_SUBCOMPONENT, "subA"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector6.arc", 18, 3, 18, 9, MISSING_SUBCOMPONENT, "subA")
      ),
      arg("MissingSymbolsInConnectorTest7",
        mpk(PKG_CPOS, "MissingSymbolsInConnector7.arc"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector7.arc", 17, 3, 17, 4, MISSING_PORT, "i"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector7.arc", 18, 12, 18, 13, MISSING_PORT, "o")
      ),
      arg("MissingSymbolsInConnectorTest8",
        mpk(PKG_CPOS, "MissingSymbolsInConnector8.arc"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector8.arc", 19, 8, 19, 13, MISSING_PORT, "sub.i"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector8.arc", 20, 3, 20, 8, MISSING_PORT, "sub.o")
      ),
      arg("MissingSymbolsInConnectorTest9",
        mpk(PKG_CPOS, "MissingSymbolsInConnector9A.arc", "MissingSymbolsInConnector9B.arc"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector9A.arc", 17, 8, 17, 13, MISSING_PORT, "sub.i"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector9A.arc", 18, 3, 18, 8, MISSING_PORT, "sub.o")
      ),
      arg("MissingSymbolsInConnectorTest10",
        mpk(PKG_CPOS, "MissingSymbolsInConnector10A.arc"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector10A.arc", 18, 8, 18, 13, MISSING_PORT, "sub.i"),
        fn(ERROR, PKG_CPOS, "MissingSymbolsInConnector10A.arc", 19, 3, 19, 8, MISSING_PORT, "sub.o")
      ),
      arg("PortMultipleSenderTest1",
        mpk(PKG_CPOS, "PortMultipleSender1.arc"),
        fn(ERROR, PKG_CPOS, "PortMultipleSender1.arc", 22, 13, 22, 14, PORT_MULTIPLE_SENDER, "o")
      ),
      arg("PortMultipleSenderTest2",
        mpk(PKG_CPOS, "PortMultipleSender2.arc"),
        fn(ERROR, PKG_CPOS, "PortMultipleSender2.arc", 21, 15, 21, 16, PORT_MULTIPLE_SENDER, "o")
      ),
      arg("PortMultipleSenderTest3",
        mpk(PKG_CPOS, "PortMultipleSender3.arc"),
        fn(ERROR, PKG_CPOS, "PortMultipleSender3.arc", 21, 9, 21, 14, PORT_MULTIPLE_SENDER, "sub.i")
      ),
      arg("PortNotConnectedTest1",
        mpk(PKG_CPOS, "PortNotConnected1.arc"),
        fn(ERROR, PKG_CPOS, "PortNotConnected1.arc", 18, 9, 18, 12, IN_PORT_NOT_CONNECTED, "sub.i1")
      ),
      arg("PortNotConnectedTest2",
        mpk(PKG_CPOS, "PortNotConnected2.arc"),
        fn(ERROR, PKG_CPOS, "PortNotConnected2.arc", 18, 9, 18, 12, IN_PORT_NOT_CONNECTED, "sub.i2")
      ),
      arg("PortNotConnectedTest3",
        mpk(PKG_CPOS, "PortNotConnected3.arc"),
        fn(WARNING, PKG_CPOS, "PortNotConnected3.arc", 18, 9, 18, 12, OUT_PORT_NOT_CONNECTED, "sub.o1")
      ),
      arg("PortNotConnectedTest4",
        mpk(PKG_CPOS, "PortNotConnected4.arc"),
        fn(WARNING, PKG_CPOS, "PortNotConnected4.arc", 18, 9, 18, 12, OUT_PORT_NOT_CONNECTED, "sub.o2")
      ),
      arg("PortNotConnectedTest5",
        mpk(PKG_CPOS, "PortNotConnected5.arc"),
        fn(ERROR, PKG_CPOS, "PortNotConnected5.arc", 24, 22, 24, 26, IN_PORT_NOT_CONNECTED, "sub2.i1"),
        fn(ERROR, PKG_CPOS, "PortNotConnected5.arc", 24, 22, 24, 26, IN_PORT_NOT_CONNECTED, "sub2.i2"),
        fn(WARNING, PKG_CPOS, "PortNotConnected5.arc", 24, 22, 24, 26, OUT_PORT_NOT_CONNECTED, "sub2.o1"),
        fn(WARNING, PKG_CPOS, "PortNotConnected5.arc", 24, 22, 24, 26, OUT_PORT_NOT_CONNECTED, "sub2.o2")
      ),
      arg("PortNotConnectedTest6",
        mpk(PKG_CPOS, "PortNotConnected6A.arc", "PortNotConnected6B.arc"),
        fn(ERROR, PKG_CPOS, "PortNotConnected6A.arc", 12, 22, 12, 25, IN_PORT_NOT_CONNECTED, "sub.i1"),
        fn(ERROR, PKG_CPOS, "PortNotConnected6A.arc", 12, 22, 12, 25, IN_PORT_NOT_CONNECTED, "sub.i2"),
        fn(WARNING, PKG_CPOS, "PortNotConnected6A.arc", 12, 22, 12, 25, OUT_PORT_NOT_CONNECTED, "sub.o1"),
        fn(WARNING, PKG_CPOS, "PortNotConnected6A.arc", 12, 22, 12, 25, OUT_PORT_NOT_CONNECTED, "sub.o2")
      ),
      arg("PortNotConnectedTest7",
        mpk(PKG_CPOS, "PortNotConnected7A.arc"),
        fn(ERROR, PKG_CPOS, "PortNotConnected7A.arc", 13, 22, 13, 25, IN_PORT_NOT_CONNECTED, "sub.i1"),
        fn(ERROR, PKG_CPOS, "PortNotConnected7A.arc", 13, 22, 13, 25, IN_PORT_NOT_CONNECTED, "sub.i2"),
        fn(WARNING, PKG_CPOS, "PortNotConnected7A.arc", 13, 22, 13, 25, OUT_PORT_NOT_CONNECTED, "sub.o1"),
        fn(WARNING, PKG_CPOS, "PortNotConnected7A.arc", 13, 22, 13, 25, OUT_PORT_NOT_CONNECTED, "sub.o2")
      ),
      arg("SelfReferentialComponentWithCompositionTest",
        mpk(PKG_CPOS, "SelfReferentialComponentWithComposition.arc"),
        fn(ERROR, PKG_CPOS, "SelfReferentialComponentWithComposition.arc", 13, 39, 13, 43, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponentWithComposition", "SelfReferentialComponentWithComposition -> SelfReferentialComponentWithComposition"),
        fn(ERROR, PKG_CPOS, "SelfReferentialComponentWithComposition.arc", 13, 45, 13, 49, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponentWithComposition", "SelfReferentialComponentWithComposition -> SelfReferentialComponentWithComposition")
      ),
      arg("NameClashParamParamTest",
        mp("NameClashParamParam.arc"),
        fn(ERROR, "NameClashParamParam.arc", 7, 42, 7, 43, UNIQUE_IDENTIFIER_NAMES, "p")
      ),
      arg("NameClashParamPortTest",
        mp("NameClashParamPort.arc"),
        fn(ERROR, "NameClashParamPort.arc", 9, 15, 9, 16, UNIQUE_IDENTIFIER_NAMES, "p")
      ),
      arg("NameClashParamVarTest",
        mp("NameClashParamVar.arc"),
        fn(ERROR, "NameClashParamVar.arc", 12, 7, 12, 8, UNIQUE_IDENTIFIER_NAMES, "p")
      ),
      arg("NameClashPortPortTest1",
        mp("NameClashPortPort1.arc"),
        fn(ERROR, "NameClashPortPort1.arc", 10, 15, 10, 16, UNIQUE_IDENTIFIER_NAMES, "i")
      ),
      arg("NameClashPortPortTest2",
        mp("NameClashPortPort2.arc"),
        fn(ERROR, "NameClashPortPort2.arc", 11, 16, 11, 17, UNIQUE_IDENTIFIER_NAMES, "o")
      ),
      arg("NameClashPortVarTest",
        mp("NameClashPortVar.arc"),
        fn(ERROR, "NameClashPortVar.arc", 12, 7, 12, 8, UNIQUE_IDENTIFIER_NAMES, "i")
      ),
      arg("NameClashTypeParamTest",
        mp("NameClashTypeParam.arc"),
        fn(ERROR, "NameClashTypeParam.arc", 7, 33, 7, 34, UNIQUE_IDENTIFIER_NAMES, "T")
      ),
      arg("NameClashVarPortTest",
        mp("NameClashVarPort.arc"),
        fn(ERROR, "NameClashVarPort.arc", 11, 15, 11, 16, UNIQUE_IDENTIFIER_NAMES, "v")
      ),
      arg("NameClashVarVarTest",
        mp("NameClashVarVar.arc"),
        fn(ERROR, "NameClashVarVar.arc", 13, 7, 13, 8, UNIQUE_IDENTIFIER_NAMES, "v")
      )
    );
  }

  protected static Stream<Arguments> invalidModelAndError4VariabilityProvider() {
    return Stream.of(
    );
  }

  private static String mp(@NotNull String... models) {
    Preconditions.checkNotNull(models);
    String[] parts = new String[models.length];
    for (int i = 0; i < models.length; i++) {
      parts[i] = Paths.get(TEST_RESOURCE, TEST_DIR, models[i]).toString();
    }
    return path(parts);
  }

  private static String mpk(@NotNull String pkg, @NotNull String... models) {
    Preconditions.checkNotNull(models);
    String[] parts = new String[models.length];
    for (int i = 0; i < models.length; i++) {
      parts[i] = Paths.get(TEST_RESOURCE, TEST_DIR, pkg, models[i]).toString();
    }
    return path(parts);
  }

  private static String path(@NotNull String... parts) {
    return String.join(File.pathSeparator, parts);
  }

  private static Finding fn(Finding.Type type,
                            String model,
                            int lineStart,
                            int columnStart,
                            int lineEnd,
                            int columnEnd,
                            Error error,
                            String... args) {
    return new Finding(type, String.format(error.toString(), (Object[]) args),
      sourcePos(model, lineStart, columnStart),
      sourcePos(model, lineEnd, columnEnd)
    );
  }

  private static Finding fn(Finding.Type type,
                            String pkg,
                            String model,
                            int lineStart,
                            int columnStart,
                            int lineEnd,
                            int columnEnd,
                            Error error,
                            String... args) {
    return new Finding(type, String.format(error.toString(), (Object[]) args),
      sourcePos(pkg, model, lineStart, columnStart),
      sourcePos(pkg, model, lineEnd, columnEnd)
    );
  }

  private static SourcePosition sourcePos(@NotNull String model, int line, int column) {
    // columns in monticore are parsed with a -1 offset, workaround until fixed
    int columnFix = column - 1;
    return new SourcePosition(line, columnFix, Paths.get(TEST_RESOURCE, TEST_DIR, model).toAbsolutePath().toString());
  }

  private static SourcePosition sourcePos(@NotNull String pkg, @NotNull String model, int line, int column) {
    // columns in monticore are parsed with a -1 offset, workaround until fixed
    int columnFix = column - 1;
    return new SourcePosition(line, columnFix, Paths.get(TEST_RESOURCE, TEST_DIR, pkg, model).toAbsolutePath().toString());
  }
}
