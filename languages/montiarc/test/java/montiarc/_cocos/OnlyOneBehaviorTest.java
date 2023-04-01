/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.OnlyOneBehavior;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link OnlyOneBehavior}.
 */
public class OnlyOneBehaviorTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic no behavior
    "component Comp1 { }",
    // atomic with automaton
    "component Comp2 { " +
      "automaton { } " +
      "}",
    // inner with automaton
    "component Comp3 { " +
      "component Inner { " +
      "automaton { } " +
      "} " +
      "}",
    // two inner with automata
    "component Comp4 { " +
      "component Inner1 { " +
      "automaton { } " +
      "} " +
      "component Inner2 { " +
      "automaton { } " +
      "} " +
      "}",
    // atomic with ajava
    "component Comp5 { " +
      "compute { } " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new OnlyOneBehavior());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new OnlyOneBehavior());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // atomic two automata
      arg("component Comp1 { " +
          "automaton { } " +
          "automaton { } " +
          "}",
        ArcError.MULTIPLE_BEHAVIOR,
        ArcError.MULTIPLE_BEHAVIOR),
      // atomic two ajava blocks
      arg("component Comp2 { " +
          "compute { } " +
          "compute { } " +
          "}",
        ArcError.MULTIPLE_BEHAVIOR,
        ArcError.MULTIPLE_BEHAVIOR),
      // atomic with automaton and ajava
      arg("component Comp3 { " +
          "automaton { } " +
          "compute { } " +
          "}",
        ArcError.MULTIPLE_BEHAVIOR,
        ArcError.MULTIPLE_BEHAVIOR),
      // inner with two automata
      arg("component Comp4 { " +
          "component Inner { " +
          "automaton { } " +
          "automaton { } " +
          "} " +
          "}",
        ArcError.MULTIPLE_BEHAVIOR,
        ArcError.MULTIPLE_BEHAVIOR)
    );
  }
}
