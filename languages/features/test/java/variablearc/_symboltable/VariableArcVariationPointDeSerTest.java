/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ArcBasisSymbols2Json;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc.evaluation.expressions.Expression;

import java.util.Optional;

/**
 * Tests for {@link VariableArcVariationPointDeSer}
 */
public class VariableArcVariationPointDeSerTest extends VariableArcAbstractTest {

  protected static final String JSON_EMPTY_VARIATION_POINT = "{\"kind\":\"variablearc._symboltable.VariableArcVariationPoint\",\"expression\":\"f1\"}";
  protected static final String JSON_NESTED_VARIATION_POINT = "{" +
    "\"kind\":\"variablearc._symboltable.VariableArcVariationPoint\"," +
    "\"expression\":\"f1\"," +
    "\"childVariationPoints\":[{\"kind\":\"variablearc._symboltable.VariableArcVariationPoint\",\"expression\":\"f2\"}]" +
    "}";
  protected static final String JSON_VARIATION_POINT_WITH_SYMBOL = "{" +
    "\"kind\":\"variablearc._symboltable.VariableArcVariationPoint\"," +
    "\"expression\":\"f1\"," +
    "\"symbols\":[{\"kind\":\"arcbasis._symboltable.ArcPortSymbol\",\"name\":\"p1\",\"type\":{\"kind\":\"de.monticore.types.check.SymTypePrimitive\",\"primitiveName\":\"int\"},\"outgoing\":true,\"timing\":\"untimed\"}]" +
    "}";

  @Test
  public void shouldSerializeEmptyVariationPoint() {
    // Given
    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(new Expression(VariableArcMill.nameExpressionBuilder().setName("f1").build()));

    VariableArcVariationPointDeSer deser = new VariableArcVariationPointDeSer((s) -> Optional.empty());
    ArcBasisSymbols2Json arc2json = (ArcBasisSymbols2Json) (new VariableArcSymbols2Json()).getTraverser().getArcBasisVisitorList().get(0);

    // When
    deser.serialize(variationPoint, arc2json);
    String createdJson = arc2json.getJsonPrinter().getContent();

    // Then
    Assertions.assertEquals(JSON_EMPTY_VARIATION_POINT, createdJson);
  }

  @Test
  public void shouldDeserializeEmptyVariationPoint() {
    // Given
    VariableArcVariationPointDeSer deser = new VariableArcVariationPointDeSer((s) -> Optional.of(VariableArcMill.nameExpressionBuilder().setName(s).build()));
    IVariableArcComponentTypeSymbol componentTypeSymbol = createSimpleComp();

    // When
    VariableArcVariationPoint variationPoint = deser.deserialize(componentTypeSymbol, JsonParser.parse(JSON_EMPTY_VARIATION_POINT).getAsJsonObject());

    // Then
    Assertions.assertEquals(0, variationPoint.getElements().size());
    Assertions.assertEquals(1, variationPoint.getAllConditions().size());
    Assertions.assertEquals(1, componentTypeSymbol.getAllVariationPoints().size());
    Assertions.assertEquals(variationPoint, componentTypeSymbol.getAllVariationPoints().get(0));
    Assertions.assertAll(
      () -> Assertions.assertEquals("f1", variationPoint.getCondition().print()),
      () -> Assertions.assertEquals(variationPoint.getCondition(), variationPoint.getAllConditions().get(0))
    );
  }

  @Test
  public void shouldSerializeNestedVariationPoint() {
    // Given
    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(new Expression(VariableArcMill.nameExpressionBuilder().setName("f1").build()));
    VariableArcVariationPoint childVariationPoint = new VariableArcVariationPoint(new Expression(VariableArcMill.nameExpressionBuilder().setName("f2").build()), variationPoint);

    VariableArcVariationPointDeSer deser = new VariableArcVariationPointDeSer((s) -> Optional.empty());
    ArcBasisSymbols2Json arc2json = (ArcBasisSymbols2Json) (new VariableArcSymbols2Json()).getTraverser().getArcBasisVisitorList().get(0);

    // When
    deser.serialize(variationPoint, arc2json);
    String createdJson = arc2json.getJsonPrinter().getContent();

    // Then
    Assertions.assertEquals(JSON_NESTED_VARIATION_POINT, createdJson);
  }

