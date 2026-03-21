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
import variablearc._cocos.UniqueIdentifier4Family;

import java.util.stream.Stream;

import static montiarc.util.ArcError.UNIQUE_IDENTIFIER_NAMES;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link UniqueIdentifier4Family}
 */
class UniqueIdentifier4FamilyTest extends UniqueIdentifierTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UniqueIdentifier4Family());

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
    "InvalidCompWithVariability6",
    "InvalidCompWithVariability7",
    "InvalidCompWithVariability8"
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UniqueIdentifier4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // ports with the same name in different variants
      arg("""
        component ValidCompWithVariability1 {
          feature f1;
          feature f2;
          varif (f1) {
            port in int i;
          }
          varif (f2) {
            port in int i;
          }
          constraint (!(f1 && f2));
        }
        """
      ),
      // variables with the same name in different variants
      arg("""
        component ValidCompWithVariability2 {
          feature f1;
          feature f2;
          varif (f1) {
            int v = 0;
          }
          varif (f2) {
            int v = 0;
          }
          constraint (!(f1 && f2));
        }
        """
      ),
      // inner components with the same name in different variants
      arg("""
        component ValidCompWithVariability3 {
          feature f1;
          feature f2;
          varif (f1) {
            component Inner { }
          }
          varif (f2) {
            component Inner { }
          }
          constraint (!(f1 && f2));
        }
        """
      ),
      // subcomponents with the same name in different variants
      arg("""
        component ValidCompWithVariability4 {
          feature f1;
          feature f2;
          varif (f1) {
            CompType sub;
          }
          varif (f2) {
            CompType sub;
          }
          constraint (!(f1 && f2));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // duplicate ports, same variation point
      arg("""
          component InvalidCompWithVariability5 {
            feature f;
            varif (f) {
              port in int i;
              port in int i;
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate component fields, same variation point
      arg("""
          component InvalidCompWithVariability2 {
            feature f;
            varif (f) {
              int v = 0;
              int v = 0;
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate inner components, same variation point
      arg("""
          component InvalidCompWithVariability3 {
            feature f;
            varif (f) {
              component Inner { }
              component Inner { }
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate subcomponents, same variation point
      arg("""
          component InvalidCompWithVariability4 {
            feature f;
            varif (f) {
              CompType sub;
              CompType sub;
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate ports, different variation points
      arg("""
          component InvalidCompWithVariability5 {
            feature f1;
            feature f2;
            varif (f1) {
              port in int i;
            }
            varif (f2) {
              port in int i;
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate variables, different variation points
      arg("""
          component InvalidCompWithVariability6 {
            feature f1;
            feature f2;
            varif (f1) {
              int v = 0;
            }
            varif (f2) {
              int v = 0;
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate inner components, different variation points
      arg("""
          component InvalidCompWithVariability7 {
            feature f1;
            feature f2;
            varif (f1) {
              component Inner { }
            }
            varif (f2) {
              component Inner { }
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate subcomponents, different variation points
      arg("""
          component InvalidCompWithVariability8 {
            feature f1;
            feature f2;
            varif (f1) {
              CompType sub;
            }
            varif (f2) {
              CompType sub;
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      )
    );
  }
}
