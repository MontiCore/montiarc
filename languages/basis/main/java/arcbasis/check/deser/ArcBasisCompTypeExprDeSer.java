/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Composed DeSerializator of {@link CompKindExpression}s for the ArcBasis language.
 */
public class ArcBasisCompTypeExprDeSer implements FullCompKindExprDeSer {
  protected TypeExprOfComponentDeSer componentExprDeSer;

  public ArcBasisCompTypeExprDeSer() {
    componentExprDeSer = new TypeExprOfComponentDeSer();
  }

  @Override
  public String serializeAsJson(@NotNull CompKindExpression toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    if (toSerialize instanceof TypeExprOfComponent) {
      return componentExprDeSer.serializeAsJson((TypeExprOfComponent) toSerialize);
    } else {
      throw missingDeSerException(toSerialize);
    }
  }

  @Override
  public CompKindExpression deserialize(@NotNull JsonElement serialized) {
    Preconditions.checkNotNull(serialized);

    if (JsonDeSers.getKind(serialized.getAsJsonObject()).equals(TypeExprOfComponentDeSer.SERIALIZED_KIND)) {
      return componentExprDeSer.deserialize(serialized.getAsJsonObject());
    }
    throw missingDeSerException(serialized.getAsJsonObject());
  }
}
