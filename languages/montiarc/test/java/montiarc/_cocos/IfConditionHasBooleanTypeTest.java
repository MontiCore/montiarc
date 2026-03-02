/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.MCError.IF_CONDITION_NOT_BOOLEAN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link IfConditionHasBooleanType}.
 */
class IfConditionHasBooleanTypeTest extends MontiArcTestBase {

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new IfConditionHasBooleanType());

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
    checker.addCoCo(new IfConditionHasBooleanType());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // component no behavior
      arg("""
        component ValidComp1 { }
        """
      ),
      // if-condition with primitive boolean type
      arg("""
        component ValidComp2 {
          boolean v = false;
          automaton {
            initial state S;
            S -> S / {
              if (v) { }
            }
          }
        }
        """
      ),
      // if-condition with boxed boolean type
      arg("""
        import java.lang.Boolean;
        component ValidComp3 {
          Boolean v = false;
          automaton {
            initial state S;
            S -> S / {
              if (v) { }
            }
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // if-condition with expression of non-boolean primitive type
      arg("""
          component InvalidComp1 {
            int v = 0;
            automaton {
              initial state S;
              S -> S / {
                if (v) { }
              }
            }
          }
          """,
        IF_CONDITION_NOT_BOOLEAN
      ),
      // if-condition with expression of non-boolean boxed type
      arg("""
          import java.lang.Integer;
          component InvalidComp2 {
            Integer v = 0;
            automaton {
              initial state S;
              S -> S / {
                if (v) { }
              }
            }
          }
          """,
        IF_CONDITION_NOT_BOOLEAN
      )
    );
  }
}
