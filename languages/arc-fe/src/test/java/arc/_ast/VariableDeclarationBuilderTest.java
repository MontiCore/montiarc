/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc.AbstractTest;
import montiarc.util.ArcError;

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
import org.mockito.Mockito;

/**
 * Holds the tests for the handwritten methods of {@link ASTArcVariableDeclarationBuilder}.
 */
public class VariableDeclarationBuilderTest extends AbstractTest {

  protected ASTArcVariableDeclarationBuilder builder;

  @BeforeEach
  public void setUpBuilder() {
    this.builder = new ASTArcVariableDeclarationBuilder();
    this.builder.setType(Mockito.mock(ASTMCType.class))
      .setVariableList("a", "b", "c");
  }

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest
  @ValueSource(strings = { "i1" })
  public void shouldAddGivenVariable(String variable) {
    List<String> expectedVariableList = this.getVariableList(this.builder);
    expectedVariableList.add(variable);
    ASTArcVariableDeclaration ast = this.builder.addVariable(variable).build();
    List<String> actualVariableList = this.getVariableList(ast);
    Assertions.assertEquals(expectedVariableList.size(), ast.getVariableList().size());
    Assertions.assertEquals(expectedVariableList, actualVariableList);
  }

  @ParameterizedTest
  @MethodSource("indexAndVariableProvider")
  public void shouldSetGivenVariable(int index, String variable) {
    List<String> expectedVariableList = this.getVariableList(this.builder);
    expectedVariableList.set(index, variable);
    ASTArcVariableDeclaration ast = this.builder.setVariable(index, variable).build();
    List<String> actualVariableList = this.getVariableList(ast);
    Assertions.assertEquals(ast.getVariable(index).getName(), variable);
    Assertions.assertEquals(expectedVariableList.size(), ast.getVariableList().size());
    Assertions.assertEquals(expectedVariableList, actualVariableList);
  }

  @ParameterizedTest
  @MethodSource("indexAndVariableProvider")
  public void shouldAddGivenVariable(int index, String variable) {
    List<String> expectedVariableList = this.getVariableList(this.builder);
    expectedVariableList.add(index, variable);
    ASTArcVariableDeclaration ast = this.builder.addVariable(index, variable).build();
    List<String> actualVariableList = this.getVariableList(ast);
    Assertions.assertEquals(ast.getVariable(index).getName(), variable);
    Assertions.assertEquals(expectedVariableList.size(), ast.getVariableList().size());
    Assertions.assertEquals(expectedVariableList, actualVariableList);
  }

  static Stream<Arguments> indexAndVariableProvider() {
    return Stream.of(Arguments.of(0, "v1"), Arguments.of(1, "v2"), Arguments.of(2, "v3"));
  }

  @ParameterizedTest
  @MethodSource("variablesProvider")
  public void shouldSetGivenVariables(String[] variables) {
    List<String> expectedVariableList = Arrays.asList(variables);
    ASTArcVariableDeclaration ast = this.builder.setVariableList(variables).build();
    List<String> actualVariableList = this.getVariableList(ast);
    Assertions.assertEquals(expectedVariableList.size(), ast.getVariableList().size());
    Assertions.assertEquals(expectedVariableList, actualVariableList);
  }

  @ParameterizedTest
  @MethodSource("variablesProvider")
  public void shouldAddGivenVariables(String[] variables) {
    List<String> expectedVariableList = this.getVariableList(this.builder);
    expectedVariableList.addAll(Arrays.asList(variables));
    ASTArcVariableDeclaration ast = this.builder.addAllVariables(variables).build();
    List<String> actualVariableList = this.getVariableList(ast);
    Assertions.assertEquals(expectedVariableList.size(), ast.getVariableList().size());
    Assertions.assertEquals(expectedVariableList, actualVariableList);
  }

  static Stream<Arguments> variablesProvider() {
    return Stream.of(Arguments.of((Object) new String[] {}),
      Arguments.of((Object) new String[] { "v" }),
      Arguments.of((Object) new String[] { "v1", "v2", "v3" }));
  }

  @ParameterizedTest
  @MethodSource("indexAndVariablesProvider")
  public void shouldAddGivenVariables(int index, String[] variables) {
    List<String> expectedVariableList = this.getVariableList(this.builder);
    expectedVariableList.addAll(index, Arrays.asList(variables));
    ASTArcVariableDeclaration ast = this.builder.addAllVariables(index, variables).build();
    List<String> actualVariableList = this.getVariableList(ast);
    Assertions.assertEquals(expectedVariableList.size(), ast.getVariableList().size());
    Assertions.assertEquals(expectedVariableList, actualVariableList);
  }

  static Stream<Arguments> indexAndVariablesProvider() {
    return Stream.of(Arguments.of(0, new String[] {}),
      Arguments.of(3, new String[] { "v1" }),
      Arguments.of(0, new String[] { "v1", "v2", "v3" }));
  }

  protected List<String> getVariableList(ASTArcVariableDeclarationBuilder builder) {
    return builder.getVariableList().stream().map(ASTArcVariable::getName)
      .collect(Collectors.toList());
  }

  protected List<String> getVariableList(ASTArcVariableDeclaration ast) {
    return ast.getVariableList().stream().map(ASTArcVariable::getName).collect(Collectors.toList());
  }
}