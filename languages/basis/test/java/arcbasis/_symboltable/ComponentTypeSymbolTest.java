/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods of {@link ComponentTypeSymbol}.
 */
public class ComponentTypeSymbolTest extends ArcBasisTestBase {

  @Test
  public void shouldStateIfHasParameters() {
    // Given
    ComponentTypeSymbol compWithoutParameters = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp1")
      .setSpannedScope(ArcBasisMill.scope()).build();
    ComponentTypeSymbol compWithParameters = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp2")
      .setSpannedScope(ArcBasisMill.scope()).build();
    List<VariableSymbol> params = Arrays.asList(
      mock(VariableSymbol.class),
      mock(VariableSymbol.class),
      mock(VariableSymbol.class)
    );

    // When
    params.forEach(compWithParameters.getSpannedScope()::add);
    compWithParameters.addAllParameter(params);

    // Then
    Assertions.assertFalse(compWithoutParameters.hasParameters());
    Assertions.assertTrue(compWithParameters.hasParameters());
    Assertions.assertEquals(3, compWithParameters.getParameterList().size());
  }

  @Test
  public void shouldReturnParametersIfPresent() {
    // Given
    ComponentTypeSymbol compWithoutParameters = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp1")
      .setSpannedScope(ArcBasisMill.scope()).build();
    ComponentTypeSymbol compWithParameters = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp2")
      .setSpannedScope(ArcBasisMill.scope()).build();
    List<VariableSymbol> params = Arrays.asList(
      ArcBasisMill.variableSymbolBuilder().setName("first").build(),
      ArcBasisMill.variableSymbolBuilder().setName("second").build(),
      ArcBasisMill.variableSymbolBuilder().setName("third").build()
    );

    // When
    params.forEach(compWithParameters.getSpannedScope()::add);
    compWithParameters.addAllParameter(params);

    // Then
    for(VariableSymbol param : params) {
      Assertions.assertTrue(compWithParameters.getParameter(param.getName()).isPresent());
      Assertions.assertFalse(compWithoutParameters.getParameter(param.getName()).isPresent());
    }
  }

