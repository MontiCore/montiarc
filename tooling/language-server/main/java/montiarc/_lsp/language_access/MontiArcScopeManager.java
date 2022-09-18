/* (c) https://github.com/MontiCore/monticore */
package montiarc._lsp.language_access;

import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc._symboltable.MontiArcGlobalScope;

public class MontiArcScopeManager extends MontiArcScopeManagerTOP {

  @Override
  public void initGlobalScope(MCPath modelPath) {
    MontiArcMill.init();
    IMontiArcGlobalScope gs = MontiArcMill.globalScope();
    BasicSymbolsMill.initializePrimitives();
    setGlobalScope((MontiArcGlobalScope) gs);
  }

  @Override
  public void clearGlobalScope() {
    syncAccessGlobalScope(gs -> {
      gs.clear();
      BasicSymbolsMill.initializePrimitives();
    });
  }
}
