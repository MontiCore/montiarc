/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.check.TypeExprOfComponent;
import arcbasis.check.TypeExprOfGenericComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindExpressionDeSer;
import de.monticore.types.check.CompKindOfComponentTypeDeSer;
import de.monticore.types.check.CompKindOfGenericComponentTypeDeSer;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Composed DeSerializator of {@link CompKindExpression}s for the ArcBasis language.
 */
public class ArcBasisCompTypeExprDeSer extends CompKindExpressionDeSer {

  protected TypeExprOfComponentDeSer componentExprDeSer;

  protected TypeExprOfGenericComponentDeSer genericComponentExprDeSer;

  public ArcBasisCompTypeExprDeSer() {
    componentExprDeSer = new TypeExprOfComponentDeSer();
    genericComponentExprDeSer = new TypeExprOfGenericComponentDeSer();
  }

  @Override
  public String serialize(@NotNull CompKindExpression toSerialize) {
    Preconditions.checkNotNull(toSerialize);

    if (toSerialize instanceof TypeExprOfComponent) {
      return componentExprDeSer.serialize((TypeExprOfComponent) toSerialize);
    } else if (toSerialize instanceof TypeExprOfGenericComponent) {
      return genericComponentExprDeSer.serialize((TypeExprOfGenericComponent) toSerialize);
    } else if (toSerialize.isComponentType()) {
      return this.kindOfComponentDeSer.serialize(toSerialize.asComponentType());
    } else if (toSerialize.isGenericComponentType()) {
      return this.kindOfGenericComponentDeSer.serialize(toSerialize.asGenericComponentType());
    }

    throw this.missingDeSerException(toSerialize);
  }

  @Override
  public CompKindExpression deserialize(@NotNull ICompSymbolsScope scope, @NotNull JsonElement serialized) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(serialized);

    if (!serialized.isJsonObject()) {
      throw new IllegalArgumentException(serialized.toString());
    } else {
      JsonObject serializedCompExpr = serialized.getAsJsonObject();
      switch (JsonDeSers.getKind(serializedCompExpr)) {
        case CompKindOfGenericComponentTypeDeSer.SERIALIZED_KIND:
          return this.kindOfGenericComponentDeSer.deserialize(scope, serializedCompExpr);
        case CompKindOfComponentTypeDeSer.SERIALIZED_KIND:
          return this.kindOfComponentDeSer.deserialize(scope, serializedCompExpr);
        case TypeExprOfComponentDeSer.SERIALIZED_KIND:
          return componentExprDeSer.deserialize(scope, serialized.getAsJsonObject());
        case TypeExprOfGenericComponentDeSer.SERIALIZED_KIND:
          return genericComponentExprDeSer.deserialize(scope, serialized.getAsJsonObject());
      }

      throw this.missingDeSerException(serializedCompExpr);
    }
  }
}