  @Test
  public void shouldDeserializeNestedVariationPoint() {
    // Given
    VariableArcVariationPointDeSer deser = new VariableArcVariationPointDeSer((s) -> Optional.of(VariableArcMill.nameExpressionBuilder().setName(s).build()));
    IVariableArcComponentTypeSymbol componentTypeSymbol = createSimpleComp();

    // When
    VariableArcVariationPoint variationPoint = deser.deserialize(componentTypeSymbol, JsonParser.parse(JSON_NESTED_VARIATION_POINT).getAsJsonObject());

    // Then
    Assertions.assertEquals(0, variationPoint.getElements().size());
    Assertions.assertEquals(1, variationPoint.getAllConditions().size());
    Assertions.assertEquals(2, componentTypeSymbol.getAllVariationPoints().size());
    Assertions.assertEquals(variationPoint, componentTypeSymbol.getAllVariationPoints().get(0));
    Assertions.assertEquals(1, variationPoint.getChildVariationPoints().size());

    VariableArcVariationPoint childVariationPoint = variationPoint.getChildVariationPoints().get(0);

    Assertions.assertEquals(childVariationPoint, componentTypeSymbol.getAllVariationPoints().get(1));
    Assertions.assertEquals(0, childVariationPoint.getElements().size());
    Assertions.assertEquals(2, childVariationPoint.getAllConditions().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("f1", variationPoint.getCondition().print()),
      () -> Assertions.assertEquals(variationPoint.getCondition(), variationPoint.getAllConditions().get(0)),
      () -> Assertions.assertEquals("f2", childVariationPoint.getCondition().print()),
      () -> Assertions.assertEquals(variationPoint.getCondition(), childVariationPoint.getAllConditions().get(0)),
      () -> Assertions.assertEquals(childVariationPoint.getCondition(), childVariationPoint.getAllConditions().get(1))
    );
  }

  @Test
  public void shouldSerializeVariationPointWithSymbol() {
    // Given
    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(new Expression(VariableArcMill.nameExpressionBuilder().setName("f1").build()));

    VariableArcVariationPointDeSer deser = new VariableArcVariationPointDeSer((s) -> Optional.empty());
    ArcBasisSymbols2Json arc2json = (ArcBasisSymbols2Json) (new VariableArcSymbols2Json()).getTraverser().getArcBasisVisitorList().get(0);

    variationPoint.add(VariableArcMill.arcPortSymbolBuilder().setName("p1").setOutgoing(true).setType(SymTypeExpressionFactory.createPrimitive("int")).build());

    // When
    deser.serialize(variationPoint, arc2json);
    String createdJson = arc2json.getJsonPrinter().getContent();

    // Then
    Assertions.assertEquals(JSON_VARIATION_POINT_WITH_SYMBOL, createdJson);
  }

  @Test
  public void shouldDeserializeVariationPointWithSymbol() {
    // Given
    VariableArcVariationPointDeSer deser = new VariableArcVariationPointDeSer((s) -> Optional.of(VariableArcMill.nameExpressionBuilder().setName(s).build()));
    IVariableArcComponentTypeSymbol componentTypeSymbol = createSimpleComp();

    // When
    VariableArcVariationPoint variationPoint = deser.deserialize(componentTypeSymbol, JsonParser.parse(JSON_VARIATION_POINT_WITH_SYMBOL).getAsJsonObject());

    // Then
    Assertions.assertEquals(0, variationPoint.getElements().size());
    Assertions.assertEquals(1, variationPoint.getAllConditions().size());
    Assertions.assertEquals(1, componentTypeSymbol.getAllVariationPoints().size());
    Assertions.assertEquals(variationPoint, componentTypeSymbol.getAllVariationPoints().get(0));
    Assertions.assertEquals(1, componentTypeSymbol.getTypeInfo().getArcPorts().size());
    Assertions.assertEquals(1, variationPoint.getSymbols().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("f1", variationPoint.getCondition().print()),
      () -> Assertions.assertEquals(variationPoint.getCondition(), variationPoint.getAllConditions().get(0)),
      () -> Assertions.assertEquals(variationPoint.getSymbols().get(0), componentTypeSymbol.getTypeInfo().getArcPorts().get(0))
    );
  }

  protected static IVariableArcComponentTypeSymbol createSimpleComp() {
    return (IVariableArcComponentTypeSymbol) VariableArcMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(VariableArcMill.scope())
      .build();
  }
}
