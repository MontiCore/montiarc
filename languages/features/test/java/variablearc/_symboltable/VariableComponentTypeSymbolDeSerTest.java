/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ArcBasisSymbols2Json;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolDeSer;
import arcbasis._symboltable.IArcBasisScope;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tests for {@link VariableComponentTypeSymbolDeSer}
 */
public class VariableComponentTypeSymbolDeSerTest extends VariableArcAbstractTest {

  protected static final String JSON_WITH_ARC_FEATURES =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"features\":[{\"kind\":\"variablearc._symboltable.ArcFeatureSymbol\",\"name\":\"f1\"}]" +
      "}";

  protected static final String JSON_WITH_CONSTRAINTS =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"constraints\":{\"kind\":\"variablearc.evaluation.ExpressionSet\",\"expressions\":[{\"expression\":\"f1\"}]}" +
      "}";

  protected static final String JSON_WITH_VARIATION_POINTS_AND_VARIANTS =
    "{" +
      "\"kind\":\"arcbasis._symboltable.ComponentTypeSymbol\"," +
      "\"name\":\"Comp\"," +
      "\"variationPoints\":[" +
      "{\"kind\":\"variablearc._symboltable.VariableArcVariationPoint\",\"expression\":\"f1\"}," +
      "{\"kind\":\"variablearc._symboltable.VariableArcVariationPoint\",\"expression\":\"f2\",\"childVariationPoints\":[{\"kind\":\"variablearc._symboltable.VariableArcVariationPoint\",\"expression\":\"f3\"}]}]" +
      "}";

  @Test
  public void shouldSerializeArcFeatures() {
    // Given
    ComponentTypeSymbol comp = createSimpleComp();
    ArcFeatureSymbol featureSymbol = VariableArcMill.arcFeatureSymbolBuilder()
      .setName("f1")
      .build();

    ((IVariableArcScope) comp.getSpannedScope()).add(featureSymbol);

    ComponentTypeSymbolDeSer deser = new VariableComponentTypeSymbolDeSer((s) -> Optional.empty());
    ArcBasisSymbols2Json arc2json = (ArcBasisSymbols2Json) (new VariableArcSymbols2Json()).getTraverser().getArcBasisVisitorList().get(0);

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_ARC_FEATURES, createdJson);
  }

  @Test
  public void shouldDeserializeArcFeatures() {
    // Given
    ComponentTypeSymbolDeSer deser = new VariableComponentTypeSymbolDeSer((s) -> Optional.empty());

    // When
    ComponentTypeSymbol comp = deser.deserialize(JSON_WITH_ARC_FEATURES);

    // Then
    Assertions.assertEquals(1, ((IVariableArcScope) comp.getSpannedScope()).getArcFeatureSymbols().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("f1", ((IVariableArcScope) comp.getSpannedScope()).getArcFeatureSymbols().values().get(0).getName())
    );
  }

  @Test
  public void shouldSerializeConstraints() {
    // Given
    VariableComponentTypeSymbol comp = createSimpleComp();
    comp.setConstraints(new ExpressionSet(List.of(
      new Expression(createNameExpressionInScope("f1", comp.getSpannedScope()))
    )));


    ComponentTypeSymbolDeSer deser = new VariableComponentTypeSymbolDeSer((s) -> Optional.empty());
    ArcBasisSymbols2Json arc2json = (ArcBasisSymbols2Json) (new VariableArcSymbols2Json()).getTraverser().getArcBasisVisitorList().get(0);

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_CONSTRAINTS, createdJson);
  }

  @Test
  public void shouldDeserializeConstraints() {
    // Given
    ComponentTypeSymbolDeSer deser = new VariableComponentTypeSymbolDeSer((s) -> Optional.of(VariableArcMill.nameExpressionBuilder().setName(s).build()));

    // When
    VariableComponentTypeSymbol comp = (VariableComponentTypeSymbol) deser.deserialize(JSON_WITH_CONSTRAINTS);

    // Then
    Assertions.assertEquals(1, comp.getConditions().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("f1", comp.getConstraints().getExpressions().get(0).print())
    );
  }

  @Test
  public void shouldSerializeVariationPoints() {
    // Given
    VariableComponentTypeSymbol comp = createSimpleComp();
    VariableArcVariationPoint vp1 = new VariableArcVariationPoint(new Expression(createNameExpressionInScope("f1", comp.getSpannedScope())));
    VariableArcVariationPoint vp2 = new VariableArcVariationPoint(new Expression(createNameExpressionInScope("f2", comp.getSpannedScope())));
    VariableArcVariationPoint vp2vp1 = new VariableArcVariationPoint(new Expression(createNameExpressionInScope("f3", comp.getSpannedScope())), vp2);

    comp.add(vp1);
    comp.add(vp2);
    comp.add(vp2vp1);

    ComponentTypeSymbolDeSer deser = new VariableComponentTypeSymbolDeSer((s) -> Optional.of(VariableArcMill.nameExpressionBuilder().setName(s).build()));
    ArcBasisSymbols2Json arc2json = (ArcBasisSymbols2Json) (new VariableArcSymbols2Json()).getTraverser().getArcBasisVisitorList().get(0);

    // When
    String createdJson = deser.serialize(comp, arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_VARIATION_POINTS_AND_VARIANTS, createdJson);
  }

  @Test
  public void shouldDeserializeVariationPoints() {
    // Given
    ComponentTypeSymbolDeSer deser = new VariableComponentTypeSymbolDeSer((s) -> Optional.of(VariableArcMill.nameExpressionBuilder().setName(s).build()));

    // When
    VariableComponentTypeSymbol comp = (VariableComponentTypeSymbol) deser.deserialize(JSON_WITH_VARIATION_POINTS_AND_VARIANTS);

    // Then
    Assertions.assertEquals(3, comp.getAllVariationPoints().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("f1", comp.getAllVariationPoints().get(0).getCondition().print()),
      () -> Assertions.assertEquals("f2", comp.getAllVariationPoints().get(1).getCondition().print()),
      () -> Assertions.assertEquals(1, comp.getAllVariationPoints().get(1).getChildVariationPoints().size()),
      () -> Assertions.assertEquals("f3", comp.getAllVariationPoints().get(2).getCondition().print()),
      () -> Assertions.assertTrue(comp.getAllVariationPoints().get(2).getDependsOn().isPresent())
    );
    Assertions.assertEquals(8, comp.getVariants().size());
  }

  protected static VariableComponentTypeSymbol createSimpleComp() {
    VariableComponentTypeSymbol symbol = (VariableComponentTypeSymbol) VariableArcMill.componentTypeSymbolBuilder()
      .setName("Comp")
      .setSpannedScope(VariableArcMill.scope())
      .build();
    return symbol;
  }

  protected ASTNameExpression createNameExpressionInScope(String name, IArcBasisScope scope) {
    ASTNameExpression expr = VariableArcMill.nameExpressionBuilder().setName(name).build();
    expr.setEnclosingScope(scope);
    return expr;
  }
}
