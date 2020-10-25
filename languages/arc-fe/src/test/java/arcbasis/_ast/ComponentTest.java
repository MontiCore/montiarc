/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds test for the handwritten methods of {@link ASTComponentType}.
 */
public class ComponentTest extends AbstractTest {

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

  private ASTComponentType getConnectorsTestComponent() {
    return ArcBasisMill.componentTypeBuilder().setName("Connectors")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(
        ArcBasisMill.componentBodyBuilder().addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .setPortDeclarationList(Collections.singletonList(ArcBasisMill.portDeclarationBuilder()
            .setPortDirection(ArcBasisMill.portDirectionInBuilder().build())
            .setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("i1", "i2", "i3").build())).build())
          .addArcElement(ArcBasisMill.componentInterfaceBuilder()
            .setPortDeclarationList(Collections.singletonList(ArcBasisMill.portDeclarationBuilder()
              .setPortDirection(ArcBasisMill.portDirectionOutBuilder().build())
              .setMCType(Mockito.mock(ASTMCType.class))
              .setPortList("o1", "o2").build())).build())
          .addArcElement(ArcBasisMill.connectorBuilder()
            .setSource("i1").setTargetList("sub1.i1").build())
          .addArcElement(ArcBasisMill.connectorBuilder()
            .setSource("sub1.o1").setTargetList("o1").build())
          .addArcElement(ArcBasisMill.connectorBuilder()
            .setSource("sub2.o1").setTargetList("o2").build())
          .addArcElement(ArcBasisMill.connectorBuilder()
            .setSource("sub1.o1").setTargetList("sub2.i1").build())
          .addArcElement(ArcBasisMill.componentInstantiationBuilder()
            .setMCType(Mockito.mock(ASTMCObjectType.class))
            .setComponentInstanceList("sub1", "sub2").build())
          .addArcElement(ArcBasisMill.connectorBuilder()
            .setSource("i2").setTargetList("sub1.i2", "sub2.i2").build())
          .addArcElement(ArcBasisMill.connectorBuilder()
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

  private ASTComponentType getInstancesTestComponent() {

    return ArcBasisMill.componentTypeBuilder().setName("Instances")
      .setComponentInstanceList("comp1", "comp2", "comp3")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(Mockito.mock(ASTMCObjectType.class)).setComponentInstanceList("a1").build())
        .addArcElement(ArcBasisMill.componentTypeBuilder().setName("A")
          .setBody(ArcBasisMill.componentBodyBuilder().build())
          .setHead(ArcBasisMill.componentHeadBuilder().build())
          .build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(Mockito.mock(ASTMCObjectType.class)).setComponentInstanceList("a2").build())
        .addArcElement(ArcBasisMill.componentInstantiationBuilder()
          .setMCType(Mockito.mock(ASTMCObjectType.class)).setComponentInstanceList("a3", "a4", "a5")
          .build())
        .addArcElement(ArcBasisMill.componentTypeBuilder().setName("B")
          .setComponentInstanceList("b1", "b2")
          .setBody(ArcBasisMill.componentBodyBuilder().build())
          .setHead(ArcBasisMill.componentHeadBuilder().build())
          .build())
        .addArcElement(ArcBasisMill.componentTypeBuilder().setName("C")
          .setComponentInstanceList("c1", "c2", "c3")
          .setBody(ArcBasisMill.componentBodyBuilder().build())
          .setHead(ArcBasisMill.componentHeadBuilder().build())
          .build())
        .build())
      .build();
  }

  @ParameterizedTest
  @MethodSource("innerComponentsProvider")
  public void shouldFindExpectedInnerComponents(String... innerComponents) {
    List<String> expectedInnerComponents = Arrays.asList(innerComponents);
    List<String> actualInnerComponents = this.getInnerComponentsTestComponent()
      .getInnerComponents().stream().map(ASTComponentType::getName)
      .collect(Collectors.toList());
    Assertions.assertTrue(expectedInnerComponents.containsAll(actualInnerComponents));
    Assertions.assertTrue(actualInnerComponents.containsAll(expectedInnerComponents));
  }

  static Stream<Arguments> innerComponentsProvider() {
    return Stream.of(Arguments.of((Object) new String[] { "A", "B", "C", "D" }));
  }

