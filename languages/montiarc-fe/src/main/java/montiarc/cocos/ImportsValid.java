package montiarc.cocos;

import de.monticore.symboltable.ImportStatement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcASTMACompilationUnitCoCo;
import montiarc._symboltable.ComponentSymbol;

import java.util.Optional;

public class ImportsValid implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    ComponentSymbol cmp = (ComponentSymbol) node.getSymbol().orElse(null);


  }
}
