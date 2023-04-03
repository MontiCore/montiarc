/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._cocos.UniqueStates;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.SCError;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link UniqueStates}
 */
public class UniqueStatesTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component Comp1 { }",
    // no states
    "component Comp2 { " +
      "automaton { } " +
      "}",
    // one state
    "component Comp3 { " +
      "automaton { " +
      "initial state s; " +
      "} " +
      "}",
    // multiple unique states
    "component Comp4 { " +
      "automaton { " +
      "initial state s1; " +
      "state s2; " +
      "state s3; " +
      "} " +
      "}",
    // inner with same states
    "component Comp5 { " +
      "automaton { " +
      "initial state s; " +
      "} " +
      "component Inner { " +
      "automaton { " +
      "initial state s2; " +
      "} " +
      "} " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UniqueStates());

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
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UniqueStates());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // two states same name
      arg("component Comp1 { " +
          "automaton { " +
          "state s; " +
          "state s; " +
          "} " +
          "}",
        SCError.DUPLICATE_STATE),
      // three states same name
      arg("component Comp2 { " +
          "automaton { " +
          "state s; " +
          "state s; " +
          "state s; " +
          "} " +
          "}",
        SCError.DUPLICATE_STATE),
      // hierarchical states same name
      arg("component Comp3 { " +
          "automaton { " +
          "state s1; " +
          "state s2 { " +
          "state s1; " +
          "}; " +
          "} " +
          "}",
        SCError.DUPLICATE_STATE)
    );
  }
}
