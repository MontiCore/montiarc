/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis.ArcBasisMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.CompKindOfComponentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcMill;
import variablearc.VariableArcTestBase;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class VariableArcResolvingTest extends VariableArcTestBase {

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
    Assertions.assertInstanceOf(ArcFeature2VariableAdapter.class, variables.get(0));
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
      .setSuperComponentsList(Collections.singletonList(new CompKindOfComponentType(parent))).build();

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
      .setSuperComponentsList(Collections.singletonList(new CompKindOfComponentType(parent))).build();

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
    Assertions.assertFalse(resolvedFeature.isEmpty(),
        "Failed to resolve the feature symbol.");
    Assertions.assertEquals(feature, resolvedFeature.get());
  }
}
