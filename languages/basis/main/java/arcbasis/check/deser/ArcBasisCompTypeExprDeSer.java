/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check.deser;

import arcbasis.check.TypeExprOfComponent;
import arcbasis.check.TypeExprOfGenericComponent;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Composed DeSerializator of {@link CompKindExpression}s for the ArcBasis language.
 */
public class ArcBasisCompTypeExprDeSer implements FullCompKindExprDeSer {

  protected TypeExprOfComponentDeSer componentExprDeSer;

  protected TypeExprOfGenericComponentDeSer genericComponentExprDeSer;

  public ArcBasisCompTypeExprDeSer() {
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
  public CompKindExpression deserialize(@NonNull ICompSymbolsScope scope, @NonNull JsonElement serialized) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(serialized);

    switch (JsonDeSers.getKind(serialized.getAsJsonObject())) {
      case TypeExprOfComponentDeSer.SERIALIZED_KIND: return componentExprDeSer.deserialize(scope, serialized.getAsJsonObject());
      case TypeExprOfGenericComponentDeSer.SERIALIZED_KIND: return genericComponentExprDeSer.deserialize(scope, serialized.getAsJsonObject());
      default:
        throw missingDeSerException(serialized.getAsJsonObject());
    }
  }
}
