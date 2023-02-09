/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;

import java.util.Collections;
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
  public void shouldAddVariationPoint() {
    int variationPointSize = this.getScope().getRootVariationPoints().size();

    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    this.getScope().add(variationPoint);

    Assertions.assertEquals(variationPointSize + 1, this.getScope()
      .getRootVariationPoints().size());
    Assertions.assertTrue(this.getScope().getRootVariationPoints()
      .contains(variationPoint));
  }

  @Test
  public void shouldContainSymbol() {
    VariableArcVariationPoint parent = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    VariableArcVariationPoint child = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class),
      Optional.of(parent));
    this.getScope().add(parent);

    ISymbol symbol = Mockito.mock(ISymbol.class);
    parent.add(symbol);

    Assertions.assertTrue(this.getScope().getRootVariationPoints()
      .contains(parent));
    Assertions.assertFalse(this.getScope().getRootVariationPoints()
      .contains(child));
    Assertions.assertTrue(this.getScope()
      .variationPointsContainsSymbol(Collections.singletonList(parent), symbol));
    Assertions.assertTrue(this.getScope()
      .variationPointsContainsSymbol(Collections.singletonList(child), symbol));
  }

  @Test
  public void shouldResolvePortInConfiguration() {
    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    this.getScope().add(variationPoint);

    PortSymbol symbol =
      VariableArcMill.portSymbolBuilder().setName("Test")
        .setType(Mockito.mock(SymTypeExpression.class)).build();
    this.getScope().add(symbol);
    variationPoint.add(symbol);

    Assertions.assertEquals(symbol,
      this.getScope()
        .resolvePort("Test", Collections.singletonList(variationPoint)).get());
  }

  @Test
  public void shouldResolvePortInParentConfiguration() {
    VariableArcVariationPoint parent = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    VariableArcVariationPoint child = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class),
      Optional.of(parent));
    this.getScope().add(parent);

    PortSymbol symbol = VariableArcMill.portSymbolBuilder().setName("Test")
      .setType(Mockito.mock(SymTypeExpression.class)).build();
    this.getScope().add(symbol);
    parent.add(symbol);

    Assertions.assertEquals(symbol, this.getScope()
      .resolvePort("Test", Collections.singletonList(child)).get());
  }

  @Test
  public void shouldResolvePortInNoConfiguration() {
    VariableArcVariationPoint parent = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    VariableArcVariationPoint child = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class),
      Optional.of(parent));
    this.getScope().add(parent);

    PortSymbol symbol =
      VariableArcMill.portSymbolBuilder().setName("Test")
        .setType(Mockito.mock(SymTypeExpression.class))
        .build();
    this.getScope().add(symbol); // Add Symbol only to scope

    Assertions.assertEquals(symbol, this.getScope()
      .resolvePort("Test", Collections.singletonList(child)).get());
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