  @Test
  public void shouldStateIfHasTypeParameters() {
    // Given
    ComponentTypeSymbol compWithoutTypeParameters = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp1")
      .setSpannedScope(ArcBasisMill.scope()).build();
    ComponentTypeSymbol compWithTypeParameters = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp2")
      .setSpannedScope(ArcBasisMill.scope()).build();
    List<TypeVarSymbol> typeParams = Arrays.asList(
      mock(TypeVarSymbol.class), mock(TypeVarSymbol.class), mock(TypeVarSymbol.class));
    typeParams.forEach(compWithTypeParameters.getSpannedScope()::add);

    // When & Then
    Assertions.assertFalse(compWithoutTypeParameters.hasTypeParameter());
    Assertions.assertTrue(compWithTypeParameters.hasTypeParameter());
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldReturnIncomingPortsOnly(Map<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    Assertions.assertIterableEquals(ports.entrySet().stream()
        .filter(p -> p.getValue().equals(true)).map(Map.Entry::getKey).collect(Collectors.toList()),
      symbol.getIncomingPorts().stream().map(PortSymbol::getName).collect(Collectors.toList()));
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldReturnOutgoingPortsOnly(Map<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    Assertions.assertIterableEquals(ports.entrySet().stream()
        .filter(p -> p.getValue().equals(false)).map(Map.Entry::getKey).collect(Collectors.toList()),
      symbol.getOutgoingPorts().stream().map(PortSymbol::getName).collect(Collectors.toList()));
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldFindPortWithExpectedDirection(Map<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    for (String port : ports.keySet()) {
      if (ports.get(port)) {
        Assertions.assertTrue(symbol.getIncomingPort(port).isPresent());
        Assertions.assertFalse(symbol.getOutgoingPort(port).isPresent());
      }
      else {
        Assertions.assertFalse(symbol.getIncomingPort(port).isPresent());
        Assertions.assertTrue(symbol.getOutgoingPort(port).isPresent());
      }
    }
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldStateCorrectlyIFHasPorts(Map<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    if (ports.isEmpty()) {
      Assertions.assertFalse(symbol.hasPorts());
    }
    else {
      Assertions.assertTrue(symbol.hasPorts());
    }
  }

  static Stream<Arguments> portNameAndDirectionProvider() {
    LinkedHashMap<String, Boolean> ports1 = new LinkedHashMap<>();
    LinkedHashMap<String, Boolean> ports2 = new LinkedHashMap<>();
    ports2.put("o1", false);
    ports2.put("o2", false);
    LinkedHashMap<String, Boolean> ports3 = new LinkedHashMap<>();
    ports3.put("i1", true);
    ports3.put("i2", true);
    LinkedHashMap<String, Boolean> ports4 = new LinkedHashMap<>();
    ports4.put("i1", true);
    ports4.put("o1", false);
    ports4.put("i2", true);
    ports4.put("o2", false);
    return Stream.of(arguments(ports1), arguments(ports2), arguments(ports3), arguments(ports4));
  }

  private ComponentTypeSymbol buildTestComponentWithPorts(Map<String, Boolean> ports) {
    ComponentTypeSymbol compSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(ArcBasisMill.scope()).build();
    for (String port : ports.keySet()) {
      PortSymbol portSymbol = ArcBasisMill.portSymbolBuilder()
        .setName(port).setType(mock(SymTypeExpression.class)).setIncoming(ports.get(port)).setOutgoing(!ports.get(port)).build();
      compSymbol.getSpannedScope().add(portSymbol);
    }
    return compSymbol;
  }

  @ParameterizedTest
  @MethodSource("instanceNamesProvider")
  public void shouldFindSubComponents(List<String> instances) {
    ComponentTypeSymbol symbol = builtTestComponentWithInstances(instances);
    Assertions.assertEquals(symbol.getSubcomponents().size(), instances.size());
    Assertions.assertIterableEquals(symbol.getSubcomponents()
      .stream().map(SubcomponentSymbol::getName).collect(Collectors.toList()), instances);
  }

  static Stream<Arguments> instanceNamesProvider() {
    return Stream.of(
      arguments(Collections.emptyList()),
      arguments(Arrays.asList("sub1", "sub2", "sub3")));
  }

  @Test
  public void shouldFindExpectedSubComponent() {
    List<String> instances = Arrays.asList("sub1", "sub2", "sub3");
    ComponentTypeSymbol symbol = this.builtTestComponentWithInstances(instances);
    for (String instance : instances) {
      Assertions.assertTrue(symbol.getSubcomponents(instance).isPresent());
      Assertions.assertEquals(symbol.getSubcomponents(instance).get().getName(), instance);
    }
  }

  @Test
  public void shouldNotFindUnexpectedSubComponent() {
    ComponentTypeSymbol symbol1 = this.builtTestComponentWithInstances(Collections.emptyList());
    ComponentTypeSymbol symbol2 = this.builtTestComponentWithInstances(
      Arrays.asList("sub1", "sub2", "sub3"));
    Assertions.assertFalse(symbol1.getSubcomponents("sub4").isPresent());
    Assertions.assertFalse(symbol2.getSubcomponents("sub4").isPresent());
  }

  @Test
  void shouldBeAtomicOrDecomposed() {
    ComponentTypeSymbol composedComponent =
      builtTestComponentWithInstances(Arrays.asList("a", "b", "c"));
    ComponentTypeSymbol atomicComponent =
      builtTestComponentWithInstances(Collections.emptyList());
    Assertions.assertTrue(composedComponent.isDecomposed());
    Assertions.assertFalse(composedComponent.isAtomic());
    Assertions.assertFalse(atomicComponent.isDecomposed());
    Assertions.assertTrue(atomicComponent.isAtomic());
  }

  private ComponentTypeSymbol builtTestComponentWithInstances(List<String> instances) {
    ComponentTypeSymbol compSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(ArcBasisMill.scope()).build();
    for (String instance : instances) {
      SubcomponentSymbol subCompSymbol = ArcBasisMill.subcomponentSymbolBuilder()
        .setName(instance).setType(mock(CompKindExpression.class)).build();
      compSymbol.getSpannedScope().add(subCompSymbol);
    }
    return compSymbol;
  }
}
