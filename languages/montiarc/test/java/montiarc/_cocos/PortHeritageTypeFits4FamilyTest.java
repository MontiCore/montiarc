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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.PortHeritageTypeFits4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH;
import static montiarc.util.ArcError.HERITAGE_PORT_DIRECTION_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortHeritageTypeFits4Family}.
 */
class PortHeritageTypeFits4FamilyTest extends PortHeritageTypeFitsTest {

  @BeforeEach
  protected void setUpComponentsWithVariability() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    compile("package a.b; component V { feature f; varif (f) { port in int i; } else { port in boolean i; } }");
    compile("package a.b; component W { feature f; varif (f) { port in int i; } }");
  }

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortHeritageTypeFits4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
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
    ASTMACompilationUnit ast = compile("""
      component UnknownComp(int n) extends a.b.B {
        varif (java.lang.Math.abs(n) > 3) {
          port in double i;
        }
      }""");
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).singleElement()
      .matches(Finding::isWarning)
      .extracting(Finding::getMsg).asString()
      .startsWith(HERITAGE_IN_PORT_TYPE_MISMATCH.getErrorCode());
  }

  protected static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // overriding an inherited port with the type of the variant of the super component
      arg("""
        component ValidCompWithVariability1 extends a.b.V {
          varif (f) {
            port in int i;
          }
        }"""
      ),
      // incompatible override excluded by a constraint
      arg("""
        component ValidCompWithVariability2 extends a.b.B {
          feature g;
          varif (g) {
            port in double i;
          }
          constraint (!g);
        }"""
      ),
      // incompatible override only in variants without the inherited port
      arg("""
        component ValidCompWithVariability3 extends a.b.W {
          varif (!f) {
            port in boolean i;
          }
        }"""
      ),
      // compatible overrides in mutually exclusive variation points
      arg("""
        component ValidCompWithVariability4 extends a.b.B {
          feature g;
          varif (g) {
            port in byte i;
          } else {
            port in short i;
          }
        }"""
      )
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // overriding an inherited port that has another type in some variant
      arg("""
        component InvalidCompWithVariability1 extends a.b.V {
          port in int i;
        }""",
        HERITAGE_IN_PORT_TYPE_MISMATCH
      ),
      // optional incompatible override
      arg("""
        component InvalidCompWithVariability2 extends a.b.B {
          feature g;
          varif (g) {
            port in double i;
          }
        }""",
        HERITAGE_IN_PORT_TYPE_MISMATCH
      ),
      // overriding an optional inherited port with another direction
      arg("""
        component InvalidCompWithVariability3 extends a.b.W {
          varif (f) {
            port out int i;
          }
        }""",
        HERITAGE_PORT_DIRECTION_MISMATCH
      )
    );
  }
}
