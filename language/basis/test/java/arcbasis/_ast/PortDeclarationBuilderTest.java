/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds the tests for the handwritten methods of {@link ASTPortDeclarationBuilder}.
 */
public class PortDeclarationBuilderTest extends AbstractTest {

  protected ASTPortDeclarationBuilder builder;

  @BeforeEach
  public void setUpBuilder() {
    this.builder = new ASTPortDeclarationBuilder();
    this.builder.setMCType(Mockito.mock(ASTMCType.class))
        .setPortDirection(ArcBasisMill.portDirectionInBuilder().build())
        .setPortList("a", "b", "c");
  }

  @ParameterizedTest
  @ValueSource(strings = { "i1" })
  public void shouldAddGivenPort(String port) {
    List<String> expectedPortList = this.getPortsList(this.builder);
    expectedPortList.add(port);
    ASTPortDeclaration ast = this.builder.addPort(port).build();
    List<String> actualPortList = this.getPortsList(ast);
    Assertions.assertEquals(expectedPortList.size(), ast.getPortList().size());
    Assertions.assertEquals(expectedPortList, actualPortList);
  }

  @ParameterizedTest
  @MethodSource("indexAndPortProvider")
  public void shouldSetGivenPort(int index, String port) {
    List<String> expectedPortList = this.getPortsList(this.builder);
    expectedPortList.set(index, port);
    ASTPortDeclaration ast = this.builder.setPort(index, port).build();
    List<String> actualPortList = this.getPortsList(ast);
    Assertions.assertEquals(ast.getPort(index).getName(), port);
    Assertions.assertEquals(expectedPortList.size(), ast.getPortList().size());
    Assertions.assertEquals(expectedPortList, actualPortList);
  }

  @ParameterizedTest
  @MethodSource("indexAndPortProvider")
  public void shouldAddGivenPort(int index, String port) {
    List<String> expectedPortList = this.getPortsList(this.builder);
    expectedPortList.add(index, port);
    ASTPortDeclaration ast = this.builder.addPort(index, port).build();
    List<String> actualPortList = this.getPortsList(ast);
    Assertions.assertEquals(ast.getPort(index).getName(), port);
    Assertions.assertEquals(expectedPortList.size(), ast.getPortList().size());
    Assertions.assertEquals(expectedPortList, actualPortList);
  }

  static Stream<Arguments> indexAndPortProvider() {
    return Stream.of(Arguments.of(0, "i1"), Arguments.of(1, "i2"), Arguments.of(2, "i3"));
  }

  @ParameterizedTest
  @MethodSource("portsProvider")
  public void shouldSetGivenPorts(String[] ports) {
    List<String> expectedPortList = Arrays.asList(ports);
    ASTPortDeclaration ast = this.builder.setPortList(ports).build();
    List<String> actualPortList = this.getPortsList(ast);
    Assertions.assertEquals(expectedPortList.size(), ast.getPortList().size());
    Assertions.assertEquals(expectedPortList, actualPortList);
  }

  @ParameterizedTest
  @MethodSource("portsProvider")
  public void shouldAddGivenPorts(String[] ports) {
    List<String> expectedPortList = this.getPortsList(this.builder);
    expectedPortList.addAll(Arrays.asList(ports));
    ASTPortDeclaration ast = this.builder.addAllPorts(ports).build();
    List<String> actualPortList = this.getPortsList(ast);
    Assertions.assertEquals(expectedPortList.size(), ast.getPortList().size());
    Assertions.assertEquals(expectedPortList, actualPortList);
  }

  static Stream<Arguments> portsProvider() {
    return Stream.of(Arguments.of((Object) new String[] {}),
        Arguments.of((Object) new String[] { "i" }),
        Arguments.of((Object) new String[] { "i1", "i2", "i3" }));
  }

  @ParameterizedTest
  @MethodSource("indexAndPortsProvider")
  public void shouldAddGivenPorts(int index, String[] ports) {
    List<String> expectedPortList = this.getPortsList(this.builder);
    expectedPortList.addAll(index, Arrays.asList(ports));
    ASTPortDeclaration ast = this.builder.addAllPorts(index, ports).build();
    List<String> actualPortList = this.getPortsList(ast);
    Assertions.assertEquals(expectedPortList.size(), ast.getPortList().size());
    Assertions.assertEquals(expectedPortList, actualPortList);
  }

  static Stream<Arguments> indexAndPortsProvider() {
    return Stream.of(Arguments.of(0, new String[] { "i1", "i2", "i3" }),
        Arguments.of(0, new String[] {}),
        Arguments.of(2, new String[] { "o1", "o2", "o3" }),
        Arguments.of(3, new String[] { "i1" }));
  }

  protected List<String> getPortsList(ASTPortDeclarationBuilder builder) {
    return builder.getPortList().stream().map(ASTPort::getName).collect(Collectors.toList());
  }

  protected List<String> getPortsList(ASTPortDeclaration ast) {
    return ast.getPortList().stream().map(ASTPort::getName).collect(Collectors.toList());
  }
}