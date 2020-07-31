package arcbasis._symboltable;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import genericarc._symboltable.ArcTypeParameterSymbol;
import genericarc._symboltable.IGenericArcScope;

import java.util.Optional;

public class ComponentTypeSymbolLoader extends ComponentTypeSymbolLoaderTOP{

  public  ComponentTypeSymbolLoader(String name,arcbasis._symboltable.IArcBasisScope enclosingScope)  {
    super(name,enclosingScope);
  }

  boolean isAlreadyLoaded ;

  public  arcbasis._symboltable.ComponentTypeSymbol getLoadedSymbol ()  {

    if (!isAlreadyLoaded) {
      loadedSymbol = loadSymbol();
    }

    if (!loadedSymbol.isPresent()) {
      Log.error("0xA1038 " + ComponentTypeSymbolLoader.class.getSimpleName() + " Could not load full information of '" +
          name + "' (Kind " + "ComponentTypeSymbol" + ").");
    }

    return loadedSymbol.get();
  }

  public  void setName (String name)  {
    this.isAlreadyLoaded = false;
    super.setName(name);
  }

  public  void setEnclosingScope (arcbasis._symboltable.IArcBasisScope enclosingScope)  {
    this.isAlreadyLoaded = false;
    super.setEnclosingScope(enclosingScope);
  }

  public  boolean isSymbolLoaded ()  {
    if (!isAlreadyLoaded) {
      loadSymbol();
    }
    return loadedSymbol.isPresent();
  }

  public Optional<ComponentTypeSymbol> loadSymbol ()  {
    com.google.common.base.Preconditions.checkArgument(!com.google.common.base.Strings.isNullOrEmpty(name), " 0xA4070 Symbol name may not be null or empty.");

    Log.debug("Load full information of '" + name + "' (Kind " + "arcbasis._symboltable.ComponentTypeSymbol" + ").", ComponentTypeSymbolLoader.class.getSimpleName());
    this.isAlreadyLoaded = true;
    Optional<arcbasis._symboltable.ComponentTypeSymbol> resolvedSymbol = enclosingScope.resolveComponentType(name);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + name + "' successfully.",
          ComponentTypeSymbolLoader.class.getSimpleName());
      loadedSymbol = Optional.of(resolvedSymbol.get());
    } else if(enclosingScope instanceof IGenericArcScope){
      Optional<ArcTypeParameterSymbol> resolvedTypeSymbol = ((IGenericArcScope)enclosingScope).resolveArcTypeParameter(name);
      if(resolvedTypeSymbol.isPresent()){
        String storedName = name;
        name = resolvedTypeSymbol.get().getAstNode().getUpperBound(0).printType(new MCBasicTypesPrettyPrinter(new IndentPrinter()));
        resolvedSymbol = super.loadSymbol();
        name = storedName;
      }
      else {
        Log.debug("Cannot load full information of '" + name, ComponentTypeSymbolLoader.class.getSimpleName());
      }
    }
    return resolvedSymbol;
  }
}
