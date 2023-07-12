/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import de.monticore.symboltable.serialization.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc._symboltable.IVariableArcScope;
import variablearc.evaluation.expressions.Expression;

import java.util.List;
import java.util.Optional;

/**
 * Tests for {@link ExpressionSetDeSer}
 */
public class ExpressionSetDeSerTest extends VariableArcAbstractTest {

  protected static final String JSON_SIMPLE_EXPRESSION_SET =
    "{" +
      "\"kind\":\"variablearc.evaluation.ExpressionSet\"," +
      "\"expressions\":[{\"expression\":\"a\"}]," +
      "\"negatedConjunction\":[[{\"expression\":\"b\"},{\"expression\":\"c\"}],[{\"expression\":\"d\"}]]" +
      "}";

  @Test
  public void shouldSerializeArcFeatures() {
    // Given
    ExpressionSet expressionSet = new ExpressionSet(
      List.of(new Expression(VariableArcMill.nameExpressionBuilder().setName("a").build())),
      List.of(
        List.of(new Expression(VariableArcMill.nameExpressionBuilder().setName("b").build()), new Expression(VariableArcMill.nameExpressionBuilder().setName("c").build())),
        List.of(new Expression(VariableArcMill.nameExpressionBuilder().setName("d").build()))
      )
    );

    ExpressionSetDeSer deser = new ExpressionSetDeSer((s) -> Optional.empty());

    // When
    String createdJson = deser.serialize(expressionSet);

    // Then
    Assertions.assertEquals(JSON_SIMPLE_EXPRESSION_SET, createdJson);
  }

  @Test
  public void shouldDeserializeArcFeatures() {
    // Given
    ExpressionSetDeSer deser = new ExpressionSetDeSer((s) -> Optional.of(VariableArcMill.nameExpressionBuilder().setName(s).build()));
    IVariableArcScope scope = VariableArcMill.scope();

    // When
    ExpressionSet expressionSet = deser.deserialize(JsonParser.parse(JSON_SIMPLE_EXPRESSION_SET).getAsJsonObject(), scope);

    // Then
    Assertions.assertEquals(3, expressionSet.size());
    Assertions.assertEquals(1, expressionSet.getExpressions().size());
    Assertions.assertEquals(2, expressionSet.getNegatedConjunctions().size());
    Assertions.assertEquals(2, expressionSet.getNegatedConjunctions().get(0).size());
    Assertions.assertEquals(1, expressionSet.getNegatedConjunctions().get(1).size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("a", expressionSet.getExpressions().get(0).print()),
      () -> Assertions.assertEquals(scope, expressionSet.getExpressions().get(0).getAstExpression().getEnclosingScope()),
      () -> Assertions.assertEquals("b", expressionSet.getNegatedConjunctions().get(0).get(0).print()),
      () -> Assertions.assertEquals("c", expressionSet.getNegatedConjunctions().get(0).get(1).print()),
      () -> Assertions.assertEquals("d", expressionSet.getNegatedConjunctions().get(1).get(0).print())
    );
  }

}
