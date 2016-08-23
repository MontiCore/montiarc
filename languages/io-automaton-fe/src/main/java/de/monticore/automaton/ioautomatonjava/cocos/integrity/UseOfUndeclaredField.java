package de.monticore.automaton.ioautomatonjava.cocos.integrity;

import java.util.ArrayList;
import java.util.List;

import de.monticore.automaton.ioautomaton.TypeCompatibilityChecker;
import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTValuationExt;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTIOAssignmentCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTValuationExtCoCo;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.automaton.ioautomatonjava._ast.ASTValuation;
import de.monticore.automaton.ioautomatonjava._cocos.IOAutomatonJavaASTValuationCoCo;
import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if a variable is used inside an automaton
 * which has not been defined in an {@link ASTInputDeclaration}, {@link ASTVariableDeclaration} or {@link ASTOutputDeclaration}.
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   $Version$
 *
 */
public class UseOfUndeclaredField implements IOAutomatonASTIOAssignmentCoCo, IOAutomatonJavaASTValuationCoCo {
  
  private static class FieldNameGetter implements JavaDSLVisitor {
    ArrayList<String> parts = new ArrayList<>();
    ArrayList<String> names;
//    
//    @Override
//    public void endVisit(ASTExpression node) {
//      if (node.nameIsPresent() && node.expressionIsPresent()) {
//        parts.add(node.getName().get());
//      } else if (node.primaryExpressionIsPresent()) {
//      } else {
//        addName();
//      }
//    }
    
    @Override
    public void visit(ASTPrimaryExpression node) {
      if (node.nameIsPresent()) {
        parts.add(node.getName().get());
        addName(); //TODO
      } else {
        addName();
      }
    }
    
    public FieldNameGetter init() {
      names = new ArrayList<>();
      return this;
    }
    
    private void addName() {
      if (!parts.isEmpty()) {
        names.add(String.join(".", parts));
      }
      parts.clear();
    }
    
    public List<String> getNames() {
      addName();
      return names;
    }
  }
  
//  @Override
//  public void check(ASTPrimaryExpression node) {
//    if (node.nameIsPresent()) {
//      Scope scope = node.getEnclosingScope().get();
//      boolean found = scope.resolve(node.getName().get(), VariableSymbol.KIND).isPresent();
//      if (!found) {
//        Log.error("0xAA230 " + node.getName().get() + " is used as a field, but is not declared as such.", node.get_SourcePositionStart());
//      }
//    }
//  }
  
  private FieldNameGetter fieldName = new FieldNameGetter();

  @Override
  public void check(ASTValuation node) {    
    node.getExpression().accept(fieldName.init());
    List<String> names = fieldName.getNames();
    for (String name : names) {
      Scope scope = node.getEnclosingScope().get();
      boolean found = scope.resolve(name, VariableSymbol.KIND).isPresent();
      if (!found) {
        Log.error("0xAA230 " + name + " is used as a field, but is not declared as such.", node.get_SourcePositionStart());
      }
    }
  }

  @Override
  public void check(ASTIOAssignment node) {
    if (node.nameIsPresent()) {
      Scope scope = node.getEnclosingScope().get();
      boolean found = scope.resolve(node.getName().get(), VariableSymbol.KIND).isPresent();
      if (!found) {
        Log.error("0xAA231 " + node.getName().get() + " is used as a field, but is not declared as such.", node.get_SourcePositionStart());
      }
    }
  }
  
}
