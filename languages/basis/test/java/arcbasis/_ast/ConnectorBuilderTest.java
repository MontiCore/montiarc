/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds the tests for the handwritten methods of {@link ASTConnectorBuilder}.
 */
public class ConnectorBuilderTest extends AbstractTest {

  protected ASTConnectorBuilder builder;

  @BeforeEach
  public void setUpBuilder() {
    this.builder = new ASTConnectorBuilder();
    this.builder.setSource("a")
        .setTargetList("b", "comp.b");
  }

  @ParameterizedTest
  @ValueSource(strings = { "i1", "comp.i2" })
  public void shouldSetGivenSource(String source) {
    ASTConnector ast = this.builder.setSource(source).build();
    Assertions.assertEquals(source, ast.getSource().getQName());
  }

  @ParameterizedTest
  @ValueSource(strings = { "i1", "comp.i2" })
  public void shouldAddGivenTarget(String target) {
    List<String> expectedTargetList = this.getTargetList(this.builder);
    expectedTargetList.add(target);
    ASTConnector ast = this.builder.addTarget(target).build();
    List<String> actualTargetList = this.getTargetList(ast);
    Assertions.assertEquals(expectedTargetList.size(), ast.getTargetList().size());
    Assertions.assertEquals(expectedTargetList, actualTargetList);
  }

  @ParameterizedTest
  @MethodSource("indexAndTargetProvider")
  public void shouldSetGivenTarget(int index, String target) {
    List<String> expectedTargetList = this.getTargetList(this.builder);
    expectedTargetList.set(index, target);
    ASTConnector ast = this.builder.setTarget(index, target).build();
    List<String> actualTargetList = this.getTargetList(ast);
    Assertions.assertEquals(ast.getTarget(index).getQName(), target);
    Assertions.assertEquals(expectedTargetList.size(), ast.getTargetList().size());
    Assertions.assertEquals(expectedTargetList, actualTargetList);
  }

  @ParameterizedTest
  @MethodSource("indexAndTargetProvider")
  public void shouldAddGivenTarget(int index, String target) {
    List<String> expectedTargetList = this.getTargetList(this.builder);
    expectedTargetList.add(index, target);
    ASTConnector ast = this.builder.addTarget(index, target).build();
    List<String> actualTargetList = this.getTargetList(ast);
    Assertions.assertEquals(ast.getTarget(index).getQName(), target);
    Assertions.assertEquals(expectedTargetList.size(), ast.getTargetList().size());
    Assertions.assertEquals(expectedTargetList, actualTargetList);
  }

  static Stream<Arguments> indexAndTargetProvider() {
    return Stream.of(Arguments.of(0, "i1"), Arguments.of(1, "comp.i2"));
  }

  @ParameterizedTest
  @MethodSource("targetsProvider")
  public void shouldSetGivenTargets(String[] targets) {
    List<String> expectedTargetList = Arrays.asList(targets);
    ASTConnector ast = this.builder.setTargetList(targets).build();
    List<String> actualTargetList = this.getTargetList(ast);
    Assertions.assertEquals(expectedTargetList.size(), ast.getTargetList().size());
    Assertions.assertEquals(expectedTargetList, actualTargetList);
  }

  @ParameterizedTest
  @MethodSource("targetsProvider")
  public void shouldAddGivenTargets(String[] targets) {
    List<String> expectedTargetList = this.getTargetList(this.builder);
    expectedTargetList.addAll(Arrays.asList(targets));
    ASTConnector ast = this.builder.addAllTargets(targets).build();
    List<String> actualTargetList = this.getTargetList(ast);
    Assertions.assertEquals(expectedTargetList.size(), ast.getTargetList().size());
    Assertions.assertEquals(expectedTargetList, actualTargetList);
  }

  static Stream<Arguments> targetsProvider() {
    return Stream.of(Arguments.of((Object) new String[] {}),
        Arguments.of((Object) new String[] { "i1" }),
        Arguments.of((Object) new String[] { "comp.i1" }),
        Arguments.of((Object) new String[] { "i1", "comp1.i2", "comp1.i3" }));
  }

  @ParameterizedTest
  @MethodSource("indexAndTargetsProvider")
  public void shouldAddGivenTargets(int index, String[] targets) {
    List<String> expectedTargetList = this.getTargetList(this.builder);
    expectedTargetList.addAll(index, Arrays.asList(targets));
    ASTConnector ast = this.builder.addAllTargets(index, targets).build();
    List<String> actualTargetList = this.getTargetList(ast);
    Assertions.assertEquals(expectedTargetList.size(), ast.getTargetList().size());
    Assertions.assertEquals(expectedTargetList, actualTargetList);
  }

  static Stream<Arguments> indexAndTargetsProvider() {
    return Stream.of(Arguments.of(0, new String[] { "i1", "comp1.i2", "comp1.i3" }),
        Arguments.of(0, new String[] {}),
        Arguments.of(1, new String[] { "i1" }),
        Arguments.of(2, new String[] { "comp2.o1", "o2", "o3" }));
  }

  protected List<String> getTargetList(ASTConnectorBuilder builder) {
    return builder.getTargetList().stream().map(port -> port.getQName())
        .collect(Collectors.toList());
  }

  protected List<String> getTargetList(ASTConnector ast) {
    return ast.getTargetList().stream().map(port -> port.getQName())
        .collect(Collectors.toList());
  }
}