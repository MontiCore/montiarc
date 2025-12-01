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
import static montiarc.util.ArcError.CIRCULAR_INHERITANCE;
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
    "NameClash"
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
    "NameClash"
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
      arg("MissingComponentTest6",
        mp("MissingComponent6.arc"),
        fn(ERROR, "MissingComponent6.arc", 12, 3, 12, 10, MISSING_COMPONENT, "Missing")
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
      arg("MissingPortTypeTest5",
        mp("MissingPortType5.arc"),
        fn(ERROR, "MissingPortType5.arc", 9, 11, 9, 18, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest6",
        mp("MissingPortType6.arc"),
        fn(ERROR, "MissingPortType6.arc", 10, 12, 10, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest7",
        mp("MissingPortType7.arc"),
        fn(ERROR, "MissingPortType7.arc", 14, 13, 14, 20, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest8",
        mp("MissingPortType8.arc"),
        fn(ERROR, "MissingPortType8.arc", 15, 14, 15, 21, CANT_FIND_SYMBOL, "Missing")
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
