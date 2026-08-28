/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;

import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.MCError.DUPLICATE_VAR_IN_SCOPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VarDeclarationNameAlreadyDefinedInScope}.
 */
class VarDeclarationNameAlreadyDefinedInScopeTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    Log.clearFindings();

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    Log.clearFindings();

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // local variable declared in a single transition action
      arg("component ValidComp1 { automaton { initial state S; S -> S / { int i = 1; } } }"),
      // local variable declared in a compute block
      arg("component ValidComp2 { compute { int i = 1; } }"),
      // same-named local variable declared independently in two separate transition actions
      arg("component ValidComp3 { automaton { initial state S; S -> S / { int i = 1;} S -> S / { int i = 1; } } }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // two local variables with the same name in a compute block
      arg("component InvalidComp1 { compute { int i = 1; int i = 1; } }",
        DUPLICATE_VAR_IN_SCOPE),
      // two local variables with the same name in a transition action
      arg("component InvalidComp2 { automaton { initial state S; S -> S / { int i = 1; int i = 1; } } }",
        DUPLICATE_VAR_IN_SCOPE),
      // four local variables with the same name but different types in a compute block
      arg("component InvalidComp3 { compute { boolean a = 1 == 0; int a = 1; double a = 1; long a = 1; } }",
        DUPLICATE_VAR_IN_SCOPE,
        DUPLICATE_VAR_IN_SCOPE,
        DUPLICATE_VAR_IN_SCOPE),
      // field name shadowed independently by a local variable in two separate transition actions
      arg("component InvalidComp4 { int i = 0; automaton { initial state S; S -> S / { int i = 1;} S -> S / { int i = 1; } } }",
        DUPLICATE_VAR_IN_SCOPE,
        DUPLICATE_VAR_IN_SCOPE),
      // field name shadowed by a local variable in a compute block
      arg("component InvalidComp5 { int i = 1; compute { int i = 1; } }",
        DUPLICATE_VAR_IN_SCOPE),
      // field name shadowed by a local variable in a transition action
      arg("component InvalidComp6 { int i = 1; automaton { initial state S; S -> S / { int i = 1; } } }",
        DUPLICATE_VAR_IN_SCOPE),
      // local variable shadowed by a for-loop control variable of the same name
      arg("component InvalidComp7 { automaton { initial state S; S -> S / { int i = 1; for (int i = 0; i < 1; i++) {} } } }",
        DUPLICATE_VAR_IN_SCOPE),
      // local variable shadowed by a same-named variable in a nested block
      arg("component InvalidComp8 { automaton { initial state S; S -> S / { int i = 1; { int i = 0; } } } }",
        DUPLICATE_VAR_IN_SCOPE)
    );
  }
}
