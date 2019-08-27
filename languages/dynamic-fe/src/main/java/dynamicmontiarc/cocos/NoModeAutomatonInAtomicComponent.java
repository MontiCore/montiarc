/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.se_rwth.commons.logging.Log;
import dynamicmontiarc.helper.DynamicMontiArcHelper;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

/**
 * Checks that only decomposed components contain a mode automaton
 *
 * @author Mutert
 */
public class NoModeAutomatonInAtomicComponent implements MontiArcASTComponentCoCo {

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
    final ComponentSymbol compSymbol = (ComponentSymbol) node.getSymbolOpt().get();
    if(DynamicMontiArcHelper.isDynamic(node) && compSymbol.isAtomic()){
      Log.error(
          String.format("0xMA205 The component %s is an atomic component, " +
                            "but it also includes a mode automaton. " +
                            "Either add missing subcomponents or remove the " +
                            "mode automaton.",
              compSymbol.getFullName()),
          node.get_SourcePositionStart()
      );
    }
  }
}
