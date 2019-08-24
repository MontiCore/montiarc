/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * @implements [Hab16] R12: An inner component type definition must not extend
 * the component type in which it is defined. (p. 68, lst. 3.47)
 * 
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class InnerComponentNotExtendsDefiningComponent implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol definingComp = (ComponentSymbol) node.getSymbolOpt().get();

    // Start check only at the root of the inner component hierarchy
    // This prevents duplicate errors due to the call to this function
    // on each inner component
    if(definingComp.isInnerComponent()){
      return;
    }
    Collection<ComponentSymbol> innerComps = definingComp.getInnerComponents();

    Deque<String> nameStack = new ArrayDeque<>();
    nameStack.push(definingComp.getFullName());
    for (ComponentSymbol inner : innerComps) {
      checkInner(inner, nameStack);
    }
  }

  /**
   * Recursively check the inner components.
   *
   * @param comp Symbol of the component which should be checked
   * @param compNameStack Stack of fully qualified component names which occur
   *                      higher in the inner component hierarchy.
   */
  private void checkInner(ComponentSymbol comp, Deque<String> compNameStack){
    if (comp.getSuperComponent().isPresent()) {
      String superComponent = comp.getSuperComponent().get().getFullName();

      // Search the stack for possible occurences of the super component
      // fully qualified name
      if (compNameStack.contains(superComponent)) {
        Log.error(
            String.format("0xMA083 Inner component of type %s extends the " +
                              "component type %s which is the same " +
                              "component type it is defined in.",
                comp.getFullName(), superComponent),
            comp.getAstNode().get().get_SourcePositionStart());
      }
    }
    // Add the self name to the stack
    compNameStack.push(comp.getFullName());

    // Recursively check all inner components
    for (ComponentSymbol inner : comp.getInnerComponents()) {
      checkInner(inner, compNameStack);
    }
    // Remove the current name from the top of the stack
    compNameStack.pop();
  }
  
}
