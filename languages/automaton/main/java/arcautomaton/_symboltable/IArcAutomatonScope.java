/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._symboltable;

import arcbasis._symboltable.ArcPortSymbol;
import de.monticore.scevents._symboltable.SCEventDefSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface IArcAutomatonScope extends IArcAutomatonScopeTOP {
  
  @Override
  default List<SCEventDefSymbol> resolveAdaptedSCEventDefLocallyMany(boolean foundSymbols,
                                                                     String name,
                                                                     AccessModifier modifier,
                                                                     Predicate<SCEventDefSymbol> predicate) {
  
    List<ArcPortSymbol> ports = resolveArcPortLocallyMany(foundSymbols, name, AccessModifier.ALL_INCLUSION, ArcPortSymbol::isIncoming);
  
    List<SCEventDefSymbol> adapters = new ArrayList<>(ports.size());
  
    for (ArcPortSymbol port : ports) {
    
      if (getLocalSCEventDefSymbols().stream().filter(v -> v instanceof Port2EventDefAdapter)
          .noneMatch(v -> ((Port2EventDefAdapter) v).getAdaptee().equals(port))) {
      
        // instantiate the adapter
        SCEventDefSymbol adapter = new Port2EventDefAdapter(port);
      
        // filter by modifier and predicate
        if (modifier.includes(adapter.getAccessModifier()) && predicate.test(adapter)) {
        
          // add the adapter to the result
          adapters.add(adapter);
        
          // add the adapter to the scope
          this.add(adapter);
        }
      }
    }
    return adapters;
    
  }
}
