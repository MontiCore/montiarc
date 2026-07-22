/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;

import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class VarDeclarationNameAlreadyDefinedInScopeTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  public void shouldNoReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    Log.clearFindings();

    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo( new VarDeclarationNameAlreadyDefinedInScope());

    checker.checkAll(ast);

    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull String ... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    Log.clearFindings();

    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo( new VarDeclarationNameAlreadyDefinedInScope());

    checker.checkAll(ast);

    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(errors);
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      arg("component Comp1 { automaton { initial state S; S -> S / { int i = 1; } } }"),
      arg("component Comp2 { compute { int i = 1; } }"),
      arg("component Comp3 { automaton { initial state S; S -> S / { int i = 1;} S -> S / { int i = 1; } } }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 { compute { int i = 1; int i = 1; } }", new String[] {
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE }),
      arg("component Comp2 { automaton { initial state S; S -> S / { int i = 1; int i = 1; } } }", new String[] {
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE }),
      arg("component Comp3 { compute { boolean a = 1 == 0; int a = 1; double a = 1; long a = 1; } }", new String[] {
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE,
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE,
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE }),
      arg("component Comp4 { int i = 0; automaton { initial state S; S -> S / { int i = 1;} S -> S / { int i = 1; } } }", new String[] {
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE,
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE }),
      arg("component Comp5 { int i = 1; compute { int i = 1; } }", new String[] {
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE }),
      arg("component Comp6 { int i = 1; automaton { initial state S; S -> S / { int i = 1; } } }", new String[] {
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE }),
      arg("component Comp6 { automaton { initial state S; S -> S / { int i = 1; for (int i = 0; i < 1; i++) {} } } }", new String[] {
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE }),
      arg("component Comp6 { automaton { initial state S; S -> S / { int i = 1; { int i = 0; } } } }", new String[] {
        VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE })
    );
  }
}
