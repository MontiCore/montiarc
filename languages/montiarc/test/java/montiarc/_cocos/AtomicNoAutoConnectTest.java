/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import comfortablearc._cocos.AtomicNoAutoConnect;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ComfortableArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link AtomicNoAutoConnect}.
 */
public class AtomicNoAutoConnectTest extends MontiArcAbstractTest {

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    compile("component A { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic (empty) no autoconnect
    "component Comp1 { } ",
    // atomic (automaton) no autoconnect
    "component Comp2 { " +
      "automaton { } " +
      "}",
    // composed no autoconnect
    "component Comp3 { " +
      "A a; " +
      "}",
    // composed autoconnect port
    "component Comp4 { " +
      "A a; autoconnect port; " +
      "}",
    // composed autoconnect type
    "component Comp5 { " +
      "A a; " +
      "autoconnect type; " +
      "}",
    // composed autoconnect off
    "component Comp6 { " +
      "A a; " +
      "autoconnect off; " +
      "}",
    // composed (inner) no autoconnect
    "component Comp7 { " +
      "component Inner { } " +
      "Inner sub; " +
      "}",
    // composed (inner) autoconnect
    "component Comp8 { " +
      "component Inner { } " +
      "Inner sub; " +
      "autoconnect port; " +
      "}",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtomicNoAutoConnect());

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
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtomicNoAutoConnect());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // atomic (empty) autoconnect port
      arg("component Comp1 { " +
          "autoconnect port; " +
          "}",
        ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT),
      // atomic (empty) autoconnect type
      arg("component Comp2 { " +
          "autoconnect type; " +
          "}",
        ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT),
      // atomic (empty) autoconnect type
      arg("component Comp3 { " +
          "autoconnect off; " +
          "}",
        ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT),
      // atomic (automaton) autoconnect port
      arg("component Comp4 { " +
          "automaton { } " +
          "autoconnect port; " +
          "}",
        // atomic (unused inner) autoconnect port
        ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT),
      arg("component Comp5 { " +
          "automaton { } " +
          "autoconnect port; " +
          "}",
        ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT),
      // atomic two autoconnects
      arg("component Comp6 { " +
          "autoconnect port; " +
          "autoconnect port; " +
          "}",
        ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT),
      // atomic inner autoconnect
      arg("component Comp7 { " +
        "component Inner { " +
        "autoconnect port; " +
        "} " +
        "Inner sub; " +
        "}",
        ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT)
    );
  }
}
