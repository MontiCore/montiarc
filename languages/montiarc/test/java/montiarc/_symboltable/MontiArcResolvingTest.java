/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.SymbolService;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.SymTypeExpression;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
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

public class MontiArcResolvingTest extends MontiArcTestBase {

  protected IMontiArcScope scope;

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

  @BeforeEach
  @Override
  public void init() {
    MontiArcMill.globalScope().clear();
    super.init();
    setUpType();
  }

  protected IMontiArcScope getScope() {
    return this.scope;
  }

  protected void setUpType() {
    this.scope = MontiArcMill.scope();
    IMontiArcArtifactScope aScope = MontiArcMill.artifactScope();
    aScope.setName("B");
    aScope.addSubScope(this.getScope());
    MontiArcMill.globalScope().addSubScope(aScope);
  }

  @ParameterizedTest
  @MethodSource("validScopeAndTypeAndResolutionNameProvider")
  public void shouldResolveType(@NotNull String scopeName, @NotNull String typeName, @NotNull String resolutionName) {
    //Given
    this.getScope().setName(scopeName);
    this.getScope().add(MontiArcMill.typeSymbolBuilder()
      .setName(typeName).setSpannedScope(MontiArcMill.scope()).build());

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
    this.getScope().add(MontiArcMill.typeSymbolBuilder()
      .setName(typeName).setSpannedScope(MontiArcMill.scope()).build());

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
    this.getScope().add(MontiArcMill.oOTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(MontiArcMill.scope()).build());

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
    this.getScope().add(MontiArcMill.oOTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(MontiArcMill.scope()).build());

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
    this.getScope().add(MontiArcMill.oOTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(MontiArcMill.scope()).build());

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
    this.getScope().add(MontiArcMill.componentTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(MontiArcMill.scope()).build());

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
    this.getScope().add(MontiArcMill.componentTypeSymbolBuilder()
      .setName(typeName).setSpannedScope(MontiArcMill.scope()).build());

    //When
    Optional<ComponentTypeSymbol> symbol = this.getScope().resolveComponentType(resolutionName);

    //Then
    Assertions.assertFalse(symbol.isPresent());
  }

  @Test
  public void shouldNotResolveShadowedVariable() {
    // Given
    // Outer scope with variable var
    IMontiArcScope outer = MontiArcMill.scope();
    outer.setName("Outer");
    outer.setShadowing(false);
    VariableSymbol var1 = MontiArcMill.variableSymbolBuilder()
      .setName("var")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setEnclosingScope(outer)
      .build();
    outer.add(var1);
    // Inner scope with variable var
    IMontiArcScope inner = MontiArcMill.scope();
    inner.setName("Inner");
    inner.setShadowing(true);
    VariableSymbol var2 = MontiArcMill.variableSymbolBuilder()
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
    IMontiArcScope scope = MontiArcMill.scope();
    PortSymbol port = MontiArcMill.portSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    SymbolService.link(scope, port);

    // When
    List<VariableSymbol> variables = scope.resolveVariableMany("port");

    // Then
    Assertions.assertFalse(variables.isEmpty(),
      "Failed to resolve the port to variable symbol adapter.");
    Assertions.assertEquals(1, variables.size(),
      "Failed to resolve only the single port to variable symbol adapter.");
  }

  @Test
  public void shouldResolvePort2Variable2() {
    // Given
    IMontiArcScope scope = MontiArcMill.scope();
    PortSymbol port = MontiArcMill.portSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    SymbolService.link(scope, port);

    VariableSymbol variable = MontiArcMill.variableSymbolBuilder()
      .setName("variable")
      .setType(Mockito.mock(SymTypeExpression.class))
      .build();
    SymbolService.link(scope, variable);

    // When
    List<VariableSymbol> variables = scope.resolveVariableMany("port");

    // Then
    Assertions.assertFalse(variables.isEmpty(),
      "Failed to resolve the port to variable symbol adapter.");
    Assertions.assertEquals(1, variables.size(),
      "Failed to resolve only the single port to variable symbol adapter.");
  }

