/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.NoPortInDefaultParameterValue4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoPortInDefaultParameterValue4Family}
 */
class NoPortInDefaultParameterValue4FamilyTest extends NoPortInDefaultParameterValueTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInDefaultParameterValue4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // port of inner component in its default parameter value, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          component Inner(int p = i) {
            varif (false) {
              port in int i;
            }
          }
        }
        """
      ),
      // port of inner component in its default parameter value, dead feature
      arg("""
        component ValidCompWithVariability2 {
          component Inner(int p = i) {
            feature f;
            varif (f) {
              port in int i;
            }
            constraint (!f);
          }
        }
        """
      ),
      // conditional port of inner component in its default parameter value
      arg("""
        component ValidCompWithVariability3 {
          component Inner(int p = iv) {
            feature f;
            varif (f) {
              port in int iv;
            } else {
              int iv = 0;
            }
            constraint (!f);
          }
        }
        """
      ),
      // conditional port in default parameter value
      arg("""
        component ValidCompWithVariability4(int p = iv) {
          feature f;
          varif (f) {
            port in int iv;
          } else {
            int iv = 0;
          }
          constraint (!f);
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // port of inner component in its default parameter value, tautological variation point
      arg("""
        component InvalidCompWithVariability1 {
          component Inner(int p = i) {
            varif (true) {
              port in int i;
            }
          }
        }
        """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port of inner component in its default parameter value, core feature
      arg("""
        component InvalidCompWithVariability2 {
          component Inner(int p = i) {
            feature f;
            varif (f) {
              port in int i;
            }
            constraint (f);
          }
        }
        """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // conditional port of inner component in its default parameter value
      arg("""
        component InvalidCompWithVariability3 {
          component Inner(int p = iv) {
            feature f;
            varif (f) {
              port in int iv;
            } else {
              int iv = 0;
            }
          }
        }
        """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // conditional port in default parameter value
      arg("""
        component InvalidCompWithVariability4(int p = iv) {
          feature f;
          varif (f) {
            port in int iv;
          } else {
            int iv = 0;
          }
        }
        """,
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
