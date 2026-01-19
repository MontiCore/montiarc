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
import variablearc._cocos.CircularInheritance4Family;

import java.util.stream.Stream;

import static montiarc.util.ArcError.CIRCULAR_INHERITANCE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link CircularInheritance4Family}.
 */
class CircularInheritance4FamilyTest extends CircularInheritanceTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidComp2",
    "ValidComp3",
    "ValidComp4",
    "ValidComp5",
    "ValidComp7",
    "ValidComp8",
    "ValidComp9",
    "ValidComp10",
    "ValidCompWithVariability3"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new CircularInheritance4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidComp1",
    "InvalidComp2",
    "InvalidComp6",
    "InvalidCompWithVariability2"
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new CircularInheritance4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      arg("""
        component ValidCompWithVariability1 {
          feature f1;
          varif(f1) {
            component Inner2 extends Inner1 { }
          }
          component Inner1 { }
        }
        """
      ),
      arg("""
        component ValidCompWithVariability2 {
          feature f1;
          varif(f1) {
            component Inner1 extends Inner2 { }
            component Inner2 extends Inner1 { }
          }
          constraint(!f1);
        }
        """
      ),
      arg("""
        component ValidCompWithVariability3 {
          feature f1, f2;
          varif(f1) {
            component Inner2 extends Inner1 { }
          } else {
            component Inner2 { }
          }
          varif(f2) {
            component Inner1 extends Inner2 { }
          } else {
            component Inner1 { }
          }
          constraint(!(f1 && f2));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // circular inheritance of inner components (configuration f1)
      arg("""
        component InvalidCompWithVariability1 {
          feature f1;
          varif(f1) {
            component Inner1 extends Inner2 { }
            component Inner2 extends Inner1 { }
          }
        }
        """,
        CIRCULAR_INHERITANCE, CIRCULAR_INHERITANCE
      ),
      // circular inheritance of inner components (configuration f1 && f2)
      arg("""
        component InvalidCompWithVariability2 {
          feature f1, f2;
          varif(f1) {
            component Inner2 extends Inner1 { }
          } else {
            component Inner2 { }
          }
          varif(f2) {
            component Inner1 extends Inner2 { }
          } else {
            component Inner1 { }
          }
        }
        """,
        CIRCULAR_INHERITANCE, CIRCULAR_INHERITANCE
      )
    );
  }
}
