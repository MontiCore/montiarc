/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.check.CompTypeExpression;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Serializes and deserializes {@link CompTypeExpression}s to and from their Json encoding.
 * This interface should be implemented separately for the different subtypes of {@link CompTypeExpression}.
 * These separate implementations should be composed into an implementation of {@link ComposedCompTypeExprDeSer}.
 * @param <T> the {@link CompTypeExpression} that this class (de-)serializes.
 */
public interface CompTypeExpressionDeSer<T extends CompTypeExpression> {

  String serializeAsJson(@NotNull T toSerialize);

  T deserialize(@NotNull JsonObject serialized);
}
