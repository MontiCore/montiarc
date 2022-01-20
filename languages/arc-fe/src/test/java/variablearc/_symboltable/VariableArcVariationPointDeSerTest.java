/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;

import java.io.IOException;
import java.util.Optional;

public class VariableArcVariationPointDeSerTest extends AbstractTest {

  protected VariableArcVariationPointDeSer variationPointDeSer;

  protected ASTExpression parsedCondition;

  @BeforeEach
  public void SetUpDeSer() {
    this.variationPointDeSer = new VariableArcVariationPointDeSer(this::mockParse, this::mockPrint,
      this::mockDeserializeSymbol);
    this.parsedCondition = Mockito.mock(ASTExpression.class);
  }

  private Optional<ASTExpression> mockParse(String s) throws IOException {
    return Optional.of(parsedCondition);
  }

  private String mockPrint(ASTExpression expression) {
    return "mock";
  }

  private ISymbol mockDeserializeSymbol(IScope scope, JsonObject jsonObject) {
    return Mockito.mock(ISymbol.class);
  }

  /**
   * @return the test subject
   */
  public VariableArcVariationPointDeSer getVariationPointDeSer() {
    return this.variationPointDeSer;
  }

  @Test
  public void shouldDeSerWithoutErrorSimple() {
    VariableArcSymbols2Json s2j = new VariableArcSymbols2Json();
    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(parsedCondition);
    IVariableArcScope scope = VariableArcMill.scope();

    this.getVariationPointDeSer().serialize(variationPoint, s2j.getJsonPrinter(), s2j.getTraverser());
    VariableArcVariationPoint deVariationPoint =
      this.getVariationPointDeSer().deserialize(JsonParser.parseJsonObject(s2j.getSerializedString()), scope);

    Assertions.assertTrue(deVariationPoint.deepEquals(variationPoint));
  }

  @Test
  public void shouldDeSerWithoutErrorComplex() {
    VariableArcSymbols2Json s2j = new VariableArcSymbols2Json();
    VariableArcVariationPoint parent = new VariableArcVariationPoint(parsedCondition);
    VariableArcVariationPoint child1 = new VariableArcVariationPoint(parsedCondition, Optional.of(parent));
    VariableArcVariationPoint child2 = new VariableArcVariationPoint(parsedCondition, Optional.of(parent));
    VariableArcVariationPoint child12 = new VariableArcVariationPoint(parsedCondition, Optional.of(child1));
    IVariableArcScope scope = VariableArcMill.scope();

    this.getVariationPointDeSer().serialize(parent, s2j.getJsonPrinter(), s2j.getTraverser());
    VariableArcVariationPoint deParent = this.getVariationPointDeSer()
      .deserialize(JsonParser.parseJsonObject(s2j.getSerializedString()), scope);

    Assertions.assertTrue(deParent.deepEquals(parent));
  }
}
