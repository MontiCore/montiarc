/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.MCError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ForEachIsValid4MA}.
 */
class ForEachIsValidTest extends MontiArcTestBase {

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ForEachIsValid4MA());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ForEachIsValid4MA());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      arg("""
        component ValidComp1 { }
        """
      ),
      arg("""
        import java.lang.Boolean;
        import java.util.HashSet;
        import java.util.Set;
        component ValidComp2 {
          Set<Boolean> v = HashSet();
          automaton {
            initial state S {
              -> / {
                for (Boolean i : v) { }
              }
            }
          }
        }
        """
      ),
      arg("""
        import java.lang.Boolean;
        import java.util.ArrayList;
        import java.util.List;
        component ValidComp3 {
          List<Boolean> v = ArrayList();
          automaton {
            initial state S {
              -> / {
                for (Boolean i : v) { }
              }
            }
          }
        }
        """
      ),
      arg("""
        import java.lang.Boolean;
        import java.util.HashSet;
        component ValidComp4 {
          HashSet<Boolean> v = HashSet();
          automaton {
            initial state S {
              -> / {
                for (Boolean i : v) { }
              }
            }
          }
        }
        """
      ),
      arg("""
        import java.lang.Boolean;
        import java.util.ArrayList;
        component ValidComp5 {
          ArrayList<Boolean> v = ArrayList();
          automaton {
            initial state S {
              -> / {
                for (Boolean i : v) { }
              }
            }
          }
        }
        """
      ),
      arg("""
        import java.lang.Integer;
        import java.lang.Number;
        import java.util.Collection;
        import java.util.Collections;
        component ValidComp6 {
          Collection<Integer> v = Collections.emptyList();
          automaton {
            initial state S {
              -> / {
                for (Number i : v) { }
              }
            }
          }
        }
        """
      )
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // for-each with expression of non-iterable, non-generic type
      arg("""
        import java.lang.Boolean;
        component InvalidComp1 {
          Boolean v = true;
          automaton {
            initial state S {
              -> / {
                for (Boolean i : v) { }
              }
            }
          }
        }
        """, MCError.FOR_EACH_EXPR_NOT_ITERABLE
      ),
      // for-each with expression of non-iterable, generic type
      arg("""
        import java.lang.Boolean;
        import java.util.Optional;
        component InvalidComp2 {
          Optional<Boolean> v = Optional.of(true);
          automaton {
            initial state S {
              -> / {
                for (Boolean i : v) { }
              }
            }
          }
        }
        """, MCError.FOR_EACH_EXPR_NOT_ITERABLE
      ),
      // for-each with mismatching types
      arg("""
        import java.lang.Boolean;
        import java.lang.Integer;
        import java.util.Collection;
        import java.util.Collections;
        component InvalidComp3 {
          Collection<Boolean> v = Collections.emptyList();
          automaton {
            initial state S {
              -> / {
                for (Integer i : v) { }
              }
            }
          }
        }
        """, MCError.FOR_EACH_TYPE_MISMATCH
      ),
      // for-each with mismatching super- and subtype
      arg("""
        import java.lang.Integer;
        import java.lang.Number;
        import java.util.Collection;
        import java.util.Collections;
        component InvalidComp4 {
          Collection<Number> v = Collections.emptyList();
          automaton {
            initial state S {
              -> / {
                for (Integer i : v) { }
              }
            }
          }
        }
        """, MCError.FOR_EACH_TYPE_MISMATCH
      )
    );
  }
}
