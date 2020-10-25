/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.util.ArcError;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds tests for the handwritten methods of {@link ComponentTypeSymbol}.
 */
public class ComponentTypeSymbolTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest
  @MethodSource("innerComponentNamesProvider")
  public void shouldFindInnerComponents(List<String> innerComponents) {
    ComponentTypeSymbol symbol = this.builtTestComponentWithInnerComponents(innerComponents);
    Assertions.assertEquals(symbol.getInnerComponents().size(), innerComponents.size());
    Assertions.assertIterableEquals(symbol.getInnerComponents()
      .stream().map(ComponentTypeSymbol::getName).collect(Collectors.toList()), innerComponents);
  }

  static Stream<Arguments> innerComponentNamesProvider() {
    return Stream.of(
      arguments(Collections.emptyList()),
      arguments(Arrays.asList("A", "B", "c"))
    );
  }

  @Test
  public void shouldFindExpectedInnerComponent() {
    List<String> innerComponents = Arrays.asList("A", "B", "C");
    ComponentTypeSymbol symbol = this.builtTestComponentWithInnerComponents(innerComponents);
    for (String innerComponent : innerComponents) {
      Assertions.assertTrue(symbol.getInnerComponent(innerComponent).isPresent());
      Assertions
        .assertEquals(symbol.getInnerComponent(innerComponent).get().getName(), innerComponent);
    }
  }

  @Test
  public void shouldNotFindUnexpectedComponents() {
    ComponentTypeSymbol symbol1 =
      this.builtTestComponentWithInnerComponents(Collections.emptyList());
    ComponentTypeSymbol symbol2 =
      this.builtTestComponentWithInnerComponents(Arrays.asList("A", "B", "C"));
    Assertions.assertFalse(symbol1.getInnerComponent("D").isPresent());
    Assertions.assertFalse(symbol2.getInnerComponent("D").isPresent());
  }

  @Test
  public void shouldStateIfIsInner() {
    ComponentTypeSymbol outerCompSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp1")
      .setSpannedScope(new ArcBasisScope()).build();
    ComponentTypeSymbol innerCompSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp2")
      .setSpannedScope(new ArcBasisScope()).build();
    innerCompSymbol.setOuterComponent(outerCompSymbol);
    Assertions.assertTrue(innerCompSymbol.isInnerComponent());
    Assertions.assertFalse(outerCompSymbol.isInnerComponent());
  }

  private ComponentTypeSymbol builtTestComponentWithInnerComponents(List<String> innerComponents) {
    ComponentTypeSymbol compSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(new ArcBasisScope()).build();
    for (String innerComponent : innerComponents) {
      ComponentTypeSymbol innerCompSymbol = ArcBasisMill.componentTypeSymbolBuilder()
        .setName(innerComponent)
        .setSpannedScope(new ArcBasisScope()).build();
      compSymbol.getSpannedScope().add(innerCompSymbol);
    }
    return compSymbol;
  }

  @Test
  public void shouldStateIfHasParameters() {
    ComponentTypeSymbol compWithoutParameters = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp1")
      .setSpannedScope(new ArcBasisScope()).build();
    ComponentTypeSymbol compWithParameters = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp2")
      .setSpannedScope(new ArcBasisScope()).build();
    compWithParameters.addParameters(Arrays.asList(mock(FieldSymbol.class), mock(FieldSymbol.class),
      mock(FieldSymbol.class)));
    Assertions.assertFalse(compWithoutParameters.hasParameters());
    Assertions.assertTrue(compWithParameters.hasParameters());
  }

  @Test
  public void shouldStateIfHasTypeParameters() {
    ComponentTypeSymbol compWithoutTypeParameters = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp1")
      .setSpannedScope(new ArcBasisScope()).build();
    ComponentTypeSymbol compWithTypeParameters = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp2")
      .setSpannedScope(new ArcBasisScope()).build();
    compWithTypeParameters.addTypeParameters(Arrays
      .asList(mock(TypeVarSymbol.class), mock(TypeVarSymbol.class), mock(TypeVarSymbol.class)));
    Assertions.assertFalse(compWithoutTypeParameters.hasTypeParameter());
    Assertions.assertTrue(compWithTypeParameters.hasTypeParameter());
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldReturnIncomingPortsOnly(HashMap<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    Assertions.assertIterableEquals(ports.entrySet().stream()
        .filter(p -> p.getValue().equals(true)).map(Map.Entry::getKey).collect(Collectors.toList()),
      symbol.getIncomingPorts().stream().map(PortSymbol::getName).collect(Collectors.toList()));
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldReturnOutgoingPortsOnly(HashMap<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    Assertions.assertIterableEquals(ports.entrySet().stream()
        .filter(p -> p.getValue().equals(false)).map(Map.Entry::getKey).collect(Collectors.toList()),
      symbol.getOutgoingPorts().stream().map(PortSymbol::getName).collect(Collectors.toList()));
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldFindPortWithExpectedDirection(HashMap<String, Boolean> ports) {
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
  public void shouldStateCorrectlyIFHasPorts(HashMap<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    if (ports.isEmpty()) {
      Assertions.assertFalse(symbol.hasPorts());
    }
    else {
      Assertions.assertTrue(symbol.hasPorts());
    }
  }

  static Stream<Arguments> portNameAndDirectionProvider() {
    HashMap<String, Boolean> ports1 = new HashMap<>();
    HashMap<String, Boolean> ports2 = new HashMap<>();
    ports2.put("o1", false);
    ports2.put("o2", false);
    HashMap<String, Boolean> ports3 = new HashMap<>();
    ports3.put("i1", true);
    ports3.put("i2", true);
    HashMap<String, Boolean> ports4 = new HashMap<>();
    ports4.put("i1", true);
    ports4.put("o1", false);
    ports4.put("i2", true);
    ports4.put("o2", false);
    return Stream.of(arguments(ports1), arguments(ports2), arguments(ports3), arguments(ports4));
  }

  private ComponentTypeSymbol buildTestComponentWithPorts(HashMap<String, Boolean> ports) {
    ComponentTypeSymbol compSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(new ArcBasisScope()).build();
    for (String port : ports.keySet()) {
      PortSymbol portSymbol = ArcBasisMill.portSymbolBuilder()
        .setName(port).setType(mock(SymTypeExpression.class)).setIncoming(ports.get(port)).build();
      compSymbol.getSpannedScope().add(portSymbol);
    }
    return compSymbol;
  }

  @ParameterizedTest
  @MethodSource("instanceNamesProvider")
  public void shouldFindSubComponents(List<String> instances) {
    ComponentTypeSymbol symbol = builtTestComponentWithInstances(instances);
    Assertions.assertEquals(symbol.getSubComponents().size(), instances.size());
    Assertions.assertIterableEquals(symbol.getSubComponents()
      .stream().map(ComponentInstanceSymbol::getName).collect(Collectors.toList()), instances);
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
      Assertions.assertTrue(symbol.getSubComponent(instance).isPresent());
      Assertions.assertEquals(symbol.getSubComponent(instance).get().getName(), instance);
    }
  }

  @Test
  public void shouldNotFindUnexpectedSubComponent() {
    ComponentTypeSymbol symbol1 = this.builtTestComponentWithInstances(Collections.emptyList());
    ComponentTypeSymbol symbol2 = this.builtTestComponentWithInstances(
      Arrays.asList("sub1", "sub2", "sub3"));
    Assertions.assertFalse(symbol1.getSubComponent("sub4").isPresent());
    Assertions.assertFalse(symbol2.getSubComponent("sub4").isPresent());
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
      .setSpannedScope(new ArcBasisScope()).build();
    for (String instance : instances) {
      ComponentInstanceSymbol subCompSymbol = ArcBasisMill.componentInstanceSymbolBuilder()
        .setName(instance).setType(mock(ComponentTypeSymbolLoader.class)).build();
      compSymbol.getSpannedScope().add(subCompSymbol);
    }
    return compSymbol;
  }
}