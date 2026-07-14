/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
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

import static montiarc.util.MCError.TARGET_TYPE_MISMATCH;
import static montiarc.util.MCError.VAR_DECLARATION_TYPE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VarDeclarationInitializationHasCorrectType}.
 */
class VarDeclarationInitializationHasCorrectTypeTest extends MontiArcTestBase {

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
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());

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
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());

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
      // var declaration with primitive type
      arg("""
        component ValidComp2 {
          automaton {
            initial state S;
            S -> S / {
              int v = 0;
            }
          }
        }
        """
      ),
      // var declaration with generic target typing
      arg("""
        import java.util.List;
        component ValidComp3 {
          automaton {
            initial state S;
            S -> S / {
              List<int> v = [ ];
            }
          }
        }
        """
      ),
      // var declaration with complex generic target typing
      arg("""
        import java.lang.Number;
        import java.util.List;
        component ValidComp4 {
          automaton {
            initial state S;
            S -> S / {
              List<Number> v = [ 1, 2 ];
            }
          }
        }
        """
      ),
      // var declaration with mixed numeric literals and generic target typing
      arg("""
        import java.lang.Number;
        import java.util.List;
        component ValidComp5 {
          automaton {
            initial state S;
            S -> S / {
              List<Number> v = [ 1.0F, 3.33333, 1.6F ];
            }
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // type mismatch, var declaration with primitive type
      arg("""
        component InvalidComp1 {
          automaton {
            initial state S;
            S -> S / {
              int v = true;
            }
          }
        }
        """, VAR_DECLARATION_TYPE_MISMATCH
      ),
      // target type mismatch, var declaration with generic target typing
      arg("""
        import java.util.List;
        component InvalidComp2 {
          automaton {
            initial state S;
            S -> S / {
              List<int> v = [ true ];
            }
          }
        }
        """, TARGET_TYPE_MISMATCH
      ),
      // target type mismatch, var declaration with generic target typing
      arg("""
        import java.util.List;
        component InvalidComp3 {
          automaton {
            initial state S;
            S -> S / {
              List<int> v = [ 1, true ];
            }
          }
        }
        """, TARGET_TYPE_MISMATCH
      )
    );
  }
}
