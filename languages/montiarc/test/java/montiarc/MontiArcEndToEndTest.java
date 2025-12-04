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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static de.se_rwth.commons.logging.Finding.Type.ERROR;
import static de.se_rwth.commons.logging.Finding.Type.WARNING;
import static montiarc.util.ArcError.CIRCULAR_INHERITANCE;
import static montiarc.util.ArcError.CONNECTOR_TIMING_MISMATCH;
import static montiarc.util.ArcError.CONNECTOR_TYPE_MISMATCH;
import static montiarc.util.ArcError.COMPONENT_REFERENCE_CYCLE;
import static montiarc.util.ArcError.IN_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.IN_PORT_UNUSED;
import static montiarc.util.ArcError.OUT_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.OUT_PORT_UNUSED;
import static montiarc.util.ArcError.PORT_MULTIPLE_SENDER;
import static montiarc.util.ArcError.SOURCE_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.TARGET_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.UNIQUE_IDENTIFIER_NAMES;
import static montiarc.util.MCError.CANT_FIND_SYMBOL;
import static montiarc.util.MCError.MISSING_COMPONENT;
import static montiarc.util.SCError.PRECONDITION_NOT_BOOLEAN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class groups the end-to-end tests for MontiArc. These tests mainly
 * ensure that the integration of the parser, symbol table, and cocos produces
 * the correct error messages for invalid models. That is, they verify that the
 * appropriate errors are raised, with properly formatted messages, at the
 * correct source positions.
 */
public class MontiArcEndToEndTest extends MontiArcTestBase {

