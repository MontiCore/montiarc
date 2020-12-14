/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import com.google.common.base.Preconditions;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.resolver.CD4CodeResolver;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class ArcCD4CodeResolver extends CD4CodeResolver {

  public ArcCD4CodeResolver(@NotNull ICD4CodeGlobalScope cdGlobalScope) {
    super(Preconditions.checkNotNull(cdGlobalScope));
  }

  @Override
  public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    List<TypeSymbol> typeSymbols = super.resolveAdaptedTypeSymbol(foundSymbols, name, modifier, predicate);
    typeSymbols.addAll(super.resolveAdaptedOOTypeSymbol(foundSymbols, name, modifier, predicate::test));
    return typeSymbols;
  }
}