/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcautomaton._visitor.NamePresenceChecker;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionActionCoCo;

/**
 * checks whether the ports or variables used in transition-re-, entry- and exit-actions exist.
 */
public class FieldsInReactionsExist implements SCTransitions4CodeASTTransitionActionCoCo {

  @Override
  public void check(ASTTransitionAction action) {
    if(action.isPresentMCBlockStatement()){
      ArcAutomatonTraverser traverser = ArcAutomatonMill.inheritanceTraverser();
      traverser.add4ExpressionsBasis(new NamePresenceChecker(action.getEnclosingScope()));
      action.getMCBlockStatement().accept(traverser);
    }
  }
}