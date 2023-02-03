/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import arcbasis.check.deser.TypeExprOfComponentDeSer;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Composed DeSerializator of {@link CompTypeExpression}s for the GenericArc language.
 */
public class GenericArcCompTypeExprDeSer implements ComposedCompTypeExprDeSer {

  protected TypeExprOfComponentDeSer componentExprDeSer;

  protected TypeExprOfGenericComponentDeSer genericComponentExprDeSer;

  public GenericArcCompTypeExprDeSer() {
    componentExprDeSer = new TypeExprOfComponentDeSer();
    genericComponentExprDeSer = new TypeExprOfGenericComponentDeSer();
  }

  @Override
  public String serializeAsJson(@NotNull CompTypeExpression toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    if (toSerialize instanceof TypeExprOfComponent) {
      return componentExprDeSer.serializeAsJson((TypeExprOfComponent) toSerialize);
    } else if (toSerialize instanceof TypeExprOfGenericComponent) {
      return genericComponentExprDeSer.serializeAsJson((TypeExprOfGenericComponent) toSerialize);
    } else {
      logMissingDeSer(toSerialize);
      throw new IllegalStateException();
    }
  }

  @Override
  public CompTypeExpression deserialize(JsonObject serialized) {
    Preconditions.checkNotNull(serialized);

    switch (JsonDeSers.getKind(serialized)) {
      case TypeExprOfComponentDeSer.SERIALIZED_KIND: return componentExprDeSer.deserialize(serialized);
      case TypeExprOfGenericComponentDeSer.SERIALIZED_KIND: return genericComponentExprDeSer.deserialize(serialized);
      default:
        logMissingDeSer(serialized);
        throw new IllegalStateException();
    }
  }
}
