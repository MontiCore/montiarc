/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor.ExpressionKind;

/**
 * This coco checks whether only declared variables are used in ajava.
 *
 * @author Pfeiffer
 * @version $Revision$, $Date$
 */
public class AJavaUsesExistingVariablesAndPorts implements MontiArcASTJavaPBehaviorCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTJavaPBehaviorCoCo#check(montiarc._ast.ASTJavaPBehavior)
   */
  @Override
  public void check(ASTJavaPBehavior node) {
    Scope componentScope = node.getEnclosingScopeOpt().get();
    
    for (ASTBlockStatement blockStmnt : node.getBlockStatementList()) {
      NamesInExpressionsDelegatorVisitor visitor = new NamesInExpressionsDelegatorVisitor();
      blockStmnt.accept(visitor);
      Map<ASTNameExpression, ExpressionKind> foundNames = visitor.getFoundNames();
      
      for (Entry<ASTNameExpression, ExpressionKind> entry : foundNames.entrySet()) {
        // check whether used name is declared as component variable or port or
        // is a static type
        Collection<VariableSymbol> vars = componentScope
            .<VariableSymbol> resolveDownMany(entry.getKey().getName(), VariableSymbol.KIND);
        Collection<PortSymbol> ports = componentScope
            .<PortSymbol> resolveDownMany(entry.getKey().getName(), PortSymbol.KIND);
        Optional<JTypeSymbol> type = componentScope.resolve(entry.getKey().getName(),
            JavaTypeSymbol.KIND);
        boolean foundConfigParameter = false;
        Optional<ComponentSymbol> comp = (Optional<ComponentSymbol>) componentScope
            .getSpanningSymbol();
        if (comp.isPresent()) {
          foundConfigParameter = comp.get().getConfigParameters().stream()
              .filter(p -> p.getName().equals(entry.getKey().getName())).findAny().isPresent();
        }
        
        // check whether used name is local ajava variable
        boolean isLocalVar = false;
        if (entry.getKey().getEnclosingScopeOpt().isPresent()) {
          isLocalVar = checkPortOrVariabelWithNameAlreadyExists(entry.getKey().getName(),
              entry.getKey().getEnclosingScopeOpt().get());
        }
        
        if (vars.isEmpty() && ports.isEmpty() && !type.isPresent() && !isLocalVar && !foundConfigParameter) {
          Log.error(
              "0xMA107 used variable " + entry.getKey().getName() + " is used but not declared.",
              entry.getKey().get_SourcePositionStart());
        }
      }
    }
    
  }
  
  /**
   * Checks whether the component defines a component variable or port with the
   * same name of the Java/P variable. If so, it logs a warning.
   * 
   * @param v Symbol of the variable which should be checked
   */
  private boolean checkPortOrVariabelWithNameAlreadyExists(String name, Scope s) {
    Collection<PortSymbol> ports = s.resolveMany(name,
        PortSymbol.KIND);
    Collection<VariableSymbol> vars = s.resolveMany(name,
        VariableSymbol.KIND);
    
    if (ports.size() > 0 || vars.size() > 0) {
      return true;
    }
    
    if (s.getEnclosingScope().isPresent() && !s.getEnclosingScope().get().isShadowingScope()) {
      checkPortOrVariabelWithNameAlreadyExists(name, s.getEnclosingScope().get());
    }
    
    return false;
  }
  
}
