/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;

import java.util.ArrayList;
import java.util.List;

public class VariableArcDeSerTest extends AbstractTest {

  protected VariableArcDeSer variableArcDeSer;

  @BeforeEach
  public void SetUpDeSer() {
    this.variableArcDeSer = new VariableArcDeSer();
  }

  /**
   * @return the test subject
   */
  public VariableArcDeSer getVariableArcDeSer() {
    return this.variableArcDeSer;
  }

  @Test
  public void shouldDeSerAddonsWithoutError() {
    VariableArcSymbols2Json s2j = new VariableArcSymbols2Json();
    IVariableArcScope scope = VariableArcMill.scope();

    VariableArcVariationPoint parent = new VariableArcVariationPoint(Mockito.mock(ASTExpression.class));
    scope.add(parent);

    s2j.getJsonPrinter().beginObject();
    this.getVariableArcDeSer().serialize(scope, s2j);
    s2j.getJsonPrinter().beginArray(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS);

    s2j.getJsonPrinter().endArray();
    this.getVariableArcDeSer().serializeAddons(scope, s2j);
    s2j.getJsonPrinter().endObject();

    List<VariableArcVariationPoint> variationPoints = new ArrayList<>(scope.getRootVariationPoints());

    scope.getRootVariationPoints().clear();
    VariableArcMill.globalScope().clear();

    IVariableArcScope deScope = this.getVariableArcDeSer().deserializeScope(de.monticore.symboltable.serialization.JsonParser.parseJsonObject(s2j.getSerializedString()));

    Assertions.assertEquals(variationPoints.size(), deScope.getRootVariationPoints().size());
  }
}
