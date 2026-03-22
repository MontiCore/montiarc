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
import variablearc._cocos.PortUniqueSender4Family;

import java.util.stream.Stream;

import static montiarc.util.ArcError.PORT_MULTIPLE_SENDER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortUniqueSender4Family}.
 */
class PortUniqueSender4FamilyTest extends PortUniqueSenderTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortUniqueSender4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidCompWithVariability1",
    "InvalidCompWithVariability2",
    "InvalidCompWithVariability3",
    "InvalidCompWithVariability4",
    "InvalidCompWithVariability5",
    "InvalidCompWithVariability6"
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortUniqueSender4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // redundant input forward, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          port in int i;
          feature f;
          a.b.A sub;
          varif (false) {
            i -> sub.i;
            i -> sub.i;
          }
        }
        """
      ),
      // redundant output forward, dead variation point
      arg("""
        component ValidCompWithVariability2 {
          port out int o;
          feature f;
          a.b.B sub;
          varif (false) {
            sub.o -> o;
            sub.o -> o;
          }
        }
        """
      ),
      // redundant input forward, dead feature
      arg("""
        component ValidCompWithVariability3 {
          port in int i;
          feature f;
          a.b.A sub;
          varif (f) {
            i -> sub.i;
            i -> sub.i;
          }
          constraint (!f);
        }
        """
      ),
      // redundant output forward, dead feature
      arg("""
        component ValidCompWithVariability4 {
          port out int o;
          feature f;
          a.b.B sub;
          varif (f) {
            sub.o -> o;
            sub.o -> o;
          }
          constraint (!f);
        }
        """
      ),
      // conditional input forward
      arg("""
        component ValidCompWithVariability5 {
          port in int i;
          feature f1;
          feature f2;
          a.b.A sub;
          varif (f1) {
            i -> sub.i;
          }
          varif (f2) {
            i -> sub.i;
          }
          constraint (f1 ^ f2);
        }
        """
      ),
      // conditional output forward
      arg("""
        component ValidCompWithVariability6 {
          port out int o;
          feature f1;
          feature f2;
          a.b.B sub;
          varif (f1) {
            sub.o -> o;
          }
          varif (f2) {
            sub.o -> o;
          }
          constraint (f1 ^ f2);
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // redundant input forward, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            port in int i;
            feature f;
            a.b.A sub;
            varif (false) {
              i -> sub.i;
              i -> sub.i;
            }
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // redundant output forward, tautological variation point
      arg("""
          component InvalidCompWithVariability2 {
            port out int o;
            feature f;
            a.b.B sub;
            varif (false) {
              sub.o -> o;
              sub.o -> o;
            }
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // redundant input forward, core feature
      arg("""
          component InvalidCompWithVariability3 {
            port in int i;
            feature f;
            a.b.A sub;
            varif (f) {
              i -> sub.i;
              i -> sub.i;
            }
            constraint (!f);
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // redundant output forward, core feature
      arg("""
          component InvalidCompWithVariability4 {
            port out int o;
            feature f;
            a.b.B sub;
            varif (f) {
              sub.o -> o;
              sub.o -> o;
            }
            constraint (!f);
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // conditional redundant input forward
      arg("""
          component InvalidCompWithVariability5 {
            port in int i;
            feature f1;
            feature f2;
            a.b.A sub;
            varif (f1) {
              i -> sub.i;
            }
            varif (f2) {
              i -> sub.i;
            }
            constraint (f1 || f2);
          }
          """,
        PORT_MULTIPLE_SENDER
      ),
      // conditional redundant output forward
      arg("""
          component InvalidCompWithVariability6 {
            port out int o;
            feature f1;
            feature f2;
            a.b.B sub;
            varif (f1) {
              sub.o -> o;
            }
            varif (f2) {
              sub.o -> o;
            }
            constraint (f1 || f2);
          }
          """,
        PORT_MULTIPLE_SENDER
      )
    );
  }
}
