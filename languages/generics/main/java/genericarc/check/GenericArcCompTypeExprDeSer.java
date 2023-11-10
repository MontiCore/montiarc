/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.check.TypeExprOfComponent;
import arcbasis.check.deser.TypeExprOfComponentDeSer;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Composed DeSerializator of {@link CompKindExpression}s for the GenericArc language.
 */
public class GenericArcCompTypeExprDeSer implements FullCompKindExprDeSer {

  protected TypeExprOfComponentDeSer componentExprDeSer;

  protected TypeExprOfGenericComponentDeSer genericComponentExprDeSer;

  public GenericArcCompTypeExprDeSer() {
    componentExprDeSer = new TypeExprOfComponentDeSer();
    genericComponentExprDeSer = new TypeExprOfGenericComponentDeSer();
  }

  @Override
  public String serializeAsJson(@NotNull CompKindExpression toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    if (toSerialize instanceof TypeExprOfComponent) {
      return componentExprDeSer.serializeAsJson((TypeExprOfComponent) toSerialize);
    } else if (toSerialize instanceof TypeExprOfGenericComponent) {
      return genericComponentExprDeSer.serializeAsJson((TypeExprOfGenericComponent) toSerialize);
    } else {
      throw missingDeSerException(toSerialize);
    }
  }

  @Override
  public CompKindExpression deserialize(JsonElement serialized) {
    Preconditions.checkNotNull(serialized);

    switch (JsonDeSers.getKind(serialized.getAsJsonObject())) {
      case TypeExprOfComponentDeSer.SERIALIZED_KIND: return componentExprDeSer.deserialize(serialized.getAsJsonObject());
      case TypeExprOfGenericComponentDeSer.SERIALIZED_KIND: return genericComponentExprDeSer.deserialize(serialized.getAsJsonObject());
      default:
        throw missingDeSerException(serialized.getAsJsonObject());
    }
  }
}
