package montiarc.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.monticore.java.javadsl._cocos.JavaDSLASTPrimaryExpressionCoCo;
import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTValueListCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

import javax.swing.text.html.Option;

//public class AutomatonOutputInExpression implements MontiArcASTValueListCoCo {


public class AutomatonOutputInExpression implements JavaDSLASTPrimaryExpressionCoCo {

  @Override
  public void check(ASTPrimaryExpression node) {
    if (node.nameIsPresent() && node.getEnclosingScope().isPresent()) {
      Scope scope = node.getEnclosingScope().get();
      Optional<? extends ScopeSpanningSymbol> scopeSymbol = scope.getSpanningSymbol();
      if (scopeSymbol.isPresent() && scopeSymbol.get().isKindOf(ComponentSymbol.KIND)) {
        Optional<ASTNode> nodeAST = scopeSymbol.get().getAstNode();
        if (nodeAST.isPresent()) {
          ASTComponent compAST = (ASTComponent) nodeAST.get();
          List<ASTElement> elements = compAST.getBody().getElements();
          for (ASTElement elem : elements) {
            if (elem instanceof ASTAutomatonBehavior) {
              Optional<PortSymbol> found = scope.resolve(node.getName().get(), PortSymbol.KIND);
              if (found.isPresent() && found.get().isOutgoing()) {
                Log.error("0xMA022 Field " + found.get().getName() + " is an Ouput and not allowed in Expressions.", node.get_SourcePositionStart());
              }
            }
          }
        }
      }
    }
  }
}



