/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcGlobalScope extends MontiArcGlobalScopeTOP {

  @Override
  public MontiArcGlobalScope getRealThis() {
    return this;
  }

  @Override
  public void init() {
    super.init();
    this.putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
    this.putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol", new MethodSymbolDeSer());
    this.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol", new FieldSymbolDeSer());
  }

  @Override
  public void clear() {
    this.getRootVariationPoints().clear();
    super.clear();
  }

  public void loadFile(@NotNull String file) {
    Preconditions.checkNotNull(file);
    if (!isFileLoaded(file)) {
      this.addLoadedFile(file);
      this.addSubScope(this.getSymbols2Json().load(file));
    }
  }
}