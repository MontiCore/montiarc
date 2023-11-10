/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbolDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;

public class ArcBasisSubcomponentSymbolDeSer extends SubcomponentSymbolDeSer {

  @Override
  protected FullCompKindExprDeSer getCompKindExprDeSer() {
    return ArcBasisMill.compTypeExprDeSer();
  }

  @Override
  protected void serializeType(CompKindExpression type, CompSymbolsSymbols2Json s2j) {
    super.serializeType(type, s2j);
  }

  @Override
  protected CompKindExpression deserializeType(JsonObject symbolJson) {
    return symbolJson.getObjectMemberOpt("type").map(t -> this.getCompKindExprDeSer().deserialize(t)).orElse(null);
  }
}
