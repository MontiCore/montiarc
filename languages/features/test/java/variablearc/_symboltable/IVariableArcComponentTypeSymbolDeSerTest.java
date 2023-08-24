/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ArcBasisSymbols2Json;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolDeSer;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcAbstractTest;
import variablearc.VariableArcMill;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSetDeSer;
import variablearc.evaluation.expressions.Expression;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Tests for {@link IVariableArcComponentTypeSymbolDeSer}
 */
public class IVariableArcComponentTypeSymbolDeSerTest extends VariableArcAbstractTest {

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
    IVariableArcComponentTypeSymbol comp = createSimpleComp();
    ArcFeatureSymbol featureSymbol = VariableArcMill.arcFeatureSymbolBuilder()
      .setName("f1")
      .build();

    ((IVariableArcScope) comp.getTypeInfo().getSpannedScope()).add(featureSymbol);

    ComponentTypeSymbolDeSer deser = new VariableArcComponentTypeSymbolDeSer((s) -> Optional.empty());
    ArcBasisSymbols2Json arc2json = (ArcBasisSymbols2Json) (new VariableArcSymbols2Json()).getTraverser().getArcBasisVisitorList().get(0);

    // When
    String createdJson = deser.serialize(comp.getTypeInfo(), arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_ARC_FEATURES, createdJson);
  }

  @Test
  public void shouldDeserializeArcFeatures() {
    // Given
    ComponentTypeSymbolDeSer deser = new VariableArcComponentTypeSymbolDeSer((s) -> Optional.empty());

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
    IVariableArcComponentTypeSymbol comp = createSimpleComp();
    comp.setLocalConstraints(new ExpressionSet(List.of(
      new Expression(createNameExpressionInScope("f1", comp.getTypeInfo().getSpannedScope()))
    )));


    ComponentTypeSymbolDeSer deser = new VariableArcComponentTypeSymbolDeSer((s) -> Optional.empty());
    ArcBasisSymbols2Json arc2json = (ArcBasisSymbols2Json) (new VariableArcSymbols2Json()).getTraverser().getArcBasisVisitorList().get(0);

    // When
    String createdJson = deser.serialize(comp.getTypeInfo(), arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_CONSTRAINTS, createdJson);
  }

  @Test
  public void shouldDeserializeConstraints() {
    // Given
    ComponentTypeSymbolDeSer deser = new VariableArcComponentTypeSymbolDeSer((s) -> Optional.of(VariableArcMill.nameExpressionBuilder().setName(s).build()));

    // When
    IVariableArcComponentTypeSymbol comp = (IVariableArcComponentTypeSymbol) deser.deserialize(JSON_WITH_CONSTRAINTS);

    // Then
    Assertions.assertEquals(1, comp.getConstraints().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("f1", comp.getLocalConstraints().getExpressions().get(0).print())
    );
  }

  @Test
  public void shouldSerializeVariationPoints() {
    // Given
    IVariableArcComponentTypeSymbol comp = createSimpleComp();
    VariableArcVariationPoint vp1 = new VariableArcVariationPoint(new Expression(createNameExpressionInScope("f1", comp.getTypeInfo().getSpannedScope())));
    VariableArcVariationPoint vp2 = new VariableArcVariationPoint(new Expression(createNameExpressionInScope("f2", comp.getTypeInfo().getSpannedScope())));
    VariableArcVariationPoint vp2vp1 = new VariableArcVariationPoint(new Expression(createNameExpressionInScope("f3", comp.getTypeInfo().getSpannedScope())), vp2);

    comp.add(vp1);
    comp.add(vp2);
    comp.add(vp2vp1);

    ComponentTypeSymbolDeSer deser = new VariableArcComponentTypeSymbolDeSer((s) -> Optional.of(VariableArcMill.nameExpressionBuilder().setName(s).build()));
    ArcBasisSymbols2Json arc2json = (ArcBasisSymbols2Json) (new VariableArcSymbols2Json()).getTraverser().getArcBasisVisitorList().get(0);

    // When
    String createdJson = deser.serialize(comp.getTypeInfo(), arc2json);

    // Then
    Assertions.assertEquals(JSON_WITH_VARIATION_POINTS_AND_VARIANTS, createdJson);
  }

  @Test
  public void shouldDeserializeVariationPoints() {
    // Given
    ComponentTypeSymbolDeSer deser = new VariableArcComponentTypeSymbolDeSer((s) -> Optional.of(VariableArcMill.nameExpressionBuilder().setName(s).build()));

    // When
    IVariableArcComponentTypeSymbol comp = (IVariableArcComponentTypeSymbol) deser.deserialize(JSON_WITH_VARIATION_POINTS_AND_VARIANTS);

    // Then
    Assertions.assertEquals(3, comp.getAllVariationPoints().size());
    Assertions.assertAll(
      () -> Assertions.assertEquals("f1", comp.getAllVariationPoints().get(0).getCondition().print()),
      () -> Assertions.assertEquals("f2", comp.getAllVariationPoints().get(1).getCondition().print()),
      () -> Assertions.assertEquals(1, comp.getAllVariationPoints().get(1).getChildVariationPoints().size()),
      () -> Assertions.assertEquals("f3", comp.getAllVariationPoints().get(2).getCondition().print()),
      () -> Assertions.assertTrue(comp.getAllVariationPoints().get(2).getDependsOn().isPresent())
    );
    Assertions.assertEquals(8, comp.getVariableArcVariants().size());
  }

  protected static IVariableArcComponentTypeSymbol createSimpleComp() {
    IVariableArcComponentTypeSymbol symbol = (IVariableArcComponentTypeSymbol) VariableArcMill.componentTypeSymbolBuilder()
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

  /**
   * Helper class that implements the interface under test
   */
  public static class VariableArcComponentTypeSymbolDeSer extends ComponentTypeSymbolDeSer implements IVariableArcComponentTypeSymbolDeSer {

    protected final ExpressionSetDeSer expressionSetDeSer;
    protected final VariableArcVariationPointDeSer variationPointDeSer;

    public VariableArcComponentTypeSymbolDeSer(@NotNull Function<String, Optional<ASTExpression>> parseExpression) {
      expressionSetDeSer = new ExpressionSetDeSer(parseExpression);
      variationPointDeSer = new VariableArcVariationPointDeSer(parseExpression);
    }

    @Override
    protected void serializeAddons(@NotNull ComponentTypeSymbol toSerialize, @NotNull ArcBasisSymbols2Json s2j) {
      Preconditions.checkNotNull(toSerialize);
      Preconditions.checkNotNull(s2j);
      if (toSerialize instanceof IVariableArcComponentTypeSymbol) {
        serializeArcFeatures((IVariableArcComponentTypeSymbol) toSerialize, s2j);
        serializeConstraints((IVariableArcComponentTypeSymbol) toSerialize, s2j);
        serializeVariationPoint((IVariableArcComponentTypeSymbol) toSerialize, s2j);
      }
      super.serializeAddons(toSerialize, s2j);
    }

    @Override
    protected void deserializeAddons(@NotNull ComponentTypeSymbol component, @NotNull JsonObject json) {
      super.deserializeAddons(component, json);
      if (component instanceof IVariableArcComponentTypeSymbol) {
        deserializeArcFeatures((IVariableArcComponentTypeSymbol) component, json);
        deserializeConstraints((IVariableArcComponentTypeSymbol) component, json);
        deserializeVariationPoints((IVariableArcComponentTypeSymbol) component, json);
      }
    }

    @Override
    public ExpressionSetDeSer getExpressionSetDeSer() {
      return expressionSetDeSer;
    }

    @Override
    public VariableArcVariationPointDeSer getVariationPointDeSer() {
      return variationPointDeSer;
    }
  }
}
