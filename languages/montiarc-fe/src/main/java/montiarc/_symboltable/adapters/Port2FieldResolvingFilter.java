package montiarc._symboltable.adapters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;
import montiarc._symboltable.PortSymbol;

public class Port2FieldResolvingFilter extends TransitiveAdaptedResolvingFilter<JavaFieldSymbol> {
  
  public Port2FieldResolvingFilter() {
    super(PortSymbol.KIND, JavaFieldSymbol.class, JavaFieldSymbol.KIND);
  }
  
  @Override
  public Symbol translate(Symbol s) {
    return new Port2FieldAdapter((PortSymbol) s);
  }
  
  @Override
  public Collection<Symbol> filter(ResolvingInfo resolvingInfo, Collection<Symbol> symbols) {
	  Collection<Symbol> result = Lists.newArrayList();
	  for(Symbol s : symbols) {
		  Optional<Symbol> foundSymbol = super.filter(resolvingInfo, s.getName(), getSymbolMap(symbols));
		  if(foundSymbol.isPresent()) {
			  result.add(foundSymbol.get());
		  }
	  }
	  return result;
  }
  
  protected Map<String,Collection<Symbol>> getSymbolMap(Collection<Symbol> symbols){
	  Map<String,Collection<Symbol>> symbolMap = new HashMap<>();
	  for(Symbol s: symbols) {
		  String name = s.getName();
		  if(!symbolMap.containsKey(name)) {
			  symbolMap.put(name, Lists.newArrayList(s));
		  }
		  else {
			  symbolMap.get(name).add(s);
		  }
	  }
	  return symbolMap;
  }
  
}
