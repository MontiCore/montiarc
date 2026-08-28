/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.MontiArcError.ROOT_NO_INSTANCE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link RootNoInstance}.
 */
class RootNoInstanceTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // root component without an instance name
    "component ValidComp1 { }",
    // nested component declared with an instance name (only the root is restricted)
    "component ValidComp2 { component Inner { } Inner sub; }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RootNoInstance());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RootNoInstance());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // root component declared with a single instance name
      arg("component InvalidComp1 a { }",
        ROOT_NO_INSTANCE),
      // root component declared with two instance names
      arg("component InvalidComp2 a1, a2 { }",
        ROOT_NO_INSTANCE),
      // root component declared with three instance names
      arg("component InvalidComp3 a1, a2, a3 { }",
        ROOT_NO_INSTANCE)
    );
  }
}
