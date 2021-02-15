/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.io.FileFinder;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class MontiArcGlobalScope extends MontiArcGlobalScopeTOP {

  @Override
  public MontiArcGlobalScope getRealThis() {
    return this;
  }

  @Override
  public void init() {
    super.init();
    desers.put("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
    desers.put("de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope", new MontiArc4OOSymbolDeSer());
    desers.put("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol", new MethodSymbolDeSer());
    desers.put("de.monticore.cdassociation._symboltable.CDRoleSymbol", new FieldSymbolDeSer());
    desers.put("de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope", new MontiArc4BasicSymbolsDeSer());
    
    
  }

  //TODO: Default tries to load montiarc component models instead of serialized symbol tables. Remove once fixed.
  @Override
  public void loadFileForModelName(@NotNull String modelName) {
    Preconditions.checkNotNull(modelName);
    Optional<ModelCoordinate> mc = FileFinder.findFile(getModelPath(), modelName, ".sym", cache);
    if (mc.isPresent()) {
      addLoadedFile(mc.get().getQualifiedPath().toString());
      IMontiArcArtifactScope as = getSymbols2Json().load(mc.get().getLocation());
      addSubScope(as);
    }
  }
}