/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcautomaton._visitor.NamePresenceChecker;
import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

public class FieldsInGuardsExist implements SCTransitions4CodeASTTransitionBodyCoCo {

  @Override
  public void check(@NotNull ASTTransitionBody transition) {
    Preconditions.checkNotNull(transition);
    if(transition.isPresentPre()) {
      ArcAutomatonTraverser traverser = ArcAutomatonMill.inheritanceTraverser();
      traverser.add4ExpressionsBasis(new NamePresenceChecker(transition.getEnclosingScope()));
      transition.getPre().accept(traverser);
    }
  }
}