  private ASTComponentType getInnerComponentsTestComponent() {
    return ArcBasisMill.componentTypeBuilder()
      .setName("InnerComponents")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentTypeBuilder().setName("A")
          .setHead(ArcBasisMill.componentHeadBuilder().build())
          .setBody(ArcBasisMill.componentBodyBuilder().build())
          .build())
        .addArcElement(ArcBasisMill.componentTypeBuilder().setName("B")
          .setComponentInstanceList("b1", "b2")
          .setHead(ArcBasisMill.componentHeadBuilder().build())
          .setBody(ArcBasisMill.componentBodyBuilder().build())
          .build())
        .addArcElement(ArcBasisMill.componentTypeBuilder().setName("C")
          .setHead(ArcBasisMill.componentHeadBuilder()
            .setArcParameterList(Collections.singletonList(ArcBasisMill.arcParameterBuilder()
              .setMCType(Mockito.mock(ASTMCObjectType.class))
              .setName("c")
              .build()))
            .build())
          .setBody(ArcBasisMill.componentBodyBuilder().build())
          .build())
        .addArcElement(ArcBasisMill.componentTypeBuilder().setName("D")
          .setHead(Mockito.mock(ASTComponentHead.class))
          .setBody(Mockito.mock(ASTComponentBody.class))
          .build())
        .build())
      .build();
  }

  @ParameterizedTest
  @MethodSource("variablesProvider")
  public void shouldFindExpectedVariables(String... variables) {
    List<String> expectedVariables = Arrays.asList(variables);
    List<String> actualVariables = this.getVariablesTestComponent().getFieldNames();
    Assertions.assertTrue(expectedVariables.containsAll(actualVariables));
    Assertions.assertTrue(actualVariables.containsAll(expectedVariables));
  }

  static Stream<Arguments> variablesProvider() {
    return Stream.of(
      Arguments.of((Object) new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "o" }));
  }

  private ASTComponentType getVariablesTestComponent() {
    return ArcBasisMill.componentTypeBuilder()
      .setName("Variables")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder().setPortDeclarationList(
          Collections.singletonList(ArcBasisMill.portDeclarationBuilder()
            .setPortDirection(ArcBasisMill.portDirectionInBuilder().build())
            .setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("i1").build())).build())
        .addArcElement(ArcBasisMill.componentInterfaceBuilder().setPortDeclarationList(
          Collections.singletonList(ArcBasisMill.portDeclarationBuilder()
            .setPortDirection(ArcBasisMill.portDirectionOutBuilder().build())
            .setMCType(Mockito.mock(ASTMCType.class))
            .setPortList("o1").build())).build())
        .addArcElement(ArcBasisMill.arcFieldDeclarationBuilder()
          .setMCType(Mockito.mock(ASTMCType.class))
          .addArcField("a", Mockito.mock(ASTExpression.class)).build())
        .addArcElement(ArcBasisMill.arcFieldDeclarationBuilder()
          .setMCType(Mockito.mock(ASTMCType.class))
          .addArcField("b", Mockito.mock(ASTExpression.class))
          .addArcField("c", Mockito.mock(ASTExpression.class))
          .build())
        .addArcElement(ArcBasisMill.arcFieldDeclarationBuilder()
          .setMCType(Mockito.mock(ASTMCType.class))
          .addArcField("d", Mockito.mock(ASTExpression.class))
          .addArcField("e", Mockito.mock(ASTExpression.class))
          .addArcField("f", Mockito.mock(ASTExpression.class))
          .addArcField("g", Mockito.mock(ASTExpression.class))
          .build())
        .addArcElement(ArcBasisMill.arcFieldDeclarationBuilder()
          .setMCType(Mockito.mock(ASTMCType.class))
          .addArcField("h", Mockito.mock(ASTExpression.class))
          .addArcField("i", Mockito.mock(ASTExpression.class))
          .build())
        .addArcElement(ArcBasisMill.arcFieldDeclarationBuilder()
          .setMCType(Mockito.mock(ASTMCType.class))
          .addArcField("o", Mockito.mock(ASTExpression.class))
          .build()).build()).build();
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

  protected ASTComponentType getPortTestComponent() {
    return ArcBasisMill.componentTypeBuilder()
      .setName("Ports")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .setPortDeclarationList(Arrays.asList(
            ArcBasisMill.portDeclarationBuilder()
              .setPortDirection(ArcBasisMill.portDirectionInBuilder().build())
              .setMCType(Mockito.mock(ASTMCType.class))
              .setPortList("i1").build(),
            ArcBasisMill.portDeclarationBuilder()
              .setPortDirection(ArcBasisMill.portDirectionOutBuilder().build())
              .setMCType(Mockito.mock(ASTMCType.class))
              .setPortList("o1").build()))
          .build())
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .setPortDeclarationList(Arrays.asList(
            ArcBasisMill.portDeclarationBuilder()
              .setPortDirection(ArcBasisMill.portDirectionInBuilder().build())
              .setMCType(Mockito.mock(ASTMCType.class))
              .setPortList("i2", "i3").build(),
            ArcBasisMill.portDeclarationBuilder()
              .setPortDirection(ArcBasisMill.portDirectionOutBuilder().build())
              .setMCType(Mockito.mock(ASTMCType.class))
              .setPortList("o2", "o3").build()))
          .build())
        .build())
      .build();
  }
}