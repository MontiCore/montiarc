/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.BehaviorInDecomposed;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link arcbasis._cocos.BehaviorInDecomposed}.
 */
public class BehaviorInDecomposedTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    MontiArcTestBase.compile("package a.b; component A {}");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  public void shouldNotReportError(@NotNull String model) throws IOException {
    ASTMACompilationUnit ast = MontiArcTestBase.compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new BehaviorInDecomposed());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error[] expectedErrors) throws IOException {
    ASTMACompilationUnit ast = MontiArcTestBase.compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new BehaviorInDecomposed());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      Arguments.of("component C1 { a.b.A a; }"),
      Arguments.of("component C2 { component A {} compute {} }"),
      Arguments.of("component C3 { automaton {} }"),
      Arguments.of("component C4 {}")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      Arguments.of(
        "component C5 { a.b.A a; compute {} }",
        new Error[]{ ArcError.DECOMPOSED_COMPONENT_WITH_BEHAVIOR }
      ),
      Arguments.of(
        "component C6 { a.b.A a; automaton { initial state S; } }",
        new Error[]{ ArcError.DECOMPOSED_COMPONENT_WITH_BEHAVIOR }
      ),
      Arguments.of(
        "component C7 { a.b.A a; compute {} automaton { initial state S; } }",
        new Error[]{ ArcError.DECOMPOSED_COMPONENT_WITH_BEHAVIOR }
      ),
      Arguments.of(
        "component C8 { component A { a.b.A a; compute {} } }",
        new Error[]{ ArcError.DECOMPOSED_COMPONENT_WITH_BEHAVIOR }
      )
    );
  }
}
