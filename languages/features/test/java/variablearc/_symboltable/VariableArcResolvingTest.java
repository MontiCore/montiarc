/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;

import java.util.List;
import java.util.Optional;

public class VariableArcResolvingTest extends AbstractTest {

  protected IVariableArcScope scope;

  @BeforeEach
  public void SetUpScope() {
    this.scope = VariableArcMill.scope();
  }

  /**
   * @return the test subject
   */
  public IVariableArcScope getScope() {
    return this.scope;
  }

  @Test
  public void shouldResolveArcFeature2Variable() {
    // Given
    ArcFeatureSymbol symbol = VariableArcMill.arcFeatureSymbolBuilder()
      .setName("f1").build();
    this.getScope().add(symbol);

    // When
    List<VariableSymbol> variables = this.getScope().resolveVariableMany("f1");

    // Then
    Assertions.assertEquals(1, variables.size());
    Assertions.assertTrue(variables.get(0) instanceof ArcFeature2VariableAdapter);
    Assertions.assertEquals(symbol, ((ArcFeature2VariableAdapter) variables.get(0)).getAdaptee());
  }

  @Test
  public void shouldCorrectlyOverrideParentComponent() {
    // Given
    IVariableArcScope scope = VariableArcMill.scope();
    IVariableArcScope parentScope = VariableArcMill.scope();

    ComponentTypeSymbol parent =
      VariableArcMill.componentTypeSymbolBuilder().setName("Parent")
        .setSpannedScope(parentScope).build();
    ComponentTypeSymbol child = VariableArcMill.componentTypeSymbolBuilder()
      .setName("Child").setSpannedScope(scope)
      .setParent(new TypeExprOfComponent(parent)).build();

    ArcFeatureSymbol parentFeature = VariableArcMill.arcFeatureSymbolBuilder()
      .setName("f1").build();
    parentScope.add(parentFeature);

    ArcFeatureSymbol feature = VariableArcMill.arcFeatureSymbolBuilder()
      .setName("f1").build();
    scope.add(feature);

    // When
    Optional<ArcFeatureSymbol> resolvedFeature = scope.resolveArcFeature("f1");

    // Then
    Assertions.assertTrue(resolvedFeature.isPresent());
    Assertions.assertEquals(feature, resolvedFeature.get());
  }

  @Test
  public void shouldNotResolveInEnclosingScopeForComponents() {
    // Given
    IVariableArcScope enclosingScope = VariableArcMill.scope();
    IVariableArcScope scope = VariableArcMill.scope();
    enclosingScope.addSubScope(scope);

    ComponentTypeSymbol parent =
      VariableArcMill.componentTypeSymbolBuilder().setName("Parent")
        .setSpannedScope(ArcBasisMill.scope()).build();
    ComponentTypeSymbol child = VariableArcMill.componentTypeSymbolBuilder()
      .setName("Child").setSpannedScope(scope)
      .setParent(new TypeExprOfComponent(parent)).build();

    ArcFeatureSymbol feature = VariableArcMill.arcFeatureSymbolBuilder()
      .setName("f1").build();
    enclosingScope.add(feature);

    // When
    Optional<ArcFeatureSymbol> resolvedFeature = scope.resolveArcFeature("f1");

    // Then
    Assertions.assertTrue(resolvedFeature.isEmpty());
  }

  @Test
  public void shouldResolveInEnclosingScope() {
    // Given
    IVariableArcScope enclosingScope = VariableArcMill.scope();
    IVariableArcScope scope = VariableArcMill.scope();
    enclosingScope.addSubScope(scope);

    ArcFeatureSymbol feature = VariableArcMill.arcFeatureSymbolBuilder()
      .setName("f1").build();
    enclosingScope.add(feature);

    // When
    Optional<ArcFeatureSymbol> resolvedFeature = scope.resolveArcFeature("f1");

    // Then
    Assertions.assertAll(
      () -> Assertions.assertFalse(resolvedFeature.isEmpty(),
        "Failed to resolve the feature symbol.")
    );
    Assertions.assertAll(
      () -> Assertions.assertEquals(feature, resolvedFeature.get())
    );
  }
}
