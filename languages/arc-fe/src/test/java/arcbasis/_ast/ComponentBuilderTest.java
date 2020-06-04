/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import montiarc.AbstractTest;
import arcbasis.util.ArcError;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Holds the tests for the handwritten methods of {@link ASTComponentTypeBuilder}.
 */
public class ComponentBuilderTest extends AbstractTest {

  protected ASTComponentTypeBuilder builder;

  @BeforeEach
  public void setUpBuilder() {
    this.builder = new ASTComponentTypeBuilder();
    this.builder.setName("Components")
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .setComponentInstanceList("a", "b", "c");
  }

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest
  @ValueSource(strings = { "comp1" })
  public void shouldAddGivenInstance(String instance) {
    List<String> expectedInstanceList = this.getInstanceList(this.builder);
    expectedInstanceList.add(instance);
    ASTComponentType ast = this.builder.addInstance(instance).build();
    List<String> actualInstanceList = this.getInstanceList(ast);
    Assertions.assertEquals(expectedInstanceList.size(), ast.getComponentInstanceList().size());
    Assertions.assertEquals(expectedInstanceList, actualInstanceList);
  }

  @ParameterizedTest
  @MethodSource("indexAndInstanceProvider")
  public void shouldSetGivenInstance(int index, String instance) {
    List<String> expectedInstanceList = this.getInstanceList(this.builder);
    expectedInstanceList.set(index, instance);
    ASTComponentType ast = this.builder.setInstance(index, instance).build();
    List<String> actualInstanceList = this.getInstanceList(ast);
    Assertions.assertEquals(ast.getComponentInstance(index).getName(), instance);
    Assertions.assertEquals(expectedInstanceList.size(), ast.getComponentInstanceList().size());
    Assertions.assertEquals(expectedInstanceList, actualInstanceList);
  }

  @ParameterizedTest
  @MethodSource("indexAndInstanceProvider")
  public void shouldAddGivenInstance(int index, String instance) {
    List<String> expectedInstanceList = this.getInstanceList(this.builder);
    expectedInstanceList.add(index, instance);
    ASTComponentType ast = this.builder.addInstance(index, instance).build();
    List<String> actualInstanceList = this.getInstanceList(ast);
    Assertions.assertEquals(ast.getComponentInstance(index).getName(), instance);
    Assertions.assertEquals(expectedInstanceList.size(), ast.getComponentInstanceList().size());
    Assertions.assertEquals(expectedInstanceList, actualInstanceList);
  }

  static Stream<Arguments> indexAndInstanceProvider() {
    return Stream.of(Arguments.of(0, "comp1"), Arguments.of(1, "comp2"), Arguments.of(2, "comp3"));
  }

  @ParameterizedTest
  @MethodSource("instancesProvider")
  public void shouldSetGivenInstances(String[] instances) {
    List<String> expectedInstanceList = Arrays.asList(instances);
    ASTComponentType ast = this.builder.setComponentInstanceList(instances).build();
    List<String> actualInstanceList = this.getInstanceList(ast);
    Assertions.assertEquals(expectedInstanceList.size(), ast.getComponentInstanceList().size());
    Assertions.assertEquals(expectedInstanceList, actualInstanceList);
  }

  @ParameterizedTest
  @MethodSource("instancesProvider")
  public void shouldAddGivenInstances(String[] instances) {
    List<String> expectedInstanceList = this.getInstanceList(this.builder);
    expectedInstanceList.addAll(Arrays.asList(instances));
    ASTComponentType ast = this.builder.addAllInstances(instances).build();
    List<String> actualInstanceList = this.getInstanceList(ast);
    Assertions.assertEquals(expectedInstanceList.size(), ast.getComponentInstanceList().size());
    Assertions.assertEquals(expectedInstanceList, actualInstanceList);
  }

  static Stream<Arguments> instancesProvider() {
    return Stream.of(Arguments.of((Object) new String[] {}),
      Arguments.of((Object) new String[] { "comp" }),
      Arguments.of((Object) new String[] { "comp1", "comp2", "comp3" }));
  }

  @ParameterizedTest
  @MethodSource("indexAndInstancesProvider")
  public void shouldAddGivenInstances(int index, String[] instances) {
    List<String> expectedInstanceList = this.getInstanceList(this.builder);
    expectedInstanceList.addAll(index, Arrays.asList(instances));
    ASTComponentType ast = this.builder.addAllInstances(index, instances).build();
    List<String> actualInstanceList = this.getInstanceList(ast);
    Assertions.assertEquals(expectedInstanceList.size(), ast.getComponentInstanceList().size());
    Assertions.assertEquals(expectedInstanceList, actualInstanceList);
  }

  static Stream<Arguments> indexAndInstancesProvider() {
    return Stream.of(Arguments.of(0, new String[] {}),
      Arguments.of(3, new String[] { "comp1" }),
      (Arguments.of(0, new String[] { "comp1", "comp2", "comp3" })));
  }

  protected List<String> getInstanceList(ASTComponentTypeBuilder builder) {
    return builder.getComponentInstanceList().stream()
      .map(ASTComponentInstance::getName)
      .collect(Collectors.toList());
  }

  protected List<String> getInstanceList(ASTComponentType ast) {
    return ast.getComponentInstanceList().stream().map(ASTComponentInstance::getName)
      .collect(Collectors.toList());
  }
}