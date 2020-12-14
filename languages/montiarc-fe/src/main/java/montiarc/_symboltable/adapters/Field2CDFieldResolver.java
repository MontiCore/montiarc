/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IFieldSymbolResolver;
import de.monticore.symboltable.modifiers.AccessModifier;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Field2CDFieldResolver implements IFieldSymbolResolver {

  protected ICD4CodeGlobalScope globalScope;

  public Field2CDFieldResolver(@NotNull ICD4CodeGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<FieldSymbol> resolveAdaptedFieldSymbol(boolean foundSymbols, String name,
                                                     AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    List<FieldSymbol> result = new ArrayList<>();
    Optional<FieldSymbol> symbol = globalScope.resolveField(name, modifier);
    if(symbol.isPresent() && symbol.get().isIsPublic()) {
      result.add(new CDField2FieldAdapter(symbol.get()));
    }
    return result;
  }
}