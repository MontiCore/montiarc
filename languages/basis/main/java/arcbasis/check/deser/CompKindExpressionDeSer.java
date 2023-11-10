/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Serializes and deserializes {@link CompKindExpression}s to and from their Json encoding.
 * This interface should be implemented separately for the different subtypes of {@link CompKindExpression}.
 * These separate implementations should be composed into an implementation of {@link ComposedCompTypeExprDeSer}.
 * @param <T> the {@link CompKindExpression} that this class (de-)serializes.
 */
public interface CompKindExpressionDeSer<T extends CompKindExpression> {

  String serializeAsJson(@NotNull T toSerialize);

  T deserialize(@NotNull JsonObject serialized);
}
