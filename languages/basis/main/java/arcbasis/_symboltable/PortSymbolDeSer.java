/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import org.codehaus.commons.nullanalysis.NotNull;

public class PortSymbolDeSer extends PortSymbolDeSerTOP {

  @Override
  protected void serializeType(@NotNull SymTypeExpression type, @NotNull ArcBasisSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "type", type);
  }

  @Override
  protected SymTypeExpression deserializeType(@NotNull JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson);
  }
}
