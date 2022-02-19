/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTInitialOutputDeclaration;
import arcbehaviorbasis.BehaviorError;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks whether states marked as initial (by {@link arcautomaton._ast.ASTInitialOutputDeclaration}s) refer to an
 * existing state of that automaton.
 */
public class InitialStatesResolvable implements ArcAutomatonASTInitialOutputDeclarationCoCo {

  @Override
  public void check(@NotNull ASTInitialOutputDeclaration node) {
    Preconditions.checkNotNull(node);
    if(!node.isPresentNameDefinition()){
      BehaviorError.INITIAL_STATE_REFERENCE_MISSING.logAt(node, node.getName());
    }
  }
}
