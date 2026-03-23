/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.util.stream.Stream;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoPortInSuperComponentArgument4Family}.
 */
class NoPortInSuperComponentArgument4FamilyTest extends NoPortInSuperComponentArgumentTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInSuperComponentArgument4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidCompWithVariability1 ",
    "InvalidCompWithVariability2 ",
    "InvalidCompWithVariability3 "
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInSuperComponentArgument4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // port in super component argument of inner component, dead variation point
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidCompWithVariability1 {
          port in int i;
          varif (false) {
            component Inner extends ComponentTypeWithIntParameter sub(i) { }
          }
        }
        """
      ),
      // port in super component argument of inner component, dead feature
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidCompWithVariability2 {
          feature f;
          port in int i;
          varif (f) {
            component Inner extends ComponentTypeWithIntParameter sub(i) { }
          }
          constraint (!f);
        }
        """
      ),
      // conditional port in super component argument of inner component
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
            component Inner extends ComponentTypeWithIntParameter sub(iv) { }
          }
          constraint (!(f1 && f2));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // port in super component argument of inner component, tautological variation point
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidCompWithVariability1 {
            port in int i;
            varif (true) {
              component Inner extends ComponentTypeWithIntParameter sub(i) { }
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // port in super component argument of inner component, core feature
      arg("""
          import montiarc.test.ComponentTypeWithIntParameter;
          component InvalidCompWithVariability2 {
            feature f;
            port in int i;
            varif (f) {
              component Inner extends ComponentTypeWithIntParameter sub(i) { }
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // conditional port in super component argument of inner component
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
              component Inner extends ComponentTypeWithIntParameter sub(iv) { }
            }
          }
          """,
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
