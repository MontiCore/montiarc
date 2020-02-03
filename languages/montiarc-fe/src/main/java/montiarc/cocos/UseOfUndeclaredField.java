/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.mcexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTGuardExpression;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTVariableDeclaration;
import montiarc._cocos.MontiArcASTGuardExpressionCoCo;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

/**
 * Context condition for checking, if a reference is used inside an automaton
 * which has not been defined in an {@link ASTVariableDeclaration} or as
 * {@link ASTPort}.
 *
 * @implements [Wor16] AR1: Names used in guards, valuations, and assignments
 * exist in the automaton. (p. 102, Lst. 5.19)
 * @author Gerrit Leonhardt, Andreas Wortmann, Michael Mutert
 */
public class UseOfUndeclaredField
    implements MontiArcASTIOAssignmentCoCo, MontiArcASTGuardExpressionCoCo {
  
  @Override
  public void check(ASTIOAssignment node) {
    // only check left side of IOAssignment, right side is implicitly checked
    // when resolving type of the valuations
    if (node.isPresentName()) {
      final String name = node.getName();

      if (node.getEnclosingScopeOpt().isPresent()) {

        Scope searchScope = node.getEnclosingScopeOpt().get();
        while((!searchScope.getSpanningSymbol().isPresent() ||
            !searchScope.getSpanningSymbol().get().isKindOf(ComponentSymbol.KIND))
            && searchScope.getEnclosingScope().isPresent()){
          searchScope = searchScope.getEnclosingScope().get();
        }
        ComponentSymbol currentComponent
            = (ComponentSymbol) searchScope.getSpanningSymbol().get();

        boolean foundVar = currentComponent.getVariable(name).isPresent();
        boolean foundPort
            = currentComponent.getPort(name, true).isPresent();

        if (!foundVar && !foundPort) {
          Optional<JavaTypeSymbol> javaType = Optional.empty();

          // could also be a static method call
          if (node.isCall()) {
            javaType = node.getEnclosingScopeOpt().get().resolve(node.getName(),
                JavaTypeSymbol.KIND);
          }

          if (!javaType.isPresent()) {
            Log.error(
                String.format("0xMA079: The name '%s' is used in %s, but is " +
                        "neither declared a port, nor as a " +
                        "variable or static method call.",
                    name, "assignment"),
                node.get_SourcePositionStart());
          }
        }
      }
    }
  }
  
  @Override
  public void check(ASTGuardExpression node) {
    node.getExpression().accept(new GuardVisitor());
  }
  
  /**
   * This class is used to check whether names used in GuardExpressions are
   * declared.
   */
  private class GuardVisitor implements MCExpressionsVisitor {
    
    @Override
    public void visit(ASTNameExpression node) {
      check(node.getName(), node, "guard");
    }

    /**
     * Private common helper function that is used to check whether a used field
     * by the given name exists.
     *
     * @param name Name of the field
     * @param node Node object of the field
     * @param usage Environment in which the field is used (used in the log)
     */
    private void check(String name, ASTNameExpression node, String usage) {
      if (node.getEnclosingScopeOpt().isPresent()) {
        Scope scope = node.getEnclosingScopeOpt().get(); //TransitionScope
        if(scope.getEnclosingScope().isPresent()) {
          // Scope of the automaton
          final Scope automatonScope = scope.getEnclosingScope().get();
          if(automatonScope.getEnclosingScope().isPresent()) {
            // Scope spanned by component symbol
            scope = automatonScope.getEnclosingScope().get();
            ComponentSymbol comp
                = ((ComponentSymbol) scope.getSpanningSymbol().get());
            if (comp.getVariable(name).isPresent()) {
              return;
            }
            else if (comp.getPort(name, true).isPresent()) {
              return;
            }
            else {
              final Optional<JavaTypeSymbol> typeSymbolOpt
                  = scope.resolve(name, JavaTypeSymbol.KIND);
              if (typeSymbolOpt.isPresent()
                  && (typeSymbolOpt.get().isEnum()
                  || typeSymbolOpt.get().isClass())) {
                return;
              }
            }
            Log.error(
                String.format("0xMA079: The name '%s' is used in %s, but is " +
                        "neither declared a port, nor as a variable.",
                    name, usage),
                node.get_SourcePositionStart());
          }
        }
      }
    }
  }
  
}
