/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._cocos.MontiArcASTJavaPBehaviorCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

/**
 * Checks whether variables with same name are defined multiple times in JavaP
 * behavior.
 *
 * @implements No literature reference, AJava CoCo
 */
public class JavaPVariableIdentifiersUnique implements MontiArcASTJavaPBehaviorCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTJavaPBehaviorCoCo#check(montiarc._ast.ASTJavaPBehavior)
   */
  @Override
  public void check(ASTJavaPBehavior node) {
    List<String> names = new ArrayList<>();
    if (node.isPresentSpannedScope()) {
      checkScope(node.getSpannedScope().get());
    }
  }
  
  private void checkScope(Scope s) {
    List<String> names = new ArrayList<>();
    Collection<VariableSymbol> vars = s
        .resolveLocally(VariableSymbol.KIND);
    names = new ArrayList<>();
    for (VariableSymbol v : vars) {
      if (names.contains(v.getName())) {
        Log.error("0xMA016 Duplicate local variable names " + v.getName() + ".",
            v.getSourcePosition());
      }
      else {
        names.add(v.getName());
        if (s.getEnclosingScope().isPresent()) {
          checkPortOrVariabelWithNameAlreadyExists(v, s.getEnclosingScope().get());
        }
      }
    }
    for (Scope sub : s.getSubScopes()) {
      checkScope(sub);
    }
  }
  
  /**
   * Checks whether the component defines a component variable or port with the
   * same name of the Java/P variable. If so, it logs a warning.
   * 
   * @param v Symbol of the variable which should be checked
   */
  private void checkPortOrVariabelWithNameAlreadyExists(VariableSymbol v, Scope s) {
    Collection<PortSymbol> ports = s.resolveMany(v.getName(),
        PortSymbol.KIND);
    Collection<VariableSymbol> vars = s.resolveMany(v.getName(),
        VariableSymbol.KIND);
    
    if (ports.size() > 0) {
      Log.warn(
          "0xMA094 There already exists a port with name " + v.getName()
              + " in the component definition.",
          v.getAstNode().get().get_SourcePositionStart());
    }
    
    if (vars.size() > 0) {
      Log.warn(
          "0xMA095 There already exists a variable with name " + v.getName()
              + " in the component definition.",
          v.getAstNode().get().get_SourcePositionStart());
    }
    
    if (s.getEnclosingScope().isPresent() && !s.getEnclosingScope().get().isShadowingScope()) {
      checkPortOrVariabelWithNameAlreadyExists(v, s.getEnclosingScope().get());
    }
    
  }
  
}
