/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc.AbstractTest;
import montiarc.util.ArcError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

/**
 * Holds test for the handwritten methods of {@link ASTComponent}.
 */
public class ComponentTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest
  @MethodSource("sourceAndTargetsProvider")
  public void shouldFindExpectedConnectorsFromSource(String source, String[] targets) {
    List<ASTConnector> connectors = this.getConnectorsTestComponent()
      .getConnectorsMatchingSource(source);
    List<String> expectedTargets = Arrays.asList(targets);
    List<String> actualTargets = connectors.stream()
      .flatMap(astConnector -> astConnector.getTargetsNames().stream())
      .collect(Collectors.toList());
    Assertions.assertTrue(expectedTargets.containsAll(actualTargets));
    Assertions.assertTrue(actualTargets.containsAll(expectedTargets));
  }

  static Stream<Arguments> sourceAndTargetsProvider() {
    return Stream.of(Arguments.of("i1", new String[] { "sub1.i1" }),
      Arguments.of("sub1.o1", new String[] { "o1", "sub2.i1" }),
      Arguments.of("sub2.o1", new String[] { "o2" }),
      Arguments.of("i2", new String[] { "sub1.i2", "sub2.i2" }),
      Arguments.of("i3", new String[] { "sub1.i2" }));
  }

  @ParameterizedTest
  @MethodSource("targetAndSourcesProvider")
  public void shouldFindExpectedConnectorsFromTarget(String target, String[] sources) {
    List<ASTConnector> connectors = this.getConnectorsTestComponent()
      .getConnectorsMatchingTarget(target);
    List<String> expectedSources = Arrays.asList(sources);
    List<String> actualSources = connectors.stream()
      .map(ASTConnector::getSourceName)
      .collect(Collectors.toList());
    Assertions.assertTrue(expectedSources.containsAll(actualSources));
    Assertions.assertTrue(actualSources.containsAll(expectedSources));
  }

  static Stream<Arguments> targetAndSourcesProvider() {
    return Stream.of(Arguments.of("sub1.i1", new String[] { "i1" }),
      Arguments.of("o1", new String[] { "sub1.o1" }),
      Arguments.of("o2", new String[] { "sub2.o1" }),
      Arguments.of("sub2.i1", new String[] { "sub1.o1" }),
      Arguments.of("sub1.i2", new String[] { "i2", "i3" }),
      Arguments.of("sub2.i2", new String[] { "i2" }));
  }

  private ASTComponent getConnectorsTestComponent() {
    return ArcMill.componentBuilder().setName("Connectors")
      .setHead(ArcMill.componentHeadBuilder().build())
      .setBody(ArcMill.componentBodyBuilder().addArcElement(ArcMill.componentInterfaceBuilder()
        .setPortList(Collections.singletonList(ArcMill.portDeclarationBuilder()
          .setDirection("in")
          .setType(Mockito.mock(ASTMCType.class))
          .setPortList(new String[] { "i1", "i2", "i3" }).build())).build())
        .addArcElement(ArcMill.componentInterfaceBuilder()
          .setPortList(Collections.singletonList(ArcMill.portDeclarationBuilder()
            .setDirection("out")
            .setType(Mockito.mock(ASTMCType.class))
            .setPortList(new String[] { "o1", "o2" }).build())).build())
        .addArcElement(ArcMill.connectorBuilder()
          .setSource("i1").setTargetList("sub1.i1").build())
        .addArcElement(ArcMill.connectorBuilder()
          .setSource("sub1.o1").setTargetList("o1").build())
        .addArcElement(ArcMill.connectorBuilder()
          .setSource("sub2.o1").setTargetList("o2").build())
        .addArcElement(ArcMill.connectorBuilder()
          .setSource("sub1.o1").setTargetList("sub2.i1").build())
        .addArcElement(ArcMill.componentInstantiationBuilder()
          .setType(Mockito.mock(ASTMCObjectType.class))
          .setInstanceList("sub1", "sub2").build())
        .addArcElement(ArcMill.connectorBuilder()
          .setSource("i2").setTargetList("sub1.i2", "sub2.i2").build())
        .addArcElement(ArcMill.connectorBuilder()
          .setSource("i3").setTargetList("sub1.i2").build())
        .build())
      .build();
  }

  @ParameterizedTest
  @MethodSource("subCompInstancesProvider")
  public void shouldFindSubComponentInstances(String[] instances) {
    List<String> expectedInstances = Arrays.asList(instances);
    List<String> actualInstances = this.getInstancesTestComponent().getSubComponentNames();
    Assertions.assertTrue(expectedInstances.containsAll(actualInstances));
    Assertions.assertTrue(actualInstances.containsAll(expectedInstances));
  }

  static Stream<Arguments> subCompInstancesProvider() {
    return Stream.of(Arguments.of((Object) new String[] { "a1", "a2", "a3", "a4", "a5", "b1", "b2",
      "c1", "c2", "c3" }));
  }

  @ParameterizedTest
  @MethodSource("indexAndComponentInstanceProvider")
  public void shouldFindComponentInstance(int index, String instance) {
    Assertions.assertEquals(instance, getInstancesTestComponent().getInstanceName(index));
  }

  static Stream<Arguments> indexAndComponentInstanceProvider() {
    return Stream.of(Arguments.of(0, "comp1"),
      Arguments.of(1, "comp2"),
      Arguments.of(2, "comp3"));
  }

  @ParameterizedTest
  @MethodSource("componentInstancesProvider")
  public void shouldFindComponentInstances(String[] instances) {
    List<String> expectedInstances = Arrays.asList(instances);
    List<String> actualInstances = this.getInstancesTestComponent().getInstancesNames();
    Assertions.assertTrue(expectedInstances.containsAll(actualInstances));
    Assertions.assertTrue(actualInstances.containsAll(expectedInstances));
  }

  static Stream<Arguments> componentInstancesProvider() {
    return Stream.of(Arguments.of((Object) new String[] { "comp1", "comp2", "comp3" }));
  }

  private ASTComponent getInstancesTestComponent() {

    return ArcMill.componentBuilder().setName("Instances")
      .setInstanceList("comp1", "comp2", "comp3")
      .setHead(ArcMill.componentHeadBuilder().build())
      .setBody(ArcMill.componentBodyBuilder().addArcElement(ArcMill.componentInstantiationBuilder()
        .setType(Mockito.mock(ASTMCObjectType.class)).setInstanceList("a1").build())
        .addArcElement(ArcMill.componentBuilder().setName("A")
          .setBody(ArcMill.componentBodyBuilder().build())
          .setHead(ArcMill.componentHeadBuilder().build())
          .build())
        .addArcElement(ArcMill.componentInstantiationBuilder()
          .setType(Mockito.mock(ASTMCObjectType.class)).setInstanceList("a2").build())
        .addArcElement(ArcMill.componentInstantiationBuilder()
          .setType(Mockito.mock(ASTMCObjectType.class)).setInstanceList("a3", "a4", "a5").build())
        .addArcElement(ArcMill.componentBuilder().setName("B")
          .setInstanceList("b1", "b2")
          .setBody(ArcMill.componentBodyBuilder().build())
          .setHead(ArcMill.componentHeadBuilder().build())
          .build())
        .addArcElement(ArcMill.componentBuilder().setName("C")
          .setInstanceList("c1", "c2", "c3")
          .setBody(ArcMill.componentBodyBuilder().build())
          .setHead(ArcMill.componentHeadBuilder().build())
          .build())
        .build())
      .build();
  }

  @ParameterizedTest
  @MethodSource("innerComponentsProvider")
  public void shouldFindExpectedInnerComponents(String... innerComponents) {
    List<String> expectedInnerComponents = Arrays.asList(innerComponents);
    List<String> actualInnerComponents = this.getInnerComponentsTestComponent()
      .getInnerComponents().stream().map(ASTComponent::getName)
      .collect(Collectors.toList());
    Assertions.assertTrue(expectedInnerComponents.containsAll(actualInnerComponents));
    Assertions.assertTrue(actualInnerComponents.containsAll(expectedInnerComponents));
  }

  static Stream<Arguments> innerComponentsProvider() {
    return Stream.of(Arguments.of((Object) new String[] { "A", "B", "C", "D" }));
  }

  private ASTComponent getInnerComponentsTestComponent() {
    return ArcMill.componentBuilder()
      .setName("InnerComponents")
      .setHead(ArcMill.componentHeadBuilder().build())
      .setBody(ArcMill.componentBodyBuilder()
        .addArcElement(ArcMill.componentBuilder().setName("A")
          .setHead(ArcMill.componentHeadBuilder().build())
          .setBody(ArcMill.componentBodyBuilder().build())
          .build())
        .addArcElement(ArcMill.componentBuilder().setName("B")
          .setInstanceList("b1", "b2")
          .setHead(ArcMill.componentHeadBuilder().build())
          .setBody(ArcMill.componentBodyBuilder().build())
          .build())
        .addArcElement(ArcMill.componentBuilder().setName("C")
          .setHead(ArcMill.componentHeadBuilder()
            .setParameterList(Collections.singletonList(ArcMill.arcParameterBuilder()
              .setType(Mockito.mock(ASTMCObjectType.class)).setName("c").build()))
            .build())
          .setBody(ArcMill.componentBodyBuilder().build())
          .build())
        .addArcElement(ArcMill.componentBuilder().setName("D")
          .setHead(ArcMill.componentHeadBuilder()
            .setTypeParameterList(Collections.singletonList(ArcMill.arcTypeParameterBuilder()
              .setName("T").build()))
            .build())
          .setBody(ArcMill.componentBodyBuilder().build())
          .build())
        .build())
      .build();
  }

  @ParameterizedTest
  @MethodSource("variablesProvider")
  public void shouldFindExpectedVariables(String... variables) {
    List<String> expectedVariables = Arrays.asList(variables);
    List<String> actualVariables = this.getVariablesTestComponent().getVariableNames();
    Assertions.assertTrue(expectedVariables.containsAll(actualVariables));
    Assertions.assertTrue(actualVariables.containsAll(expectedVariables));
  }

  static Stream<Arguments> variablesProvider() {
    return Stream.of(
      Arguments.of((Object) new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "o" }));
  }

  private ASTComponent getVariablesTestComponent() {
    return ArcMill.componentBuilder()
      .setName("Variables")
      .setHead(ArcMill.componentHeadBuilder().build())
      .setBody(ArcMill.componentBodyBuilder()
        .addArcElement(ArcMill.componentInterfaceBuilder().setPortList(
          Collections.singletonList(ArcMill.portDeclarationBuilder()
            .setDirection("in")
            .setType(Mockito.mock(ASTMCType.class))
            .setPortList("i1").build())).build())
        .addArcElement(ArcMill.componentInterfaceBuilder().setPortList(
          Collections.singletonList(ArcMill.portDeclarationBuilder()
            .setDirection("out")
            .setType(Mockito.mock(ASTMCType.class))
            .setPortList("o1").build())).build())
        .addArcElement(ArcMill.arcVariableDeclarationBuilder()
          .setType(Mockito.mock(ASTMCType.class))
          .setVariableList("a").build())
        .addArcElement(ArcMill.arcVariableDeclarationBuilder()
          .setType(Mockito.mock(ASTMCType.class))
          .setVariableList("b", "c").build())
        .addArcElement(ArcMill.arcVariableDeclarationBuilder()
          .setType(Mockito.mock(ASTMCType.class))
          .setVariableList("d", "e", "f", "g").build())
        .addArcElement(ArcMill.arcVariableDeclarationBuilder()
          .setType(Mockito.mock(ASTMCType.class))
          .setVariableList("h", "i").build())
        .addArcElement(ArcMill.arcVariableDeclarationBuilder()
          .setType(Mockito.mock(ASTMCType.class))
          .setVariableList("o").build()).build()).build();
  }

  @ParameterizedTest
  @MethodSource("portsProvider")
  public void shouldFindExpectedPorts(String[] ports) {
    List<String> expectedPorts = Arrays.asList(ports);
    List<String> actualPorts = getPortTestComponent().getPortNames();
    Assertions.assertTrue(actualPorts.containsAll(expectedPorts));
    Assertions.assertTrue(expectedPorts.containsAll(actualPorts));
  }

  static Stream<Arguments> portsProvider() {
    return Stream.of(Arguments.of((Object) new String[] { "i1", "i2", "i3", "o1", "o2", "o3" }));
  }

  protected ASTComponent getPortTestComponent() {
    return ArcMill.componentBuilder()
      .setName("Ports")
      .setHead(ArcMill.componentHeadBuilder().build())
      .setBody(ArcMill.componentBodyBuilder()
        .addArcElement(ArcMill.componentInterfaceBuilder()
          .setPortList(Arrays.asList(
            ArcMill.portDeclarationBuilder()
              .setDirection("in")
              .setType(Mockito.mock(ASTMCType.class))
              .setPortList("i1").build(),
            ArcMill.portDeclarationBuilder()
              .setDirection("out")
              .setType(Mockito.mock(ASTMCType.class))
              .setPortList("o1").build()))
          .build())
        .addArcElement(ArcMill.componentInterfaceBuilder()
          .setPortList(Arrays.asList(
            ArcMill.portDeclarationBuilder()
              .setDirection("in")
              .setType(Mockito.mock(ASTMCType.class))
              .setPortList("i2", "i3").build(),
            ArcMill.portDeclarationBuilder()
              .setDirection("out")
              .setType(Mockito.mock(ASTMCType.class))
              .setPortList("o2", "o3").build()))
          .build())
        .build())
      .build();
  }
}
