/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd;

import de.se_rwth.commons.logging.Log;
import montiarc.ATestBase;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Arc2FDToolTestBase extends ATestBase {

  protected Arc2FDTool tool;

  @BeforeEach
  void setupTool() {
    // a previous test may have terminated the tool, which re-enables fail quick
    Log.enableFailQuick(false);
    tool = new Arc2FDTool();
    tool.init();
  }

  protected static Path getModelPath(String path) {
    return Path.of(TEST_RESOURCE).resolve(path);
  }

  protected static void assertLoggedError(@NotNull String expected) {
    assertTrue(Arrays.asList(getLoggedErrorCodes()).contains(expected),
      "Expected error code " + expected + " to have been logged");
  }

  protected static Stream<Arguments> provideValidTestCases() {
    return Stream.of(
      Arguments.of("CarExample.arc"),
      Arguments.of("ComplexExcludes.arc"),
      Arguments.of("ComplexImplies.arc"),
      Arguments.of("ComplexSubcomponentFeatures.arc"),
      Arguments.of("ComplexXor.arc"),
      Arguments.of("EmptyComponent.arc"),
      Arguments.of("SimpleAnd.arc"),
      Arguments.of("SimpleExcludes.arc"),
      Arguments.of("SimpleImplies.arc"),
      Arguments.of("SimpleOptional.arc"),
      Arguments.of("SimpleOr.arc"),
      Arguments.of("SimpleSubcomponentFeatures.arc"),
      Arguments.of("SimpleXor.arc"),
      Arguments.of("UnderscoreNames.arc")
    );
  }

  /**
   * @return Model file or directory, component, whether to include subcomponents, and the expected feature
   * diagram in <code>expected/</code>
   */
  protected static Stream<Arguments> provideExpectedFeatureDiagrams() {
    return Stream.of(
      valid("CarExample", true, "CarExample.fd"),
      valid("ComplexExcludes", true, "ComplexExcludes.fd"),
      valid("ComplexImplies", true, "ComplexImplies.fd"),
      valid("ComplexSubcomponentFeatures", true, "ComplexSubcomponentFeatures.fd"),
      valid("ComplexSubcomponentFeatures", false, "ComplexSubcomponentFeatures.flat.fd"),
      valid("ComplexXor", true, "ComplexXor.fd"),
      valid("SimpleAnd", true, "SimpleAnd.fd"),
      valid("SimpleExcludes", true, "SimpleExcludes.fd"),
      valid("SimpleImplies", true, "SimpleImplies.fd"),
      valid("SimpleOptional", true, "SimpleOptional.fd"),
      valid("SimpleOr", true, "SimpleOr.fd"),
      valid("SimpleSubcomponentFeatures", true, "SimpleSubcomponentFeatures.fd"),
      valid("SimpleSubcomponentFeatures", false, "SimpleSubcomponentFeatures.flat.fd"),
      valid("SimpleXor", true, "SimpleXor.fd"),
      valid("UnderscoreNames", true, "UnderscoreNames.fd"),
      Arguments.of("composition", "Derived", true, "Derived.fd"),
      Arguments.of("composition", "UsesParam", true, "UsesParam.fd"),
      Arguments.of("composition", "UsesParam", false, "UsesParam.flat.fd"),
      Arguments.of("composition", "Twice", true, "Twice.fd")
    );
  }

  private static Arguments valid(String component, boolean recursive, String expected) {
    return Arguments.of("valid/" + component + ".arc", component, recursive, expected);
  }

  protected static Stream<Arguments> provideIncorrectAndErrorCodes() {
    return Stream.of(
      Arguments.of("ComponentNameAsFeature.arc", new String[]{
        Arc2FDError.RECURSIVE_COMPONENT.getErrorCode()
      }),
      Arguments.of("SeparatorInName.arc", new String[]{
        Arc2FDError.INVALID_NAME.getErrorCode()
      })
    );
  }

  protected static Stream<Arguments> provideWarningAndErrorCodes() {
    return Stream.of(Arguments.of("DeadFeature.arc", new String[]{
      Arc2FDError.DEAD_FEATURE.getErrorCode()
    }));
  }
}
