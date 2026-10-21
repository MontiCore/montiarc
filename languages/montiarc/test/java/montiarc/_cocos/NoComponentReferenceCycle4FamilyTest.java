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
import variablearc._cocos.NoComponentReferenceCycle4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.COMPONENT_REFERENCE_CYCLE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoComponentReferenceCycle4Family}.
 */
class NoComponentReferenceCycle4FamilyTest extends NoComponentReferenceCycleTest {

  protected static MontiArcCoCoChecker familyChecker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoComponentReferenceCycle4Family(new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModelsWithVariability")
  void shouldNotReportErrorWithVariability(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    familyChecker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource({"invalidModels", "invalidModelsWithVariability"})
  void shouldReportErrorWithVariability(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    familyChecker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  protected static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // self reference in a dead variation point
      arg("component ValidCompWithVariability1 { varif (false) { ValidCompWithVariability1 a; } }"),
      // reference back to the enclosing type in a variation point the inner type excludes
      arg("""
        component ValidCompWithVariability2 {
          component Inner {
            feature f;
            varif (f) {
              ValidCompWithVariability2 back;
            }
            constraint (!f);
          }
          Inner inner;
        }"""),
      // reference back to the enclosing type in a variation point the enclosing type excludes
      arg("""
        component ValidCompWithVariability3 {
          component Inner {
            feature f;
            varif (f) {
              ValidCompWithVariability3 back;
            }
          }
          varif (false) {
            Inner inner;
          }
        }""")
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // self reference in a variation point
      arg("component InvalidCompWithVariability1 { feature f; varif (f) { InvalidCompWithVariability1 a; } }",
        COMPONENT_REFERENCE_CYCLE),
      // reference back to the enclosing type in a variation point of the inner type
      arg("""
        component InvalidCompWithVariability2 {
          component Inner {
            feature f;
            varif (f) {
              InvalidCompWithVariability2 back;
            }
          }
          Inner inner;
        }""",
        COMPONENT_REFERENCE_CYCLE,
        COMPONENT_REFERENCE_CYCLE)
    );
  }
}
