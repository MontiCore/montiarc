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
import variablearc._cocos.VarDeclarationNameAlreadyDefinedInScope4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.MCError.DUPLICATE_VAR_IN_SCOPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VarDeclarationNameAlreadyDefinedInScope4Family}.
 */
class VarDeclarationNameAlreadyDefinedInScope4FamilyTest extends VarDeclarationNameAlreadyDefinedInScopeTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope4Family(withinComponentResolver,
      new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @Override
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
  @Override
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  @Test
  void shouldWarnIfPresenceDependsOnUnknownValues() {
    // Given
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    ASTMACompilationUnit ast = compile("""
      component UnknownComp(int n) {
        varif (java.lang.Math.abs(n) > 3) {
          int i = 1;
        }
        compute {
          int i = 1;
        }
      }""");
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).singleElement()
      .matches(Finding::isWarning)
      .extracting(Finding::getMsg).asString()
      .startsWith(DUPLICATE_VAR_IN_SCOPE.getErrorCode());
  }

  protected static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // field and local variable in mutually exclusive variation points
      arg("""
        component ValidCompWithVariability1 {
          feature f;
          varif (f) {
            int i = 1;
          } else {
            compute {
              int i = 1;
            }
          }
        }"""
      ),
      // field shadowed by a local variable only in a variant excluded by a constraint
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          varif (f) {
            int i = 1;
          }
          compute {
            int i = 1;
          }
          constraint (!f);
        }"""
      )
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // field of an optional variation point shadowed by a local variable
      arg("""
        component InvalidCompWithVariability1 {
          feature f;
          varif (f) {
            int i = 1;
          }
          compute {
            int i = 1;
          }
        }""",
        DUPLICATE_VAR_IN_SCOPE
      ),
      // field shadowed by a local variable in the same variation point
      arg("""
        component InvalidCompWithVariability2 {
          feature f;
          varif (f) {
            int i = 1;
            compute {
              int i = 1;
            }
          }
        }""",
        DUPLICATE_VAR_IN_SCOPE
      )
    );
  }
}
