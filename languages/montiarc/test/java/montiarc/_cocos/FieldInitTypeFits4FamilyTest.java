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
import variablearc._cocos.FieldInitTypeFits4Family;
import variablearc.check.TypeCheck4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.FIELD_INIT_TYPE_MISMATCH;
import static montiarc.util.MCError.CANT_FIND_SYMBOL_IN_EXPRESSION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link FieldInitTypeFits4Family}.
 */
class FieldInitTypeFits4FamilyTest extends FieldInitTypeFitsTest {

  protected MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FieldInitTypeFits4Family(new TypeCheck4Family(withinComponentResolver,
      new PresenceResolver(withinComponentResolver))));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModelsWithVariability")
  void shouldNotReportErrorWithVariability(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    this.checker().checkAll(ast);

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
    this.checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  protected static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // field initialized from fields of the same type in exclusive variation points
      arg("""
        component ValidCompWithVariability1 {
          feature f;
          varif (f) { int a = 1; } else { int a = 2; }
          int b = a;
        }"""),
      // fields initialized from same-named fields of different types in their variation points
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          varif (f) { int a = 1; int b = a; } else { boolean a = true; boolean b = a; }
        }""")
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // field initialized from a field missing in some configurations
      arg("""
        component InvalidCompWithVariability1 {
          feature f;
          varif (f) { int a = 1; }
          int b = a;
        }""", CANT_FIND_SYMBOL_IN_EXPRESSION),
      // field initialized from a field of a different type in some configurations
      arg("""
        component InvalidCompWithVariability2 {
          feature f;
          varif (f) { int a = 1; } else { boolean a = true; }
          int b = a;
        }""", FIELD_INIT_TYPE_MISMATCH)
    );
  }
}
