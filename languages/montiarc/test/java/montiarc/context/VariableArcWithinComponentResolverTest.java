/* (c) https://github.com/MontiCore/monticore */
package montiarc.context;

import arcbasis.context.WithinComponentResolver;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import montiarc.MontiArcTestBase;
import org.junit.jupiter.api.Test;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc.context.VariableArcWithinComponentResolver;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VariableArcWithinComponentResolver}.
 */
public class VariableArcWithinComponentResolverTest extends MontiArcTestBase {

  protected final WithinComponentResolver resolver = new VariableArcWithinComponentResolver();

  @Test
  public void shouldExcludeFeatureAdapters() {
    // Given
    ComponentTypeSymbol c = compile("package a; component C { feature F; boolean p = true; }").getArcComponentType().getSymbol();
    IVariableArcScope scope = (IVariableArcScope) c.getSpannedScope();
    scope.resolveVariableMany("F");

    // When && Then
    assertThat(resolver.getAll(c, VariableSymbol.class)).isEqualTo(List.of(scope.resolveVariableLocally("p").orElseThrow()));
    assertThat(resolver.resolveMany(c, "F", ArcFeatureSymbol.class)).isEqualTo(List.of(scope.resolveArcFeatureLocally("F").orElseThrow()));
  }
}
