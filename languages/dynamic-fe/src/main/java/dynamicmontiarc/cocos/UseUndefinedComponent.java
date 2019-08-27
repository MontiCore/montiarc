/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._ast.ASTModeAutomaton;
import dynamicmontiarc._cocos.DynamicMontiArcASTModeAutomatonCoCo;
import dynamicmontiarc.helper.DynamicMontiArcHelper;
import dynamicmontiarc._ast.ASTModeDeclaration;
import dynamicmontiarc._ast.ASTUseStatement;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTSubComponent;
import montiarc._ast.ASTSubComponentInstance;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceKind;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * R3: Modes may only use components that have been declared in the
 * corresponding component type definition.
 *
 * @author (last commit) Fuerste
 */
public class UseUndefinedComponent implements DynamicMontiArcASTModeAutomatonCoCo {

  @Override
  public void check(ASTModeAutomaton node) {

    if(!node.getEnclosingScopeOpt().isPresent()){
      // TODO Better error handling
      return;
    }
    final Scope componentScope = node.getEnclosingScopeOpt().get();

    for (ASTModeDeclaration mode : node.getModeDeclarationList()) {
      for (ASTUseStatement useStatement : mode.getUseStatementList()) {
        for (String usedCmp : useStatement.getNameList()) {

          // For the component instance mentioned in the use statement we try to
          // find the instance with the component symbol of the component that
          // embeds the mode controller
          final Optional<Symbol> resolvedInstance =
              componentScope.resolve(usedCmp, ComponentInstanceSymbol.KIND);

          if (!resolvedInstance.isPresent()) {
            Log.error(
                String.format("0xMA204 Component '%s' declared to be used in " +
                                  "modes '%s' does not exist!",
                    usedCmp, mode.getModeList().toString()),
                useStatement.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
