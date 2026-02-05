/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.check.SymTypeExpressionFactory;
import montiarc.MontiArcMill;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class CDRole2FieldSymbolDeSer extends FieldSymbolDeSer {

  @Override
  protected boolean deserializeIsPrivate(@NotNull IOOSymbolsScope scope,
                                         @NotNull JsonObject symbolJson) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(symbolJson);
    return symbolJson.getBooleanMemberOpt("isPrivate").orElse(false) ||
      (!(symbolJson.getBooleanMemberOpt("isDefinitiveNavigable").orElse(false)));
  }

  @Override
  protected boolean deserializeIsProtected(@NotNull IOOSymbolsScope scope,
                                           @NotNull JsonObject symbolJson) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(symbolJson);
    return symbolJson.getBooleanMemberOpt("isProtected").orElse(false)
      && symbolJson.getBooleanMemberOpt("isDefinitiveNavigable").orElse(false);
  }

  @Override
  protected boolean deserializeIsPublic(@NotNull IOOSymbolsScope scope,
                                        @NotNull JsonObject symbolJson) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(symbolJson);
    return !symbolJson.getBooleanMemberOpt("isPrivate").orElse(false)
      && !symbolJson.getBooleanMemberOpt("isProtected").orElse(false)
      && symbolJson.getBooleanMemberOpt("isDefinitiveNavigable").orElse(false);
  }

  @Override
  protected SymTypeExpression deserializeType(@NotNull IOOSymbolsScope scope,
                                              @NotNull JsonObject symbolJson) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(symbolJson);

    SymTypeExpression type = SymTypeExpressionDeSer.deserializeMember("type", symbolJson, scope);

    Optional<String> cardinality = symbolJson.getStringMemberOpt("cardinality");

    if (cardinality.isEmpty() || cardinality.get().equals("[1]")) {
      return type;
    } else if (cardinality.get().equals("[0..1]")) {
      TypeSymbol optional = MontiArcMill.globalScope().resolveType("Optional")
        .orElse(MontiArcMill.globalScope().resolveType("java.util.Optional").orElseThrow());
      return SymTypeExpressionFactory.createGenerics(optional, type);
    } else if (symbolJson.getBooleanMemberOpt("isOrdered").orElse(false)) {
      TypeSymbol list = MontiArcMill.globalScope().resolveType("List")
        .orElse(MontiArcMill.globalScope().resolveType("java.util.List").orElseThrow());
      return SymTypeExpressionFactory.createGenerics(list, type);
    } else {
      TypeSymbol set = MontiArcMill.globalScope().resolveType("Set")
        .orElse(MontiArcMill.globalScope().resolveType("java.util.Set").orElseThrow());
      return SymTypeExpressionFactory.createGenerics(set, type);
    }
  }
}