  protected final static String TEST_DIR = "endtoend";

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("invalidModelAndErrorProvider")
  @DisableIfDisplayName(contains = {
    "CircularInheritanceTest7",
    "MissingComponentTest7",
    "MissingComponentTest8",
    "MissingComponentTest9",
    "NameClash",
    "SelfReferentialComponentInConnectorTest"
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
    "MissingPortTypeTest9",
    "MissingPortTypeTest10",
    "MissingPortTypeTest11",
    "MissingPortTypeTest12",
    "NameClash",
    "SelfReferentialComponentInConnectorTest",
    "PortNotConnectedTest",
    "PortMultipleSender",
    "ConnectorMismatchDirectionTest4"
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
                            String model,
                            int lineStart,
                            int columnStart,
                            Error error,
                            String... args) {
    return new Finding(type, String.format(error.toString(), (Object[]) args),
      sourcePos(model, lineStart, columnStart)
    );
  }

  protected static Stream<Arguments> invalidModelAndErrorProvider() {
    return Stream.of(
      arg("CircularInheritanceTest1",
        mp("CircularInheritance1.arc"),
        fn(ERROR, "CircularInheritance1.arc", 6, 32, 6, 60, CIRCULAR_INHERITANCE, "CircularInheritance1")
      ),
      arg("CircularInheritanceTest2",
        mp("CircularInheritance2A.arc", "CircularInheritance2B.arc"),
        fn(ERROR, "CircularInheritance2A.arc", 7, 33, 7, 62, CIRCULAR_INHERITANCE, "CircularInheritance2A"),
        fn(ERROR, "CircularInheritance2B.arc", 7, 33, 7, 62, CIRCULAR_INHERITANCE, "CircularInheritance2B")
      ),
      arg("CircularInheritanceTest3",
        mp("CircularInheritance3.arc"),
        fn(ERROR, "CircularInheritance3.arc", 7, 19, 7, 32, CIRCULAR_INHERITANCE, "Inner")
      ),
      arg("CircularInheritanceTest4",
        mp("CircularInheritance4.arc"),
        fn(ERROR, "CircularInheritance4.arc", 8, 20, 8, 34, CIRCULAR_INHERITANCE, "Inner1"),
        fn(ERROR, "CircularInheritance4.arc", 9, 20, 9, 34, CIRCULAR_INHERITANCE, "Inner2")
      ),
      arg("CircularInheritanceTest5",
        mp("CircularInheritance5.arc"),
        fn(ERROR, "CircularInheritance5.arc", 9, 26, 9, 44, CIRCULAR_INHERITANCE, "InnerInner")
      ),
      arg("CircularInheritanceTest6",
        mp("CircularInheritance6.arc"),
        fn(ERROR, "CircularInheritance6.arc", 9, 27, 9, 46, CIRCULAR_INHERITANCE, "InnerInner1"),
        fn(ERROR, "CircularInheritance6.arc", 10, 27, 10, 46, CIRCULAR_INHERITANCE, "InnerInner2")
      ),
      arg("CircularInheritanceTest7",
        mp("CircularInheritance7A.arc"),
        fn(ERROR, "CircularInheritance7A.arc", 7, 33, 7, 62, CIRCULAR_INHERITANCE, "CircularInheritance7A")
      ),
      arg("MissingComponentTest1",
        mp("MissingComponent1.arc"),
        fn(ERROR, "MissingComponent1.arc", 8, 3, 8, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest2",
        mp("MissingComponent2.arc"),
        fn(ERROR, "MissingComponent2.arc", 8, 3, 8, 14, MISSING_COMPONENT, "a.b.Missing")
      ),
      arg("MissingComponentTest3",
        mp("MissingComponent3.arc"),
        fn(ERROR, "MissingComponent3.arc", 8, 3, 8, 10, MISSING_COMPONENT, "Missing"),
        fn(ERROR, "MissingComponent3.arc", 9, 3, 9, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest4",
        mp("MissingComponent4.arc"),
        fn(ERROR, "MissingComponent4.arc", 9, 5, 9, 12, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest5",
        mp("MissingComponent5.arc"),
        fn(ERROR, "MissingComponent5.arc", 13, 5, 13, 12, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest7",
        mp("MissingComponent7.arc"),
        fn(ERROR, "MissingComponent7.arc", 9, 3, 9, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest8",
        mp("MissingComponent8.arc"),
        fn(ERROR, "MissingComponent8.arc", 8, 3, 8, 10, MISSING_COMPONENT, "boolean")
      ),
      arg("MissingComponentTest9",
        mp("MissingComponent9.arc"),
        fn(ERROR, "MissingComponent9.arc", 8, 3, 8, 10, MISSING_COMPONENT, "boolean"),
        fn(ERROR, "MissingComponent9.arc", 9, 3, 8, 7, MISSING_COMPONENT, "byte"),
        fn(ERROR, "MissingComponent9.arc", 10, 3, 8, 8, MISSING_COMPONENT, "short"),
        fn(ERROR, "MissingComponent9.arc", 11, 3, 8, 6, MISSING_COMPONENT, "int"),
        fn(ERROR, "MissingComponent9.arc", 12, 3, 8, 7, MISSING_COMPONENT, "long"),
        fn(ERROR, "MissingComponent9.arc", 13, 3, 8, 8, MISSING_COMPONENT, "float"),
        fn(ERROR, "MissingComponent9.arc", 14, 3, 8, 9, MISSING_COMPONENT, "double")
      ),
      arg("MissingPortTypeTest1",
        mp("MissingPortType1.arc"),
        fn(ERROR, "MissingPortType1.arc", 9, 11, 9, 18, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest2",
        mp("MissingPortType2.arc"),
        fn(ERROR, "MissingPortType2.arc", 10, 12, 10, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest3",
        mp("MissingPortType3.arc"),
        fn(ERROR, "MissingPortType3.arc", 9, 11, 9, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, "MissingPortType3.arc", 10, 12, 10, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest4",
        mp("MissingPortType4.arc"),
        fn(ERROR, "MissingPortType4.arc", 13, 13, 13, 20, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, "MissingPortType4.arc", 14, 14, 14, 21, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest9",
        mp("MissingPortType9.arc"),
        fn(ERROR, "MissingPortType9.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, "MissingPortType9.arc", 15, 13, 15, 14, PRECONDITION_NOT_BOOLEAN, "Obscure")
      ),
      arg("MissingPortTypeTest10",
        mp("MissingPortType10.arc"),
        fn(ERROR, "MissingPortType10.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest11",
        mp("MissingPortType11.arc"),
        fn(ERROR, "MissingPortType11.arc", 11, 12, 11, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest12",
        mp("MissingPortType12.arc"),
        fn(ERROR, "MissingPortType12.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, "MissingPortType12.arc", 11, 12, 11, 19, CANT_FIND_SYMBOL, "Missing")
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
      ),
      arg("MissingSymbolsInConnectorTest1",
        mp("MissingSymbolsInConnector1.arc"),
        fn(ERROR, "MissingSymbolsInConnector1.arc", 12, 3, 12, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingSymbolsInConnectorTest2",
        mp("MissingSymbolsInConnector2.arc"),
        fn(ERROR, "MissingSymbolsInConnector2.arc", 9, 11, 9, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, "MissingSymbolsInConnector2.arc", 10, 12, 10, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingSymbolsInConnectorTest3",
        mp("MissingSymbolsInConnector3.arc"),
        fn(ERROR, "MissingSymbolsInConnector3.arc", 14, 13, 14, 20, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, "MissingSymbolsInConnector3.arc", 15, 14, 15, 21, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingSymbolsInConnectorTest4",
        mp("MissingSymbolsInConnector4.arc", "MissingPortType3.arc"),
        fn(ERROR, "MissingPortType3.arc", 9, 11, 9, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, "MissingPortType3.arc", 10, 12, 10, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingSymbolsInConnectorTest5",
        mp("MissingSymbolsInConnector5A.arc"),
        fn(ERROR, "MissingSymbolsInConnector5A.arc", 18, 8, 18, 13, CONNECTOR_TYPE_MISMATCH, "Missing", "int"),
        fn(ERROR, "MissingSymbolsInConnector5A.arc", 19, 12, 19, 13, CONNECTOR_TYPE_MISMATCH, "int", "Missing")
      ),
      arg("SelfReferentialComponentTest1",
        mp("SelfReferentialComponent1.arc"),
        fn(ERROR, "SelfReferentialComponent1.arc", 9, 29, 9, 32, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent1", "SelfReferentialComponent1 -> SelfReferentialComponent1")
      ),
      arg("SelfReferentialComponentTest2",
        mp("SelfReferentialComponent2.arc"),
        fn(ERROR, "SelfReferentialComponent2.arc", 9, 29, 9, 33, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent2", "SelfReferentialComponent2 -> SelfReferentialComponent2"),
        fn(ERROR, "SelfReferentialComponent2.arc", 10, 29, 10, 33, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent2", "SelfReferentialComponent2 -> SelfReferentialComponent2")
      ),
      arg("SelfReferentialComponentTest3",
        mp("SelfReferentialComponent3A.arc"),
        fn(ERROR, "SelfReferentialComponent3A.arc", 13, 30, 13, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent3A", "SelfReferentialComponent3A -> SelfReferentialComponent3B -> SelfReferentialComponent3A"),
        fn(ERROR, "SelfReferentialComponent3A.arc", 16, 32, 16, 36, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent3B", "SelfReferentialComponent3B -> SelfReferentialComponent3A -> SelfReferentialComponent3B")
      ),
      arg("SelfReferentialComponentTest4",
        mp("SelfReferentialComponent4A.arc", "SelfReferentialComponent4B.arc"),
        fn(ERROR, "SelfReferentialComponent4A.arc", 11, 30, 11, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent4A", "SelfReferentialComponent4A -> SelfReferentialComponent4B -> SelfReferentialComponent4A"),
        fn(ERROR, "SelfReferentialComponent4B.arc", 11, 30, 11, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent4B", "SelfReferentialComponent4B -> SelfReferentialComponent4A -> SelfReferentialComponent4B")
      ),
      arg("SelfReferentialComponentTest5",
        mp("SelfReferentialComponent5A.arc"),
        fn(ERROR, "SelfReferentialComponent5A.arc", 12, 30, 12, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent5A", "SelfReferentialComponent5A -> SelfReferentialComponent5B -> SelfReferentialComponent5A")
      ),
      arg("SelfReferentialComponentInConnectorTest",
        mp("SelfReferentialComponentInConnector.arc"),
        fn(ERROR, "SelfReferentialComponentInConnector.arc", 12, 39, 12, 43, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponentInConnector", "SelfReferentialComponentInConnector -> SelfReferentialComponentInConnector"),
        fn(ERROR, "SelfReferentialComponentInConnector.arc", 12, 45, 12, 49, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponentInConnector", "SelfReferentialComponentInConnector -> SelfReferentialComponentInConnector")
      ),
      arg("PortUnusedTest1",
        mp("PortUnused1.arc"),
        fn(WARNING, "PortUnused1.arc", 8, 15, 8, 17, IN_PORT_UNUSED, "i1")
      ),
      arg("PortUnusedTest2",
        mp("PortUnused2.arc"),
        fn(WARNING, "PortUnused2.arc", 8, 19, 8, 21, IN_PORT_UNUSED, "i2")
      ),
      arg("PortUnusedTest3",
        mp("PortUnused3.arc"),
        fn(WARNING, "PortUnused3.arc", 9, 16, 9, 18, OUT_PORT_UNUSED, "o1")
      ),
      arg("PortUnusedTest4",
        mp("PortUnused4.arc"),
        fn(WARNING, "PortUnused4.arc", 9, 20, 9, 22, OUT_PORT_UNUSED, "o2")
      ),
      arg("PortUnusedTest5",
        mp("PortUnused5.arc"),
        fn(WARNING, "PortUnused5.arc", 13, 17, 13, 19, IN_PORT_UNUSED, "i1"),
        fn(WARNING, "PortUnused5.arc", 13, 21, 13, 23, IN_PORT_UNUSED, "i2"),
        fn(WARNING, "PortUnused5.arc", 14, 18, 14, 20, OUT_PORT_UNUSED, "o1"),
        fn(WARNING, "PortUnused5.arc", 14, 22, 14, 24, OUT_PORT_UNUSED, "o2")
      ),
      arg("PortNotConnectedTest1",
        mp("PortNotConnected1.arc"),
        fn(ERROR, "PortNotConnected1.arc", 17, 9, 17, 12, IN_PORT_NOT_CONNECTED, "sub.i1")
      ),
      arg("PortNotConnectedTest2",
        mp("PortNotConnected2.arc"),
        fn(ERROR, "PortNotConnected2.arc", 17, 9, 17, 12, IN_PORT_NOT_CONNECTED, "sub.i2")
      ),
      arg("PortNotConnectedTest3",
        mp("PortNotConnected3.arc"),
        fn(WARNING, "PortNotConnected3.arc", 17, 9, 17, 12, OUT_PORT_NOT_CONNECTED, "sub.o1")
      ),
      arg("PortNotConnectedTest4",
        mp("PortNotConnected4.arc"),
        fn(WARNING, "PortNotConnected4.arc", 17, 9, 17, 12, OUT_PORT_NOT_CONNECTED, "sub.o2")
      ),
      arg("PortNotConnectedTest5",
        mp("PortNotConnected5.arc"),
        fn(ERROR, "PortNotConnected5.arc", 23, 22, 23, 26, IN_PORT_NOT_CONNECTED, "sub2.i1"),
        fn(ERROR, "PortNotConnected5.arc", 23, 22, 23, 26, IN_PORT_NOT_CONNECTED, "sub2.i2"),
        fn(WARNING, "PortNotConnected5.arc", 23, 22, 23, 26, OUT_PORT_NOT_CONNECTED, "sub2.o1"),
        fn(WARNING, "PortNotConnected5.arc", 23, 22, 23, 26, OUT_PORT_NOT_CONNECTED, "sub2.o2")
      ),
      arg("PortNotConnectedTest6",
        mp("PortNotConnected6A.arc", "PortNotConnected6B.arc"),
        fn(ERROR, "PortNotConnected6A.arc", 11, 22, 11, 25, IN_PORT_NOT_CONNECTED, "sub.i1"),
        fn(ERROR, "PortNotConnected6A.arc", 11, 22, 11, 25, IN_PORT_NOT_CONNECTED, "sub.i2"),
        fn(WARNING, "PortNotConnected6A.arc", 11, 22, 11, 25, OUT_PORT_NOT_CONNECTED, "sub.o1"),
        fn(WARNING, "PortNotConnected6A.arc", 11, 22, 11, 25, OUT_PORT_NOT_CONNECTED, "sub.o2")
      ),
      arg("PortNotConnectedTest7",
        mp("PortNotConnected7A.arc"),
        fn(ERROR, "PortNotConnected7A.arc", 12, 22, 12, 25, IN_PORT_NOT_CONNECTED, "sub.i1"),
        fn(ERROR, "PortNotConnected7A.arc", 12, 22, 12, 25, IN_PORT_NOT_CONNECTED, "sub.i2"),
        fn(WARNING, "PortNotConnected7A.arc", 12, 22, 12, 25, OUT_PORT_NOT_CONNECTED, "sub.o1"),
        fn(WARNING, "PortNotConnected7A.arc", 12, 22, 12, 25, OUT_PORT_NOT_CONNECTED, "sub.o2")
      ),
      arg("PortMultipleSenderTest1",
        mp("PortMultipleSender1.arc"),
        fn(ERROR, "PortMultipleSender1.arc", 21, 13, 21, 14, PORT_MULTIPLE_SENDER, "o")
      ),
      arg("PortMultipleSenderTest2",
        mp("PortMultipleSender2.arc"),
        fn(ERROR, "PortMultipleSender2.arc", 20, 15, 20, 16, PORT_MULTIPLE_SENDER, "o")
      ),
      arg("PortMultipleSenderTest3",
        mp("PortMultipleSender3.arc"),
        fn(ERROR, "PortMultipleSender3.arc", 20, 9, 20, 14, PORT_MULTIPLE_SENDER, "sub.i")
      ),
      arg("ConnectorMismatchDirectionTest1",
        mp("ConnectorMismatchDirection1.arc"),
        fn(ERROR, "ConnectorMismatchDirection1.arc", 24, 3, 24, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, "ConnectorMismatchDirection1.arc", 24, 8, 24, 13, TARGET_DIRECTION_MISMATCH, "sub.o"),
        fn(ERROR, "ConnectorMismatchDirection1.arc", 25, 3, 25, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, "ConnectorMismatchDirection1.arc", 25, 12, 25, 13, TARGET_DIRECTION_MISMATCH, "i")
      ),
      arg("ConnectorMismatchDirectionTest2",
        mp("ConnectorMismatchDirection2.arc"),
        fn(ERROR, "ConnectorMismatchDirection2.arc", 26, 3, 26, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, "ConnectorMismatchDirection2.arc", 26, 8, 26, 14, TARGET_DIRECTION_MISMATCH, "sub.o1"),
        fn(ERROR, "ConnectorMismatchDirection2.arc", 26, 16, 26, 22, TARGET_DIRECTION_MISMATCH, "sub.o2"),
        fn(ERROR, "ConnectorMismatchDirection2.arc", 27, 3, 27, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, "ConnectorMismatchDirection2.arc", 27, 12, 27, 14, TARGET_DIRECTION_MISMATCH, "i1"),
        fn(ERROR, "ConnectorMismatchDirection2.arc", 27, 16, 27, 18, TARGET_DIRECTION_MISMATCH, "i2")
      ),
      arg("ConnectorMismatchDirectionTest3",
        mp("ConnectorMismatchDirection3A.arc", "ConnectorMismatchDirection3B.arc"),
        fn(ERROR, "ConnectorMismatchDirection3A.arc", 19, 3, 19, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, "ConnectorMismatchDirection3A.arc", 19, 8, 19, 13, TARGET_DIRECTION_MISMATCH, "sub.o"),
        fn(ERROR, "ConnectorMismatchDirection3A.arc", 20, 3, 20, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, "ConnectorMismatchDirection3A.arc", 20, 12, 20, 13, TARGET_DIRECTION_MISMATCH, "i")
      ),
      arg("ConnectorMismatchDirectionTest4",
        mp("ConnectorMismatchDirection4A.arc"),
        fn(ERROR, "ConnectorMismatchDirection4A.arc", 20, 3, 20, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, "ConnectorMismatchDirection4A.arc", 20, 8, 20, 13, TARGET_DIRECTION_MISMATCH, "sub.o"),
        fn(ERROR, "ConnectorMismatchDirection4A.arc", 21, 3, 21, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, "ConnectorMismatchDirection4A.arc", 21, 12, 21, 13, TARGET_DIRECTION_MISMATCH, "i")
      ),
      arg("ConnectorMismatchTypeTest1",
        mp("ConnectorMismatchType1.arc"),
        fn(ERROR, "ConnectorMismatchType1.arc", 26, 8, 26, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType1.arc", 28, 13, 28, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType1.arc", 30, 13, 30, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("ConnectorMismatchTypeTest2",
        mp("ConnectorMismatchType2.arc"),
        fn(ERROR, "ConnectorMismatchType2.arc", 32, 8, 32, 15, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType2.arc", 32, 17, 32, 24, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType2.arc", 34, 13, 34, 20, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType2.arc", 34, 22, 34, 29, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType2.arc", 36, 13, 36, 15, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType2.arc", 36, 17, 36, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("ConnectorMismatchTypeTest3",
        mp("ConnectorMismatchType3A.arc", "ConnectorMismatchType3B.arc"),
        fn(ERROR, "ConnectorMismatchType3A.arc", 21, 8, 21, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType3A.arc", 23, 13, 23, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType3A.arc", 25, 13, 25, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("ConnectorMismatchTypeTest4",
        mp("ConnectorMismatchType4A.arc"),
        fn(ERROR, "ConnectorMismatchType4A.arc", 22, 8, 22, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType4A.arc", 24, 13, 24, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, "ConnectorMismatchType4A.arc", 26, 13, 26, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("ConnectorMismatchTimingTest1",
        mp("ConnectorMismatchTiming1.arc"),
        fn(ERROR, "ConnectorMismatchTiming1.arc", 26, 8, 26, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming1.arc", 28, 13, 28, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming1.arc", 30, 13, 30, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTimingTest2",
        mp("ConnectorMismatchTiming2.arc"),
        fn(ERROR, "ConnectorMismatchTiming2.arc", 26, 8, 26, 15, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming2.arc", 26, 17, 26, 24, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming2.arc", 28, 13, 28, 20, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming2.arc", 28, 22, 28, 29, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming2.arc", 30, 13, 30, 15, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming2.arc", 30, 17, 30, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTimingTest3",
        mp("ConnectorMismatchTiming3A.arc", "ConnectorMismatchTiming3B.arc"),
        fn(ERROR, "ConnectorMismatchTiming3A.arc", 21, 8, 21, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming3A.arc", 23, 13, 23, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming3A.arc", 25, 13, 25, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTimingTest4",
        mp("ConnectorMismatchTiming4A.arc"),
        fn(ERROR, "ConnectorMismatchTiming4A.arc", 22, 8, 22, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming4A.arc", 24, 13, 24, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, "ConnectorMismatchTiming4A.arc", 26, 13, 26, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      )
    );
  }

  protected static Stream<Arguments> invalidModelAndError4VariabilityProvider() {
    return Stream.of(
    );
  }

  private static String mp(@NotNull String... models) {
    return path(models);
  }

  private static String path(@NotNull String... models) {
    Preconditions.checkNotNull(models);
    List<String> parts = Arrays.stream(models)
      .map(model -> Paths.get(TEST_RESOURCE, TEST_DIR, model).toString())
      .toList();
    return String.join(File.pathSeparator, parts);
  }

  private static SourcePosition sourcePos(@NotNull String model, int line, int column) {
    // columns in monticore are parsed with a -1 offset, workaround until fixed
    int columnFix = column - 1;
    return new SourcePosition(line, columnFix, Paths.get(TEST_RESOURCE, TEST_DIR, model).toAbsolutePath().toString());
  }
}
