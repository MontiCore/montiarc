/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ArcBasisResolvingTest extends ArcBasisAbstractTest {

  protected IArcBasisScope scope;

  protected static Stream<Arguments> validScopeAndTypeAndResolutionNameProvider() {
    return Stream.of(
      Arguments.of("", "A", "A"),
      Arguments.of("x", "A", "A"),
      Arguments.of("x", "A", "x.A"),
      Arguments.of("x", "A", "B.x.A")
    );
  }

  protected static Stream<Arguments> invalidScopeAndTypeAndResolutionNameProvider() {
    return Stream.of(
      Arguments.of("", "A", ""),
      Arguments.of("", "", "A"),
      Arguments.of("", "", "B"),
      Arguments.of("", "A", "B"),
      Arguments.of("", "x.A", "A"),
      Arguments.of("x", "A", "B.A"),
      Arguments.of("", "x.A", "x.A"),
      Arguments.of("y", "x.A", "x.A"),
      Arguments.of("y", "x.A", "y.x.A"),
      Arguments.of("y", "x.A", "B.y.x.A")
    );
  }

  protected IArcBasisScope getScope() {
    return this.scope;
  }

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    this.setUpType();
  }

  protected void setUpType() {
    this.scope = ArcBasisMill.scope();
    IArcBasisArtifactScope aScope = ArcBasisMill.artifactScope();
    aScope.setName("B");
    aScope.addSubScope(this.getScope());
    ArcBasisMill.globalScope().addSubScope(aScope);
  }

  @ParameterizedTest
  @MethodSource("validScopeAndTypeAndResolutionNameProvider")
  public void shouldResolveType(@NotNull String scopeName, @NotNull String typeName, @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.typeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<TypeSymbol> symbol = this.getScope().resolveType(resolutionName);

    //Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("invalidScopeAndTypeAndResolutionNameProvider")
  public void shouldNotResolveType(@NotNull String scopeName, @NotNull String typeName,
                                   @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.typeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<TypeSymbol> symbol = this.getScope().resolveType(resolutionName);

    //Then
    Assertions.assertFalse(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("validScopeAndTypeAndResolutionNameProvider")
  public void shouldResolveOOType(@NotNull String scopeName, @NotNull String typeName, @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.oOTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<OOTypeSymbol> symbol = this.getScope().resolveOOType(resolutionName);

    //Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("invalidScopeAndTypeAndResolutionNameProvider")
  public void shouldNotResolveOOType(@NotNull String scopeName, @NotNull String typeName,
                                     @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.oOTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<OOTypeSymbol> symbol = this.getScope().resolveOOType(resolutionName);

    //Then
    Assertions.assertFalse(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("validScopeAndTypeAndResolutionNameProvider")
  public void shouldResolveTypeFromOOType(@NotNull String scopeName, @NotNull String typeName, @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.oOTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<TypeSymbol> symbol = this.getScope().resolveType(resolutionName);

    //Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("validScopeAndTypeAndResolutionNameProvider")
  public void shouldResolveComponentType(@NotNull String scopeName, @NotNull String typeName,
                                         @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.componentTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<ComponentTypeSymbol> symbol = this.getScope().resolveComponentType(resolutionName);

    //Then
    Assertions.assertTrue(symbol.isPresent());
  }

  @ParameterizedTest
  @MethodSource("invalidScopeAndTypeAndResolutionNameProvider")
  public void shouldNotResolveComponentType(@NotNull String scopeName, @NotNull String typeName,
                                            @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(ArcBasisMill.componentTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(ArcBasisMill.scope()).build());

    //When
    Optional<ComponentTypeSymbol> symbol = this.getScope().resolveComponentType(resolutionName);

    //Then
    Assertions.assertFalse(symbol.isPresent());
  }

  @Test
  public void shouldNotResolveShadowedVariable() {
    // Given
    // Outer scope with variable var
    IArcBasisScope outer = ArcBasisMill.scope();
    outer.setName("Outer");
    outer.setShadowing(false);
    VariableSymbol var1 = ArcBasisMill.variableSymbolBuilder()
      .setName("var")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setEnclosingScope(outer)
      .build();
    outer.add(var1);
    // Inner scope with variable var
    IArcBasisScope inner = ArcBasisMill.scope();
    inner.setName("Inner");
    inner.setShadowing(true);
    VariableSymbol var2 = ArcBasisMill.variableSymbolBuilder()
      .setName("var")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setEnclosingScope(inner)
      .build();
    inner.add(var2);
    // Scope hierarchy
    inner.setEnclosingScope(outer);
    outer.addSubScope(inner);

    // When && Then
    Assertions.assertTrue(outer.resolveVariable("var").isPresent(),
      "Could not resolve the local variable 'var' from the outer scope.");
    Assertions.assertDoesNotThrow(() -> inner.resolveVariable("var"),
      "An exception was thrown while resolving the local variable 'var' from the inner scope.");
    Assertions.assertTrue(inner.resolveVariable("var").isPresent(),
      "Could not resolve the local variable 'var' from the inner scope.");
    Assertions.assertEquals(var1, outer.resolveVariable("var").get(),
      "Resolved the wrong variable symbol from the outer scope.");
    Assertions.assertEquals(var2, inner.resolveVariable("var").get(),
      "Resolve the wrong variable symbol from the inner scope.");
  }

  @Test
  public void shouldResolvePort2Variable1() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ArcPortSymbol port = ArcBasisMill.arcPortSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    SymbolService.link(scope, port);

    // When
    List<VariableSymbol> variables = scope.resolveVariableMany("port");

    // Then
    Assertions.assertFalse(variables.isEmpty(),
      "Failed to resolve the port to variable symbol adapter." );
    Assertions.assertEquals(1, variables.size(),
      "Failed to resolve only the single port to variable symbol adapter.");
  }

  @Test
  public void shouldResolvePort2Variable2() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ArcPortSymbol port = ArcBasisMill.arcPortSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    SymbolService.link(scope, port);

    VariableSymbol variable = ArcBasisMill.variableSymbolBuilder()
      .setName("variable")
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();
    SymbolService.link(scope, variable);

    // When
    List<VariableSymbol> variables = scope.resolveVariableMany("port");

    // Then
    Assertions.assertFalse(variables.isEmpty(),
      "Failed to resolve the port to variable symbol adapter." );
    Assertions.assertEquals(1, variables.size(),
      "Failed to resolve only the single port to variable symbol adapter.");
  }

  @Test
  @Disabled("see https://git.rwth-aachen.de/monticore/monticore/-/issues/3241")
  public void shouldResolvePort2Variable3() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ArcPortSymbol port = ArcBasisMill.arcPortSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    SymbolService.link(scope, port);

    VariableSymbol variable = ArcBasisMill.variableSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();
    SymbolService.link(scope, variable);

    // When
    List<VariableSymbol> variables = scope.resolveVariableMany("port");

    // Then
    Assertions.assertEquals(2, variables.size(),
      "Failed to resolve both the variable symbol and the port to variable symbol adapter.");
  }

  @Test
  public void shouldResolvePort2Variable4() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ArcPortSymbol port = ArcBasisMill.arcPortSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    SymbolService.link(scope, port);

    // When
    List<VariableSymbol> variables1 = scope.resolveVariableMany("port");
    List<VariableSymbol> variables2 = scope.resolveVariableMany("port");

    // Then
    Assertions.assertAll(
      () -> Assertions.assertFalse(variables1.isEmpty(),
        "The first call failed to resolve the port to variable symbol adapter."),
      () -> Assertions.assertFalse(variables2.isEmpty(),
        "The second call failed to resolve the port to variable symbol adapter.")
    );
    Assertions.assertAll(
      () -> Assertions.assertEquals(1, variables1.size(),
        "The first call failed to resolve only the single port to variable symbol adapter."),
      () -> Assertions.assertEquals(1, variables2.size(),
        "The second call failed to resolve only the single port to variable symbol adapter.")
    );
  }

  @Test
  @Disabled("see https://git.rwth-aachen.de/monticore/monticore/-/issues/3241")
  public void shouldResolvePort2Variable5() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    ArcPortSymbol port = ArcBasisMill.arcPortSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    SymbolService.link(scope, port);

    VariableSymbol variable = ArcBasisMill.variableSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setAccessModifier(BasicAccessModifier.PRIVATE)
      .build();
    SymbolService.link(scope, variable);

    // When
    List<VariableSymbol> variables1 = scope.resolveVariableMany("port", BasicAccessModifier.ALL_INCLUSION);
    List<VariableSymbol> variables2 = scope.resolveVariableMany("port", BasicAccessModifier.PUBLIC);
    List<VariableSymbol> variables3 = scope.resolveVariableMany("port", BasicAccessModifier.ALL_INCLUSION);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertFalse(variables1.isEmpty(),
        "The first call failed to resolve the variable symbols."),
      () -> Assertions.assertFalse(variables2.isEmpty(),
        "The second call failed to resolve the variable symbol."),
      () -> Assertions.assertFalse(variables3.isEmpty(),
        "The third call failed to resolve the variable symbols.")
    );
    Assertions.assertAll(
      () -> Assertions.assertEquals(2, variables1.size(),
        "The first call failed to resolve both the variable symbol and the port to variable symbol adapter."),
      () -> Assertions.assertEquals(1, variables2.size(),
        "The second call failed to resolve only the port to variable symbol adapter."),
      () -> Assertions.assertEquals(2, variables3.size(),
        "The third call failed to resolve both the variable symbol and the port to variable symbol adapter.")
    );
  }

  @Test
  public void shouldResolveInParentComponents() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    IArcBasisScope parent1Scope = ArcBasisMill.scope();
    IArcBasisScope parent2Scope = ArcBasisMill.scope();

    ComponentTypeSymbol parent1 =
        ArcBasisMill.componentTypeSymbolBuilder().setName("Parent").setSpannedScope(parent1Scope).build();
    ComponentTypeSymbol parent2 =
      ArcBasisMill.componentTypeSymbolBuilder().setName("Parent").setSpannedScope(parent2Scope).build();
    ComponentTypeSymbol child = ArcBasisMill.componentTypeSymbolBuilder().setName("Child").setSpannedScope(scope)
        .setSuperComponentsList(List.of(new TypeExprOfComponent(parent1), new TypeExprOfComponent(parent2))).build();

    ArcPortSymbol port1 = ArcBasisMill.arcPortSymbolBuilder().setName("p1")
        .setType(Mockito.mock(SymTypeExpression.class)).build();
    parent1Scope.add(port1);

    VariableSymbol variable1 = ArcBasisMill.variableSymbolBuilder().setName("var1").setType(Mockito.mock(
        SymTypeExpression.class)).build();
    parent1Scope.add(variable1);

    ArcPortSymbol port2 = ArcBasisMill.arcPortSymbolBuilder().setName("p2")
      .setType(Mockito.mock(SymTypeExpression.class)).build();
    parent1Scope.add(port2);

    VariableSymbol variable2 = ArcBasisMill.variableSymbolBuilder().setName("var2").setType(Mockito.mock(
      SymTypeExpression.class)).build();
    parent1Scope.add(variable2);

    // When
    Optional<ArcPortSymbol> resolvedPort1 = scope.resolveArcPort("p1");
    Optional<VariableSymbol> resolvedVariable1 = scope.resolveVariable("var1");
    Optional<ArcPortSymbol> resolvedPort2 = scope.resolveArcPort("p2");
    Optional<VariableSymbol> resolvedVariable2 = scope.resolveVariable("var2");

    // Then
    Assertions.assertTrue(resolvedPort1.isPresent());
    Assertions.assertEquals(port1, resolvedPort1.get());
    Assertions.assertTrue(resolvedVariable1.isPresent());
    Assertions.assertEquals(variable1, resolvedVariable1.get());
    Assertions.assertTrue(resolvedPort2.isPresent());
    Assertions.assertEquals(port2, resolvedPort2.get());
    Assertions.assertTrue(resolvedVariable2.isPresent());
    Assertions.assertEquals(variable2, resolvedVariable2.get());
  }

  @Test
  public void shouldCorrectlyOverrideParentComponent() {
    // Given
    IArcBasisScope scope = ArcBasisMill.scope();
    IArcBasisScope parentScope = ArcBasisMill.scope();

    ComponentTypeSymbol parent =
        ArcBasisMill.componentTypeSymbolBuilder().setName("Parent").setSpannedScope(parentScope).build();
    ComponentTypeSymbol child = ArcBasisMill.componentTypeSymbolBuilder().setName("Child").setSpannedScope(scope)
        .setSuperComponentsList(Collections.singletonList(new TypeExprOfComponent(parent))).build();

    ArcPortSymbol parentPort = ArcBasisMill.arcPortSymbolBuilder().setName("p1")
        .setType(Mockito.mock(SymTypeExpression.class)).build();
    parentScope.add(parentPort);

    ArcPortSymbol port = ArcBasisMill.arcPortSymbolBuilder().setName("p1")
        .setType(Mockito.mock(SymTypeExpression.class)).build();
    scope.add(port);

    VariableSymbol parentVariable = ArcBasisMill.variableSymbolBuilder().setName("var1").setType(Mockito.mock(
        SymTypeExpression.class)).build();
    parentScope.add(parentVariable);

    VariableSymbol variable = ArcBasisMill.variableSymbolBuilder().setName("var1").setType(Mockito.mock(
        SymTypeExpression.class)).build();
    scope.add(variable);

    // When
    Optional<ArcPortSymbol> resolvedPort = scope.resolveArcPort("p1");
    Optional<VariableSymbol> resolvedVariable = scope.resolveVariable("var1");

    // Then
    Assertions.assertTrue(resolvedPort.isPresent());
    Assertions.assertEquals(port, resolvedPort.get());
    Assertions.assertTrue(resolvedVariable.isPresent());
    Assertions.assertEquals(variable, resolvedVariable.get());
  }

  @Test
  public void shouldNotResolveInEnclosingScopeForComponents() {
    // Given
    IArcBasisScope enclosingScope = ArcBasisMill.scope();
    IArcBasisScope scope = ArcBasisMill.scope();
    enclosingScope.addSubScope(scope);

    ComponentTypeSymbol parent =
        ArcBasisMill.componentTypeSymbolBuilder().setName("Parent").setSpannedScope(ArcBasisMill.scope()).build();
    ComponentTypeSymbol child = ArcBasisMill.componentTypeSymbolBuilder().setName("Child").setSpannedScope(scope)
        .setSuperComponentsList(Collections.singletonList(new TypeExprOfComponent(parent))).build();

    ArcPortSymbol port = ArcBasisMill.arcPortSymbolBuilder().setName("p1")
        .setType(Mockito.mock(SymTypeExpression.class)).build();
    enclosingScope.add(port);

    SubcomponentSymbol instance =
        ArcBasisMill.subcomponentSymbolBuilder().setName("ins1").setType(Mockito.mock(
            CompTypeExpression.class)).build();
    enclosingScope.add(instance);

    VariableSymbol variable = ArcBasisMill.variableSymbolBuilder().setName("var1").setType(Mockito.mock(
        SymTypeExpression.class)).build();
    enclosingScope.add(variable);

    // When
    Optional<ArcPortSymbol> resolvedPort = scope.resolveArcPort("p1");
    Optional<SubcomponentSymbol> resolvedInstance = scope.resolveSubcomponent("ins1");
    Optional<VariableSymbol> resolvedVariable = scope.resolveVariable("var1");

    // Then
    Assertions.assertTrue(resolvedPort.isEmpty());
    Assertions.assertTrue(resolvedInstance.isEmpty());
    Assertions.assertTrue(resolvedVariable.isEmpty());
  }

  @Test
  public void shouldResolveInEnclosingScope() {
    // Given
    IArcBasisScope enclosingScope = ArcBasisMill.scope();
    IArcBasisScope scope = ArcBasisMill.scope();
    enclosingScope.addSubScope(scope);

    ArcPortSymbol port = ArcBasisMill.arcPortSymbolBuilder().setName("p1")
        .setType(Mockito.mock(SymTypeExpression.class)).build();
    enclosingScope.add(port);

    SubcomponentSymbol instance =
        ArcBasisMill.subcomponentSymbolBuilder().setName("ins1").setType(Mockito.mock(
            CompTypeExpression.class)).build();
    enclosingScope.add(instance);

    VariableSymbol variable = ArcBasisMill.variableSymbolBuilder().setName("var1").setType(Mockito.mock(
        SymTypeExpression.class)).build();
    enclosingScope.add(variable);

    // When
    Optional<ArcPortSymbol> resolvedPort = scope.resolveArcPort("p1");
    Optional<SubcomponentSymbol> resolvedInstance = scope.resolveSubcomponent("ins1");
    Optional<VariableSymbol> resolvedVariable = scope.resolveVariable("var1");

    // Then
    Assertions.assertAll(
        () -> Assertions.assertFalse(resolvedPort.isEmpty(),
            "Failed to resolve the port symbol."),
        () -> Assertions.assertFalse(resolvedInstance.isEmpty(),
            "Failed to resolve the component instance symbol."),
        () -> Assertions.assertFalse(resolvedVariable.isEmpty(),
            "Failed to resolve the variable symbol.")
    );
    Assertions.assertAll(
        () -> Assertions.assertEquals(port, resolvedPort.get()),
        () -> Assertions.assertEquals(instance, resolvedInstance.get()),
        () -> Assertions.assertEquals(variable, resolvedVariable.get())
    );
  }
}