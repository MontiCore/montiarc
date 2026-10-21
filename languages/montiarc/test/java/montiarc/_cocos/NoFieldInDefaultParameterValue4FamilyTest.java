/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.NoFieldInDefaultParameterValue4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoFieldInDefaultParameterValue4Family}.
 */
class NoFieldInDefaultParameterValue4FamilyTest extends NoFieldInDefaultParameterValueTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoFieldInDefaultParameterValue4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    ASTMACompilationUnit ast = compile(model);

    checker().checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    ASTMACompilationUnit ast = compile(model);

    checker().checkAll(ast);

    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // field in default parameter value, dead variation point
      arg("""
        component ValidCompWithVariability1(int p = x) {
          varif (false) {
            int x = 1;
          }
        }
        """
      ),
      // field in default parameter value, dead feature
      arg("""
        component ValidCompWithVariability2(int p = x) {
          feature f;
          varif (f) {
            int x = 1;
          }
          constraint (!f);
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // field in default parameter value, tautological variation point
      arg("""
        component InvalidCompWithVariability1(int p = x) {
          varif (true) {
            int x = 1;
          }
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // field in default parameter value, optional feature
      arg("""
        component InvalidCompWithVariability2(int p = x) {
          feature f;
          varif (f) {
            int x = 1;
          }
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      )
    );
  }

  @Test
  void shouldReportErrorIfConstraintsDependOnUnknownValues() {
    // Given
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    ASTMACompilationUnit ast = compile("""
      component UnknownComp1(int n, int p = x) {
        int x = 1;
        constraint (java.lang.Math.abs(n) > 3);
      }""");
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).singleElement()
      .matches(Finding::isError)
      .extracting(Finding::getMsg).asString()
      .startsWith(FIELD_REF_IN_STATIC_CONTEXT.getErrorCode());
  }
}
