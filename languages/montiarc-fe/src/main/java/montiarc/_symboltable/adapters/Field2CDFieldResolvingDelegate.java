/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.IFieldSymbolResolvingDelegate;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Field2CDFieldResolvingDelegate implements IFieldSymbolResolvingDelegate {

  protected CD4AnalysisGlobalScope globalScope;

  public Field2CDFieldResolvingDelegate(@NotNull CD4AnalysisGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<FieldSymbol> resolveAdaptedFieldSymbol(boolean foundSymbols, String name,
    AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    List<FieldSymbol> result = new ArrayList<>();
    Optional<CDFieldSymbol> symbol = globalScope.resolveCDField(name, modifier);
    if(symbol.isPresent() && symbol.get().isIsPublic()) {
      result.add(new CDField2FieldAdapter(symbol.get()));
    }
    return result;
  }
}