  @Test
  @Disabled("see https://git.rwth-aachen.de/monticore/monticore/-/issues/3241")
  public void shouldResolvePort2Variable3() {
    // Given
    IMontiArcScope scope = MontiArcMill.scope();
    PortSymbol port = MontiArcMill.portSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    SymbolService.link(scope, port);

    VariableSymbol variable = MontiArcMill.variableSymbolBuilder()
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
    IMontiArcScope scope = MontiArcMill.scope();
    PortSymbol port = MontiArcMill.portSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    SymbolService.link(scope, port);

    // When
    List<VariableSymbol> variables1 = scope.resolveVariableMany("port");
    List<VariableSymbol> variables2 = scope.resolveVariableMany("port");

    // Then
    Assertions.assertFalse(variables1.isEmpty(),
      "The first call failed to resolve the port to variable symbol adapter.");
    Assertions.assertFalse(variables2.isEmpty(),
      "The second call failed to resolve the port to variable symbol adapter.");
    Assertions.assertEquals(1, variables1.size(),
      "The first call failed to resolve only the single port to variable symbol adapter.");
    Assertions.assertEquals(1, variables2.size(),
      "The second call failed to resolve only the single port to variable symbol adapter.");
  }

  @Test
  @Disabled("see https://git.rwth-aachen.de/monticore/monticore/-/issues/3241")
  public void shouldResolvePort2Variable5() {
    // Given
    IMontiArcScope scope = MontiArcMill.scope();
    PortSymbol port = MontiArcMill.portSymbolBuilder()
      .setName("port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    SymbolService.link(scope, port);

    VariableSymbol variable = MontiArcMill.variableSymbolBuilder()
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
    Assertions.assertFalse(variables1.isEmpty(),
      "The first call failed to resolve the variable symbols.");
    Assertions.assertFalse(variables2.isEmpty(),
      "The second call failed to resolve the variable symbol.");
    Assertions.assertFalse(variables3.isEmpty(),
      "The third call failed to resolve the variable symbols.");
    Assertions.assertEquals(2, variables1.size(),
      "The first call failed to resolve both the variable symbol and the port to variable symbol adapter.");
    Assertions.assertEquals(1, variables2.size(),
      "The second call failed to resolve only the port to variable symbol adapter.");
    Assertions.assertEquals(2, variables3.size(),
      "The third call failed to resolve both the variable symbol and the port to variable symbol adapter.");
  }

  @Test
  public void shouldResolveInParentComponents() {
    // Given
    IMontiArcScope scope = MontiArcMill.scope();
    IMontiArcScope parent1Scope = MontiArcMill.scope();
    IMontiArcScope parent2Scope = MontiArcMill.scope();

    ComponentTypeSymbol parent1 =
      MontiArcMill.componentTypeSymbolBuilder().setName("Parent").setSpannedScope(parent1Scope).build();
    ComponentTypeSymbol parent2 =
      MontiArcMill.componentTypeSymbolBuilder().setName("Parent").setSpannedScope(parent2Scope).build();
    ComponentTypeSymbol child = MontiArcMill.componentTypeSymbolBuilder().setName("Child").setSpannedScope(scope)
      .setSuperComponentsList(List.of(new CompKindOfComponentType(parent1), new CompKindOfComponentType(parent2))).build();

    PortSymbol port1 = MontiArcMill.portSymbolBuilder().setName("p1")
      .setType(Mockito.mock(SymTypeExpression.class)).build();
    parent1Scope.add(port1);

    VariableSymbol variable1 = MontiArcMill.variableSymbolBuilder().setName("var1").setType(Mockito.mock(
      SymTypeExpression.class)).build();
    parent1Scope.add(variable1);

    PortSymbol port2 = MontiArcMill.portSymbolBuilder().setName("p2")
      .setType(Mockito.mock(SymTypeExpression.class)).build();
    parent1Scope.add(port2);

    VariableSymbol variable2 = MontiArcMill.variableSymbolBuilder().setName("var2").setType(Mockito.mock(
      SymTypeExpression.class)).build();
    parent1Scope.add(variable2);

    // When
    Optional<PortSymbol> resolvedPort1 = scope.resolvePort("p1");
    Optional<VariableSymbol> resolvedVariable1 = scope.resolveVariable("var1");
    Optional<PortSymbol> resolvedPort2 = scope.resolvePort("p2");
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
    IMontiArcScope scope = MontiArcMill.scope();
    IMontiArcScope parentScope = MontiArcMill.scope();

    ComponentTypeSymbol parent =
      MontiArcMill.componentTypeSymbolBuilder().setName("Parent").setSpannedScope(parentScope).build();
    ComponentTypeSymbol child = MontiArcMill.componentTypeSymbolBuilder().setName("Child").setSpannedScope(scope)
      .setSuperComponentsList(Collections.singletonList(new CompKindOfComponentType(parent))).build();

    PortSymbol parentPort = MontiArcMill.portSymbolBuilder().setName("p1")
      .setType(Mockito.mock(SymTypeExpression.class)).build();
    parentScope.add(parentPort);

    PortSymbol port = MontiArcMill.portSymbolBuilder().setName("p1")
      .setType(Mockito.mock(SymTypeExpression.class)).build();
    scope.add(port);

    VariableSymbol parentVariable = MontiArcMill.variableSymbolBuilder().setName("var1").setType(Mockito.mock(
      SymTypeExpression.class)).build();
    parentScope.add(parentVariable);

    VariableSymbol variable = MontiArcMill.variableSymbolBuilder().setName("var1").setType(Mockito.mock(
      SymTypeExpression.class)).build();
    scope.add(variable);

    // When
    Optional<PortSymbol> resolvedPort = scope.resolvePort("p1");
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
    IMontiArcScope enclosingScope = MontiArcMill.scope();
    IMontiArcScope scope = MontiArcMill.scope();
    enclosingScope.addSubScope(scope);

    ComponentTypeSymbol parent =
      MontiArcMill.componentTypeSymbolBuilder().setName("Parent").setSpannedScope(MontiArcMill.scope()).build();
    ComponentTypeSymbol child = MontiArcMill.componentTypeSymbolBuilder().setName("Child").setSpannedScope(scope)
      .setSuperComponentsList(Collections.singletonList(new CompKindOfComponentType(parent))).build();

    PortSymbol port = MontiArcMill.portSymbolBuilder().setName("p1")
      .setType(Mockito.mock(SymTypeExpression.class)).build();
    enclosingScope.add(port);

    SubcomponentSymbol instance =
      MontiArcMill.subcomponentSymbolBuilder().setName("ins1").setType(Mockito.mock(
        CompKindExpression.class)).build();
    enclosingScope.add(instance);

    VariableSymbol variable = MontiArcMill.variableSymbolBuilder().setName("var1").setType(Mockito.mock(
      SymTypeExpression.class)).build();
    enclosingScope.add(variable);

    // When
    Optional<PortSymbol> resolvedPort = scope.resolvePort("p1");
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
    IMontiArcScope enclosingScope = MontiArcMill.scope();
    IMontiArcScope scope = MontiArcMill.scope();
    enclosingScope.addSubScope(scope);

    PortSymbol port = MontiArcMill.portSymbolBuilder().setName("p1")
      .setType(Mockito.mock(SymTypeExpression.class)).build();
    enclosingScope.add(port);

    SubcomponentSymbol instance =
      MontiArcMill.subcomponentSymbolBuilder().setName("ins1").setType(Mockito.mock(
        CompKindExpression.class)).build();
    enclosingScope.add(instance);

    VariableSymbol variable = MontiArcMill.variableSymbolBuilder().setName("var1").setType(Mockito.mock(
      SymTypeExpression.class)).build();
    enclosingScope.add(variable);

    // When
    Optional<PortSymbol> resolvedPort = scope.resolvePort("p1");
    Optional<SubcomponentSymbol> resolvedInstance = scope.resolveSubcomponent("ins1");
    Optional<VariableSymbol> resolvedVariable = scope.resolveVariable("var1");

    // Then
    Assertions.assertFalse(resolvedPort.isEmpty(),
      "Failed to resolve the port symbol.");
    Assertions.assertFalse(resolvedInstance.isEmpty(),
      "Failed to resolve the component instance symbol.");
    Assertions.assertFalse(resolvedVariable.isEmpty(),
      "Failed to resolve the variable symbol.");
    Assertions.assertEquals(port, resolvedPort.get());
    Assertions.assertEquals(instance, resolvedInstance.get());
    Assertions.assertEquals(variable, resolvedVariable.get());
  }
}
