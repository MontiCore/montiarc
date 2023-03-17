/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Composed DeSerializator of {@link CompTypeExpression}s for the ArcBasis language.
 */
public class ArcBasisCompTypeExprDeSer implements ComposedCompTypeExprDeSer {
  protected TypeExprOfComponentDeSer componentExprDeSer;

  public ArcBasisCompTypeExprDeSer() {
    componentExprDeSer = new TypeExprOfComponentDeSer();
  }

  public String serializeAsJson(@NotNull CompTypeExpression toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    if (toSerialize instanceof TypeExprOfComponent) {
      return componentExprDeSer.serializeAsJson((TypeExprOfComponent) toSerialize);
    } else {
      throw missingDeSerException(toSerialize);
    }
  }

  @Override
  public CompTypeExpression deserialize(@NotNull JsonObject serialized) {
    Preconditions.checkNotNull(serialized);

    if (JsonDeSers.getKind(serialized).equals(TypeExprOfComponentDeSer.SERIALIZED_KIND)) {
      return componentExprDeSer.deserialize(serialized);
    }
    throw missingDeSerException(serialized);
  }
}
