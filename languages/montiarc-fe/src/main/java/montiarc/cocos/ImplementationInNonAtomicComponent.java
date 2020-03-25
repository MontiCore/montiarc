/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

/**
 * Prevents composed components with embedded behavior models
 *
 * @implements [Wor16] MU2:Each atomic component contains at most one
 *  behavior model. (p. 55. Lst. 4.6) TODO: Review
 */
public class ImplementationInNonAtomicComponent implements MontiArcASTComponentCoCo {
  
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
    ComponentSymbol cs = (ComponentSymbol) node.getSymbolOpt().get();
    if (cs.isDecomposed() && cs.hasBehavior()) {
      Log.error("0xMA051 There must not be a behavior embedding in " +
                    "composed components.",
          node.get_SourcePositionStart());
    }
  }
}
