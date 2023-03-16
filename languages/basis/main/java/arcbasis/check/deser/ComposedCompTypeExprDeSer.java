/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Serializes and deserializes {@link CompTypeExpression}s from and to their Json encoding.
 * Implementations of this interface should compose different {@link CompTypeExpressionDeSer}s and delegate to them
 * for the real (de-)serialization.
 */
public interface ComposedCompTypeExprDeSer {

  /**
   * @param toSerialize {@link CompTypeExpression} to serialize as Json
   * @return Json encoded version of the {@link CompTypeExpression}.
   */
  String serializeAsJson(@NotNull CompTypeExpression toSerialize);

  /**
   * Deserialize a {@link CompTypeExpression} from its Json encoding.
   */
  default CompTypeExpression deserializeFromJsonString(@NotNull String serializedInJson) {
    JsonObject compExpr = JsonParser.parseJsonObject(serializedInJson);
    return deserialize(compExpr);
  }

  /**
   * Deserialize a {@link CompTypeExpression} from its Json representation.
   */
  CompTypeExpression deserialize(@NotNull JsonObject serialized);

  default IllegalStateException missingDeSerException(@NotNull JsonObject unloadableElement) {
    Preconditions.checkNotNull(unloadableElement);

    String typeExprKind = JsonDeSers.getKind(unloadableElement);
    String deSerAggregatorName = this.getClass().getName();

    return new IllegalStateException(
      String.format("No DeSer available for CompTypeExpressionKind '%s' in '%s'. Therefore, the " +
        "deserialization of '%s' is impossible.",
      typeExprKind, deSerAggregatorName, unloadableElement
    ));
  }

  default IllegalStateException missingDeSerException(@NotNull CompTypeExpression unsaveableElement) {
    Preconditions.checkNotNull(unsaveableElement);

    String typeExpressionKind = unsaveableElement.getClass().getName();
    String deSerAggregatorName = this.getClass().getName();

    return new IllegalStateException(
      String.format("No DeSer available for CompTypeExpressionKind '%s' in '%s'. Therefore, the " +
        "serialization of '%s' is impossible.",
      typeExpressionKind, deSerAggregatorName, unsaveableElement.printName()
    ));
  }

  default IllegalStateException missingDeSerException(@NotNull String compTypeExpressionKind) {
    Preconditions.checkNotNull(compTypeExpressionKind);

    String deSerAggregatorName = this.getClass().getName();

    return new IllegalStateException(
      String.format("No DeSer available for CompTypeExpressionKind '%s' in '%s'.",
      compTypeExpressionKind, deSerAggregatorName
    ));
  }
}
