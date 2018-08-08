package montiarc.cocos;

import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

public class AmbiguousTypes implements MontiArcASTComponentCoCo {
@Override
public void check(ASTComponent node) {
  if(node.getSymbolOpt().isPresent()){
    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();
    if(comp.getEnclosingScope().resolveMany(comp.getFullName(), JTypeSymbol.KIND).size()>=1){
      Log.error("0xMA012 The name of component "+comp.getName()+" is ambiguous",comp.getSourcePosition());
    }
    comp.getSubComponents().forEach(subcomp->{
      if(comp.getEnclosingScope().resolveMany(subcomp.getComponentType().getFullName(), JTypeSymbol.KIND).size()>=1){
        Log.error("0xMA040 The type "+ subcomp.getComponentType().getName()+ " of subcomponent "+subcomp.getName()+" is ambiguous", subcomp.getSourcePosition());
      }
    });
    comp.getPorts().forEach(portSymbol -> {
      if(portSymbol.getTypeReference().existsReferencedSymbol()){
        if(comp.getEnclosingScope().resolveMany(portSymbol.getTypeReference().getReferencedSymbol().getFullName(), ComponentSymbol.KIND).size()>=1){
          Log.error("0xMA040 The type "+ portSymbol.getTypeReference().getName()+ " of port "+portSymbol.getName()+" is ambiguous", portSymbol.getSourcePosition());
        }
      }
    });
  }else{
    Log.error("0xMA010 ComponentSymbol of component AST "+ node.getName() +" is missing");
  }
  
}
}
