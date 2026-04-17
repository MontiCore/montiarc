/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.CheckNoFieldDependencyCycles;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.CIRCULAR_FIELDS_DEPENDENCY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link CheckNoFieldDependencyCycles}.
 */
class CheckNoFieldDependencyCyclesTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  public void shouldNotReportError(String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new CheckNoFieldDependencyCycles());

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
    checker.addCoCo(new CheckNoFieldDependencyCycles());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // field with a literal initializer, no dependency
      arg("""
        component ValidComp1 {
          int a = 0;
        }
        """
      ),
      // field depending on a field declared before it
      arg("""
        component ValidComp2 {
          int a = 0;
          int b = a;
        }"""
      ),
      // field depending on a field declared after it
      arg("""
        component ValidComp3 {
          int a = b;
          int b = 0;
        }"""
      )
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // two fields directly depending on each other
      arg("""
          component InvalidComp1 {
            int a = b;
            int b = a;
          }
          """,
        CIRCULAR_FIELDS_DEPENDENCY
      ),
      // three fields transitively depending on each other
      arg("""
          component InvalidComp2 {
            int a = c;
            int b = a;
            int c = b;
          }
          """,
        CIRCULAR_FIELDS_DEPENDENCY
      ),
      // two fields depending on each other through arithmetic expressions
      arg("""
          component InvalidComp3 {
            int a = 1 + b;
            int b = 2 + a;
          }
          """,
        CIRCULAR_FIELDS_DEPENDENCY
      )
    );
  }
}
