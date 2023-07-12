/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisSymbols2Json;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolDeSer;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSetDeSer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VariableComponentTypeSymbolDeSer extends ComponentTypeSymbolDeSer {

  public static final String FEATURES = "features";
  public static final String CONSTRAINTS = "constraints";
  public static final String VARIATION_POINTS = "variationPoints";

  protected final ExpressionSetDeSer expressionSetDeSer;
  protected final VariableArcVariationPointDeSer variationPointDeSer;

  public VariableComponentTypeSymbolDeSer(@NotNull Function<String, Optional<ASTExpression>> parseExpression) {
    Preconditions.checkNotNull(parseExpression);
    expressionSetDeSer = new ExpressionSetDeSer(parseExpression);
    variationPointDeSer = new VariableArcVariationPointDeSer(parseExpression);
  }

  @Override
  protected void serializeAddons(@NotNull ComponentTypeSymbol toSerialize, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(toSerialize);
    Preconditions.checkNotNull(s2j);
    if (toSerialize instanceof VariableComponentTypeSymbol) {
      serializeArcFeatures((VariableComponentTypeSymbol) toSerialize, s2j);
      serializeConstraints((VariableComponentTypeSymbol) toSerialize, s2j);
      serializeVariationPoint((VariableComponentTypeSymbol) toSerialize, s2j);
    }
    super.serializeAddons(toSerialize, s2j);
  }

  @Override
  protected void deserializeAddons(@NotNull ComponentTypeSymbol component, @NotNull JsonObject json) {
    super.deserializeAddons(component, json);
    if (component instanceof VariableComponentTypeSymbol) {
      deserializeArcFeatures((VariableComponentTypeSymbol) component, json);
      deserializeConstraints((VariableComponentTypeSymbol) component, json);
      deserializeVariationPoints((VariableComponentTypeSymbol) component, json);
    }
  }

  /**
   * @param component the component that is serialized
   * @param s2j       the json printer that the arcFeatures are serialized to
   */
  protected void serializeArcFeatures(@NotNull VariableComponentTypeSymbol component, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(s2j);
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(FEATURES);
    ((IVariableArcScope) component.getSpannedScope()).getLocalArcFeatureSymbols().forEach(f -> f.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param component the component which owns the arcFeatures.
   * @param json      the component which owns the arcFeatures, encoded as JSON.
   */
  protected void deserializeArcFeatures(@NotNull VariableComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);

    final String featureSerializeKind = ArcFeatureSymbol.class.getCanonicalName();

    List<JsonElement> features = json.getArrayMemberOpt(FEATURES).orElseGet(Collections::emptyList);

    for (JsonElement feature : features) {
      String featureJsonKind = JsonDeSers.getKind(feature.getAsJsonObject());
      if (featureJsonKind.equals(featureSerializeKind)) {
        ISymbolDeSer<?, ?> deSer = ArcBasisMill.globalScope().getSymbolDeSer(featureSerializeKind);
        ArcFeatureSymbol featureSym = (ArcFeatureSymbol) deSer.deserialize(feature.getAsJsonObject());

        ((IVariableArcScope) component.getSpannedScope()).add(featureSym);

      } else {
        Log.error(String.format(
          "Could not deserialize port '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          feature.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          component.getName(),
          featureJsonKind,
          featureSerializeKind
        ));
      }
    }
  }

  /**
   * @param component the component that is serialized
   * @param s2j       the json printer that the constraints are serialized to
   */
  protected void serializeConstraints(@NotNull VariableComponentTypeSymbol component, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(s2j);
    if (component.getConstraints().isEmpty()) return;

    JsonPrinter printer = s2j.getJsonPrinter();

    printer.memberJson(CONSTRAINTS, expressionSetDeSer.serialize(component.getConstraints()));
  }

  /**
   * @param component the component which owns the constraints.
   * @param json      the component which owns the constraints, encoded as JSON.
   */
  protected void deserializeConstraints(@NotNull VariableComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);
    final String expressionSetSerializeKind = ExpressionSet.class.getCanonicalName();

    Optional<JsonObject> constraints = json.getObjectMemberOpt(CONSTRAINTS);

    if (constraints.isPresent()) {
      String expressionSetJsonKind = JsonDeSers.getKind(constraints.get());
      if (expressionSetJsonKind.equals(expressionSetSerializeKind)) {
        ExpressionSet expressionSet = expressionSetDeSer.deserialize(constraints.get(), component.getSpannedScope());

        component.setConstraints(expressionSet);

      } else {
        Log.error(String.format(
          "Could not deserialize constraints of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          component.getName(),
          expressionSetJsonKind,
          expressionSetSerializeKind
        ));
      }
    }
  }

  /**
   * @param component the component that is serialized
   * @param s2j       the json printer that the variation points are serialized to
   */
  protected void serializeVariationPoint(@NotNull VariableComponentTypeSymbol component, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(s2j);
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(VARIATION_POINTS);
    component.getAllVariationPoints().stream().filter(vp -> vp.getDependsOn().isEmpty()).forEach(vp -> variationPointDeSer.serialize(vp, s2j));
    printer.endArray();
  }

  /**
   * @param component the component which owns the variation points.
   * @param json      the component which owns the variation points, encoded as JSON.
   */
  protected void deserializeVariationPoints(@NotNull VariableComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);
    final String variationPointSerializeKind = variationPointDeSer.getSerializedKind();

    List<JsonElement> variationPoints = json.getArrayMemberOpt(VARIATION_POINTS).orElseGet(Collections::emptyList);

    for (JsonElement variationPoint : variationPoints) {
      String variationPointJsonKind = JsonDeSers.getKind(variationPoint.getAsJsonObject());
      if (variationPointJsonKind.equals(variationPointSerializeKind)) {
        variationPointDeSer.deserialize(component, variationPoint.getAsJsonObject());
      } else {
        Log.error(String.format(
          "Could not deserialize variation point of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          component.getName(),
          variationPointJsonKind,
          variationPointSerializeKind
        ));
      }
    }
  }
}
