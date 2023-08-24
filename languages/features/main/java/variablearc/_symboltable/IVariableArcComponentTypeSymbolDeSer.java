/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisSymbols2Json;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSetDeSer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IVariableArcComponentTypeSymbolDeSer {

  String FEATURES = "features";
  String CONSTRAINTS = "constraints";
  String VARIATION_POINTS = "variationPoints";

  ExpressionSetDeSer getExpressionSetDeSer();

  VariableArcVariationPointDeSer getVariationPointDeSer();

  /**
   * @param component the component that is serialized
   * @param s2j       the json printer that the arcFeatures are serialized to
   */
  default void serializeArcFeatures(@NotNull IVariableArcComponentTypeSymbol component, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(s2j);
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(FEATURES);
    ((IVariableArcScope) component.getTypeInfo().getSpannedScope()).getLocalArcFeatureSymbols().forEach(f -> f.accept(s2j.getTraverser()));
    printer.endArray();
  }

  /**
   * @param component the component which owns the arcFeatures.
   * @param json      the component which owns the arcFeatures, encoded as JSON.
   */
  default void deserializeArcFeatures(@NotNull IVariableArcComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);

    final String featureSerializeKind = ArcFeatureSymbol.class.getCanonicalName();

    List<JsonElement> features = json.getArrayMemberOpt(FEATURES).orElseGet(Collections::emptyList);

    for (JsonElement feature : features) {
      String featureJsonKind = JsonDeSers.getKind(feature.getAsJsonObject());
      if (featureJsonKind.equals(featureSerializeKind)) {
        ISymbolDeSer<?, ?> deSer = ArcBasisMill.globalScope().getSymbolDeSer(featureSerializeKind);
        ArcFeatureSymbol featureSym = (ArcFeatureSymbol) deSer.deserialize(feature.getAsJsonObject());

        ((IVariableArcScope) component.getTypeInfo().getSpannedScope()).add(featureSym);

      } else {
        Log.error(String.format(
          "Could not deserialize port '%s' of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          feature.getAsJsonObject().getStringMember(JsonDeSers.NAME),
          component.getTypeInfo().getName(),
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
  default void serializeConstraints(@NotNull IVariableArcComponentTypeSymbol component, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(s2j);
    if (component.getLocalConstraints().isEmpty()) return;

    JsonPrinter printer = s2j.getJsonPrinter();

    printer.memberJson(CONSTRAINTS, getExpressionSetDeSer().serialize(component.getLocalConstraints()));
  }

  /**
   * @param component the component which owns the constraints.
   * @param json      the component which owns the constraints, encoded as JSON.
   */
  default void deserializeConstraints(@NotNull IVariableArcComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);
    final String expressionSetSerializeKind = ExpressionSet.class.getCanonicalName();

    Optional<JsonObject> constraints = json.getObjectMemberOpt(CONSTRAINTS);

    if (constraints.isPresent()) {
      String expressionSetJsonKind = JsonDeSers.getKind(constraints.get());
      if (expressionSetJsonKind.equals(expressionSetSerializeKind)) {
        ExpressionSet expressionSet = getExpressionSetDeSer().deserialize(constraints.get(), component.getTypeInfo().getSpannedScope());

        component.setLocalConstraints(expressionSet);

      } else {
        Log.error(String.format(
          "Could not deserialize constraints of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          component.getTypeInfo().getName(),
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
  default void serializeVariationPoint(@NotNull IVariableArcComponentTypeSymbol component, @NotNull ArcBasisSymbols2Json s2j) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(s2j);
    JsonPrinter printer = s2j.getJsonPrinter();

    printer.beginArray(VARIATION_POINTS);
    component.getAllVariationPoints().stream().filter(vp -> vp.getDependsOn().isEmpty()).forEach(vp -> getVariationPointDeSer().serialize(vp, s2j));
    printer.endArray();
  }

  /**
   * @param component the component which owns the variation points.
   * @param json      the component which owns the variation points, encoded as JSON.
   */
  default void deserializeVariationPoints(@NotNull IVariableArcComponentTypeSymbol component, @NotNull JsonObject json) {
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(json);
    final String variationPointSerializeKind = getVariationPointDeSer().getSerializedKind();

    List<JsonElement> variationPoints = json.getArrayMemberOpt(VARIATION_POINTS).orElseGet(Collections::emptyList);

    for (JsonElement variationPoint : variationPoints) {
      String variationPointJsonKind = JsonDeSers.getKind(variationPoint.getAsJsonObject());
      if (variationPointJsonKind.equals(variationPointSerializeKind)) {
        getVariationPointDeSer().deserialize(component, variationPoint.getAsJsonObject());
      } else {
        Log.error(String.format(
          "Could not deserialize variation point of component '%s', " +
            "as it is of kind '%s'. However, we only know how to deserialize '%s'",
          component.getTypeInfo().getName(),
          variationPointJsonKind,
          variationPointSerializeKind
        ));
      }
    }
  }
}
