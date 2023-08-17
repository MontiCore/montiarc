/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._cocos.NoTickEventInUntimedAutomata;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcAutomataError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class NoTickEventInUntimedAutomataTest extends MontiArcAbstractTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MontiArcMill.reset();
    MontiArcMill.init();
    BasicSymbolsMill.initializePrimitives();
    ArcAutomatonMill.initializeTick();
  }

  @Override
  public void setUp() {}

  @AfterEach
  public void tearDown() {
    Log.clearFindings();
  }

  @ParameterizedTest
  @MethodSource("validModels")
  public void shouldNotReportErrors(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoTickEventInUntimedAutomata());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportErrors(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoTickEventInUntimedAutomata());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }


  protected static Stream<Arguments> validModels() {
    return Stream.of(
      Arguments.arguments(
        "component Comp1 {" +
          "port in int i, out int o;" +
          "<<timed>> automaton {" +
          "initial state S;" +
          "S -> S;" +
          "S -> S Tick;" +
          "S -> S i;" +
          "}" +
          "}"),
      Arguments.arguments(
        "component Comp2 {" +
          "port in int i, out int o;" +
          "<<sync>> automaton {" +
          "initial state S;" +
          "S -> S;" +
          "S -> S Tick;" +
          "S -> S i;" +
          "}" +
          "}"),
      Arguments.arguments(
        "component Comp3 {" +
          "port in int i, out int o;" +
          "<<untimed>> automaton {" +
          "initial state S;" +
          "S -> S;" +
          "S -> S i;" +
          "}" +
          "}")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg(
        "component Comp1 {" +
          "port in int i, out int o;" +
          "<<untimed>> automaton {" +
          "initial state S;" +
          "S -> S;" +
          "S -> S Tick;" +
          "S -> S i;" +
          "}" +
          "}",
        ArcAutomataError.TICK_EVENT_IN_UNTIMED_AUTOMATON),
      arg(
        "component Comp2 {" +
          "port in int i, out int o;" +
          "<<untimed>> automaton {" +
          "initial state S;" +
          "S -> S;" +
          "S -> S Tick;" +
          "S -> S Tick;" +
          "S -> S i;" +
          "}" +
          "}",
        ArcAutomataError.TICK_EVENT_IN_UNTIMED_AUTOMATON,
        ArcAutomataError.TICK_EVENT_IN_UNTIMED_AUTOMATON)
    );
  }
}