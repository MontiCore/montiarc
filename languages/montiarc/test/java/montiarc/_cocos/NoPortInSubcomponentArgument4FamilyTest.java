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
import variablearc._cocos.NoPortInSubcomponentArgument4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoPortInSubcomponentArgument4Family}.
 */
class NoPortInSubcomponentArgument4FamilyTest extends NoPortInSubcomponentArgumentTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInSubcomponentArgument4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
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
      // port in subcomponent argument, dead variation point
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidCompWithVariability1 {
          port in int i;
          varif (false) {
            ComponentTypeWithIntParameter sub(i);
          }
        }
        """
      ),
      // port in subcomponent argument, dead feature
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidCompWithVariability2 {
          feature f;
          port in int i;
          varif (f) {
            ComponentTypeWithIntParameter sub(i);
          }
          constraint (!f);
        }
        """
      ),
      // conditional port in subcomponent argument
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidCompWithVariability3 {
          feature f1;
          feature f2;
          varif (f1) {
            port in int iv;
          } else {
            int iv = 0;
          }
          varif (f2) {
            ComponentTypeWithIntParameter sub(iv);
          }
          constraint (!(f1 && f2));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // port in subcomponent argument, tautological variation point
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidCompWithVariability1 {
            port in int i;
            varif (true) {
              ComponentTypeWithIntParameter sub(i);
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in subcomponent argument, core feature
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidCompWithVariability2 {
            feature f;
            port in int i;
            varif (f) {
              ComponentTypeWithIntParameter sub(i);
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // conditional port in subcomponent argument
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidCompWithVariability3 {
            feature f1;
            feature f2;
            varif (f1) {
              port in int iv;
            } else {
              int iv = 0;
            }
            varif (f2) {
              ComponentTypeWithIntParameter sub(iv);
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
