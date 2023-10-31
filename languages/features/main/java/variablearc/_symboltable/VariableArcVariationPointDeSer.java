/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisSymbols2Json;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc.VariableArcMill;
import arcbasis._symboltable.TransitiveNameExpressionScopeSetter;
import variablearc._symboltable.util.ScopeAddSymbolVisitor;
import variablearc._visitor.VariableArcTraverser;
import variablearc.evaluation.expressions.Expression;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class VariableArcVariationPointDeSer {

  protected final Function<String, Optional<ASTExpression>> parseExpression;

  public static final String SERIALIZED_KIND = "variablearc._symboltable.VariableArcVariationPoint";
  public static final String EXPRESSION = "expression";
  public static final String CHILD_VARIATION_POINTS = "childVariationPoints";
  public static final String SYMBOLS = "symbols";

  public VariableArcVariationPointDeSer(@NotNull Function<String, Optional<ASTExpression>> parseExpression) {
    Preconditions.checkNotNull(parseExpression);
    this.parseExpression = parseExpression;
  }

  /**
   * @param toSerialize the variation point that is serialized
   * @param s2j         the printer this variation point is serialized to
   */
  public void serialize(@NotNull VariableArcVariationPoint toSerialize, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(toSerialize);
    Preconditions.checkNotNull(s2j);

    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginObject();
    printer.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
    printer.member(EXPRESSION, toSerialize.getCondition().print());
    printer.beginArray(CHILD_VARIATION_POINTS);
    toSerialize.getChildVariationPoints().forEach(e -> serialize(e, s2j));
    printer.endArray();

    printer.beginArray(SYMBOLS);
    toSerialize.getSymbols().forEach(s -> s.accept(s2j.getTraverser()));
    printer.endArray();

    printer.endObject();
  }

  /**
   * @param component the component which owns the variation points.
   * @param json      the component which owns the variation points, encoded as JSON.
   * @return the deserialized variation point
   */
  public VariableArcVariationPoint deserialize(@NotNull IVariableArcComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);
    return deserialize(component, json, null);
  }

  /**
   * @param component the component which owns the variation points.
   * @param json      the component which owns the variation points, encoded as JSON.
   * @param parent    the parent variation point of the variation point that is serialized (can be null)
   * @return the deserialized variation point
   */
  public VariableArcVariationPoint deserialize(@NotNull IVariableArcComponentTypeSymbol component, @NotNull JsonObject json, @Nullable VariableArcVariationPoint parent) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);

    String expressionJson = json.getStringMember(EXPRESSION);
    Optional<ASTExpression> expression = parseExpression.apply(expressionJson);
    TransitiveNameExpressionScopeSetter scopeSetter = new TransitiveNameExpressionScopeSetter(component.getTypeInfo().getSpannedScope());
    expression.ifPresent(scopeSetter::set);

    if (expression.isEmpty()) {
      Log.error(String.format(
        "Could not deserialize variation point expression '%s' of component '%s'",
        expressionJson,
        component.getTypeInfo().getName()
      ));
      return null;
    }

    VariableArcVariationPoint variationPoint = new VariableArcVariationPoint(new Expression(expression.get()), parent);
    component.add(variationPoint);

    List<JsonElement> variationPoints = json.getArrayMemberOpt(CHILD_VARIATION_POINTS).orElseGet(Collections::emptyList);
    variationPoints.forEach(vp -> deserialize(component, vp.getAsJsonObject(), variationPoint));

    List<JsonElement> symbols = json.getArrayMemberOpt(SYMBOLS).orElseGet(Collections::emptyList);
    symbols.forEach(symbolJson -> deserializeSymbol(component, variationPoint, symbolJson));

    return variationPoint;
  }

  /**
   * @param component      the component which owns the variation points and symbols.
   * @param variationPoint the variation point this symbol is part of.
   * @param json           the symbol encoded as json.
   */
  protected void deserializeSymbol(@NotNull IVariableArcComponentTypeSymbol component, @NotNull VariableArcVariationPoint variationPoint, @NotNull JsonElement json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(variationPoint);
    Preconditions.checkNotNull(json);

    String symbolKind = JsonDeSers.getKind(json.getAsJsonObject());
    ISymbolDeSer<?, ?> deSer = VariableArcMill.globalScope().getSymbolDeSer(symbolKind);
    ISymbol symbol = deSer.deserialize(json.getAsJsonObject());

    ScopeAddSymbolVisitor visitor = new ScopeAddSymbolVisitor((IVariableArcScope) component.getTypeInfo().getSpannedScope());
    VariableArcTraverser traverser = VariableArcMill.traverser();
    traverser.add4ArcBasis(visitor);

    symbol.accept(traverser);

    variationPoint.add(symbol);
  }

  public String getSerializedKind() {
    return SERIALIZED_KIND;
  }
}
