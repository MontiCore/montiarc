/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.ArcBasisAbstractTest;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
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
 * Holds the tests for the handwritten methods of {@link ASTArcFieldDeclarationBuilder}.
 */
public class FieldDeclarationBuilderTest extends ArcBasisAbstractTest {

  protected ASTArcFieldDeclarationBuilder builder;

  @BeforeEach
  public void setUpBuilder() {
    this.builder = new ASTArcFieldDeclarationBuilder();
    this.builder.setMCType(Mockito.mock(ASTMCType.class))
      .setArcFieldList(new String[] { "a", "b", "c" },
        new ASTExpression[] { Mockito.mock(ASTExpression.class), Mockito.mock(ASTExpression.class),
          Mockito.mock(ASTExpression.class) });
  }

  @ParameterizedTest
  @ValueSource(strings = { "i1" })
  public void shouldAddGivenField(String name) {
    List<String> expectedFieldList = this.getFieldList(this.builder);
    expectedFieldList.add(name);
    ASTArcFieldDeclaration ast =
      this.builder.addArcField(name, Mockito.mock(ASTExpression.class)).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(expectedFieldList.size(), ast.getArcFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  @ParameterizedTest
  @MethodSource("indexAndFieldProvider")
  public void shouldSetGivenField(int index, String name) {
    List<String> expectedFieldList = this.getFieldList(this.builder);
    expectedFieldList.set(index, name);
    ASTArcFieldDeclaration ast =
      this.builder.setArcFieldList(index, name, Mockito.mock(ASTExpression.class)).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(ast.getArcField(index).getName(), name);
    Assertions.assertEquals(expectedFieldList.size(), ast.getArcFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  @ParameterizedTest
  @MethodSource("indexAndFieldProvider")
  public void shouldAddGivenField(int index, String field) {
    List<String> expectedFieldList = this.getFieldList(this.builder);
    expectedFieldList.add(index, field);
    ASTArcFieldDeclaration ast =
      this.builder.addArcField(index, field, Mockito.mock(ASTExpression.class)).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(ast.getArcField(index).getName(), field);
    Assertions.assertEquals(expectedFieldList.size(), ast.getArcFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  static Stream<Arguments> indexAndFieldProvider() {
    return Stream.of(Arguments.of(0, "v1"), Arguments.of(1, "v2"), Arguments.of(2, "v3"));
  }

  @ParameterizedTest
  @MethodSource("fieldsProvider")
  public void shouldSetGivenFields(String[] fields) {
    List<String> expectedFieldList = Arrays.asList(fields);
    ASTArcFieldDeclaration ast =
      this.builder.setArcFieldList(fields, this.mockValues(fields.length)).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(expectedFieldList.size(), ast.getArcFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  @ParameterizedTest
  @MethodSource("fieldsProvider")
  public void shouldAddGivenFields(String[] fields) {
    List<String> expectedFieldList = this.getFieldList(this.builder);
    expectedFieldList.addAll(Arrays.asList(fields));
    ASTArcFieldDeclaration ast =
      this.builder.addAllArcFields(fields, this.mockValues(fields.length)).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(expectedFieldList.size(), ast.getArcFieldList().size());
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
    ASTArcFieldDeclaration ast =
      this.builder.addAllArcFields(index, fields, mockValues(fields.length)).build();
    List<String> actualFieldList = this.getFieldList(ast);
    Assertions.assertEquals(expectedFieldList.size(), ast.getArcFieldList().size());
    Assertions.assertEquals(expectedFieldList, actualFieldList);
  }

  static Stream<Arguments> indexAndFieldsProvider() {
    return Stream.of(Arguments.of(0, new String[] {}),
      Arguments.of(3, new String[] { "v1" }),
      Arguments.of(0, new String[] { "v1", "v2", "v3" }));
  }

  protected List<String> getFieldList(ASTArcFieldDeclarationBuilder builder) {
    return builder.getArcFieldList().stream().map(ASTArcField::getName)
      .collect(Collectors.toList());
  }

  protected List<String> getFieldList(ASTArcFieldDeclaration ast) {
    return ast.getArcFieldList().stream().map(ASTArcField::getName).collect(Collectors.toList());
  }

  protected ASTExpression[] mockValues(int length) {
    ASTExpression[] values = new ASTExpression[length];
    for (int i = 0; i < length; i++) {
      values[i] = Mockito.mock(ASTExpression.class);
    }
    return values;
  }
}