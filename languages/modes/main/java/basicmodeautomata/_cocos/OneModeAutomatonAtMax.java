/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import basicmodeautomata.BasicModeAutomataMill;
import basicmodeautomata._ast.ASTModeAutomaton;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.BasicModeAutomataError;

import java.util.Optional;

@SuppressWarnings("OptionalIsPresent") // makes code easier to read
public class OneModeAutomatonAtMax implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType component) {
    Preconditions.checkNotNull(component);
    Optional<ASTModeAutomaton> excess = BasicModeAutomataMill.getModeTool().streamAutomata(component).skip(1).findFirst();
    if(excess.isPresent()){
      Log.error(BasicModeAutomataError.MULTIPLE_MODE_AUTOMATA.format(), excess.get().get_SourcePositionStart());
    }
  }
}
