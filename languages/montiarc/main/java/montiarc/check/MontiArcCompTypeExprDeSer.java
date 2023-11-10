/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.TypeExprOfComponent;
import arcbasis.check.deser.TypeExprOfComponentDeSer;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import genericarc.check.TypeExprOfGenericComponent;
import genericarc.check.TypeExprOfGenericComponentDeSer;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcCompTypeExprDeSer implements FullCompKindExprDeSer {

  protected TypeExprOfComponentDeSer simpleCompExprDeSer;

  protected TypeExprOfGenericComponentDeSer genericCompExprDeSer;

  public MontiArcCompTypeExprDeSer() {
    simpleCompExprDeSer = new TypeExprOfComponentDeSer();
    genericCompExprDeSer = new TypeExprOfGenericComponentDeSer();
  }

  @Override
  public String serializeAsJson(@NotNull CompKindExpression toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    if (toSerialize instanceof TypeExprOfComponent) {
      return simpleCompExprDeSer.serializeAsJson((TypeExprOfComponent) toSerialize);
    } else if (toSerialize instanceof TypeExprOfGenericComponent) {
      return genericCompExprDeSer.serializeAsJson((TypeExprOfGenericComponent) toSerialize);
    } else {
      throw missingDeSerException(toSerialize);
    }
  }

  @Override
  public CompKindExpression deserialize(JsonElement serialized) {
    Preconditions.checkNotNull(serialized);

    switch (JsonDeSers.getKind(serialized.getAsJsonObject())) {
      case TypeExprOfComponentDeSer.SERIALIZED_KIND: return simpleCompExprDeSer.deserialize(serialized.getAsJsonObject());
      case TypeExprOfGenericComponentDeSer.SERIALIZED_KIND: return genericCompExprDeSer.deserialize(serialized.getAsJsonObject());
      default:
        throw missingDeSerException(serialized.getAsJsonObject());
    }
  }
}
