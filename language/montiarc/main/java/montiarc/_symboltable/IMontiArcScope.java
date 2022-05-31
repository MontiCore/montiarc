/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import de.monticore.symboltable.ISymbol;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for all scopes used in MontiArc and it's sub-languages, as usually provided by {@link IMontiArcScopeTOP}.
 * This interface is specified explicitly to temporarily counter a bug in which duplicated,
 * but identical symbols are not recognized as such and instead cause problems.
 */
public interface IMontiArcScope extends IMontiArcScopeTOP {

  @Override
  default <T extends ISymbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
    if(resolved.stream().anyMatch(symbol -> "java.lang".equals(symbol.getPackageName()))){
      return resolved.stream().findFirst();
    }
    return IMontiArcScopeTOP.super.getResolvedOrThrowException(resolved);
  }
}