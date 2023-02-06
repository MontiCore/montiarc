/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import arcbasis.check.deser.TypeExprOfComponentDeSer;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;
import genericarc.check.TypeExprOfGenericComponent;
import genericarc.check.TypeExprOfGenericComponentDeSer;
import org.codehaus.commons.nullanalysis.NotNull;
// import variablearc.check.TypeExprOfVariableComponent;

public class MontiArcCompTypeExprDeSer implements ComposedCompTypeExprDeSer {

  protected TypeExprOfComponentDeSer simpleCompExprDeSer;

  protected TypeExprOfGenericComponentDeSer genericCompExprDeSer;

  // TODO: integrate VariableArcDeSer when implemented
  // protected TypeExprOfVariableComponent variableCompExprDeSer;

  public MontiArcCompTypeExprDeSer() {
    simpleCompExprDeSer = new TypeExprOfComponentDeSer();
    genericCompExprDeSer = new TypeExprOfGenericComponentDeSer();
  }

  @Override
  public String serializeAsJson(@NotNull CompTypeExpression toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    if (toSerialize instanceof TypeExprOfComponent) {
      return simpleCompExprDeSer.serializeAsJson((TypeExprOfComponent) toSerialize);
    } else if (toSerialize instanceof TypeExprOfGenericComponent) {
      return genericCompExprDeSer.serializeAsJson((TypeExprOfGenericComponent) toSerialize);
    } /* else if (toSerialize instanceof TypeExprOfVariableComponent) {
      return variableCompExprDeSer.serializeAsJson((TypeExprOfVariableComponent) toSerialize);
    } */ else {
      throw missingDeSerException(toSerialize);
    }
  }

  @Override
  public CompTypeExpression deserialize(JsonObject serialized) {
    Preconditions.checkNotNull(serialized);

    switch (JsonDeSers.getKind(serialized)) {
      case TypeExprOfComponentDeSer.SERIALIZED_KIND: return simpleCompExprDeSer.deserialize(serialized);
      case TypeExprOfGenericComponentDeSer.SERIALIZED_KIND: return genericCompExprDeSer.deserialize(serialized);
      // case TypeExprOfVariableComponent.SERIALIZED_KIND: return variableCompExprDeSer.deserialize(serialized);
      default:
        throw missingDeSerException(serialized);
    }
  }
}
