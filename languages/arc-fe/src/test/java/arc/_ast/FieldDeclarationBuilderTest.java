/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc.AbstractTest;
import arc.util.ArcError;

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
 * Holds the tests for the handwritten methods of {@link ASTArcFieldDeclarationBuilder}.
 */
public class FieldDeclarationBuilderTest extends AbstractTest {

  protected ASTArcFieldDeclarationBuilder builder;

  @BeforeEach
  public void setUpBuilder() {
    this.builder = new ASTArcFieldDeclarationBuilder();
    this.builder.setType(Mockito.mock(ASTMCType.class))
      .setFieldList("a", "b", "c");
  }

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest
  @ValueSource(strings = { "i1" })
  public void shouldAddGivenField(String field) {
    List<String> expectedFieldList = this.getFieldList(this.builder);
    expectedFieldList.add(field);
    ASTArcFieldDeclaration ast = this.builder.addField(field).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(expectedFieldList.size(), ast.getFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  @ParameterizedTest
  @MethodSource("indexAndFieldProvider")
  public void shouldSetGivenField(int index, String field) {
    List<String> expectedFieldList = this.getFieldList(this.builder);
    expectedFieldList.set(index, field);
    ASTArcFieldDeclaration ast = this.builder.setField(index, field).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(ast.getField(index).getName(), field);
    Assertions.assertEquals(expectedFieldList.size(), ast.getFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  @ParameterizedTest
  @MethodSource("indexAndFieldProvider")
  public void shouldAddGivenField(int index, String field) {
    List<String> expectedFieldList = this.getFieldList(this.builder);
    expectedFieldList.add(index, field);
    ASTArcFieldDeclaration ast = this.builder.addField(index, field).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(ast.getField(index).getName(), field);
    Assertions.assertEquals(expectedFieldList.size(), ast.getFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  static Stream<Arguments> indexAndFieldProvider() {
    return Stream.of(Arguments.of(0, "v1"), Arguments.of(1, "v2"), Arguments.of(2, "v3"));
  }

  @ParameterizedTest
  @MethodSource("fieldsProvider")
  public void shouldSetGivenFields(String[] fields) {
    List<String> expectedFieldList = Arrays.asList(fields);
    ASTArcFieldDeclaration ast = this.builder.setFieldList(fields).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(expectedFieldList.size(), ast.getFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  @ParameterizedTest
  @MethodSource("fieldsProvider")
  public void shouldAddGivenFields(String[] fields) {
    List<String> expectedFieldList = this.getFieldList(this.builder);
    expectedFieldList.addAll(Arrays.asList(fields));
    ASTArcFieldDeclaration ast = this.builder.addAllFields(fields).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(expectedFieldList.size(), ast.getFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  static Stream<Arguments> fieldsProvider() {
    return Stream.of(Arguments.of((Object) new String[] {}),
      Arguments.of((Object) new String[] { "v" }),
      Arguments.of((Object) new String[] { "v1", "v2", "v3" }));
  }

  @ParameterizedTest
  @MethodSource("indexAndFieldsProvider")
  public void shouldAddGivenFields(int index, String[] fields) {
    List<String> expectedFieldList = this.getFieldList(this.builder);
    expectedFieldList.addAll(index, Arrays.asList(fields));
    ASTArcFieldDeclaration ast = this.builder.addAllFields(index, fields).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(expectedFieldList.size(), ast.getFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  static Stream<Arguments> indexAndFieldsProvider() {
    return Stream.of(Arguments.of(0, new String[] {}),
      Arguments.of(3, new String[] { "v1" }),
      Arguments.of(0, new String[] { "v1", "v2", "v3" }));
  }

  protected List<String> getFieldList(ASTArcFieldDeclarationBuilder builder) {
    return builder.getFieldList().stream().map(ASTArcField::getName)
      .collect(Collectors.toList());
  }

  protected List<String> getFieldList(ASTArcFieldDeclaration ast) {
    return ast.getFieldList().stream().map(ASTArcField::getName).collect(Collectors.toList());
  }
}