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
import variablearc._cocos.NoPortInFieldDeclaration4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoPortInFieldDeclaration4Family}.
 */
class NoPortInFieldDeclaration4FamilyTest extends NoPortInFieldDeclarationTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInFieldDeclaration4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
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
      // port in component field declaration, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          port in int i;
          varif (false) {
            int v = i;
          }
        }
        """
      ),
      // port in component field declaration, dead feature
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          port in int i;
          varif (f) {
            int v = i;
          }
          constraint (!f);
        }
        """
      ),
      // conditional port in component field declaration
      arg("""
        component ValidCompWithVariability3 {
          feature f1;
          feature f2;
          varif (f1) {
            port in int iv;
          } else {
            int iv = 0;
          }
          varif (f2) {
            int v = iv;
          }
          constraint (!(f1 && f2));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // port in component field declaration, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            port in int i;
            varif (true) {
              int v = i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in component field declaration, core feature
      arg("""
          component InvalidCompWithVariability2 {
            feature f;
            port in int i;
            varif (f) {
              int v = i;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // conditional port in component field declaration
      arg("""
          component InvalidCompWithVariability3 {
            feature f1;
            feature f2;
            varif (f1) {
              port in int iv;
            } else {
              int iv = 0;
            }
            varif (f2) {
              int v = iv;
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
