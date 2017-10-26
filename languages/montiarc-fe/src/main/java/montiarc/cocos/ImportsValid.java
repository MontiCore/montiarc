package montiarc.cocos;

import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcASTMACompilationUnitCoCo;
import montiarc._symboltable.ComponentSymbol;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ImportsValid implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    ComponentSymbol cmp = (ComponentSymbol) node.getSymbol().orElse(null);
    Scope encScope = cmp.getEnclosingScope();
    Scope spanScope = cmp.getSpannedScope();

    List<ImportStatement> imports = cmp.getImports();


    for (ImportStatement i: imports) {
      //Try to resolve symbol if it's not a star import
      if (!i.isStar()){
        Optional<CDTypeSymbol> cdsym = encScope.<CDTypeSymbol> resolve(i.getStatement(),CDTypeSymbol.KIND);
        Optional<ComponentSymbol> compsym = encScope.<ComponentSymbol> resolve(i.getStatement(),ComponentSymbol.KIND);
        Collection<JTypeSymbol> typesym = spanScope.<JTypeSymbol> resolveMany(i.getStatement(),JTypeSymbol.KIND);


        if (!cdsym.isPresent() && !compsym.isPresent() && typesym.isEmpty()){
          Log.error("0xMA076 Import " + i.getStatement()+ " does not exist.");
        }
      }
    }


  }
}
