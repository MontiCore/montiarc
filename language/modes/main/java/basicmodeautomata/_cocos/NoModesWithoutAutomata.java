/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import basicmodeautomata.BasicModeAutomataMill;
import montiarc.util.BasicModeAutomataError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;

public class NoModesWithoutAutomata implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType component) {
    Preconditions.checkNotNull(component);
    BasicModeAutomataMill.getModeTool()
        .streamDeclarations(component)
        .limit(1)
        .filter(x -> ! BasicModeAutomataMill.getModeTool().streamAutomata(component).findAny().isPresent())
        .forEach(x -> Log.error(BasicModeAutomataError.MODES_WITHOUT_AUTOMATON.format(component.getName()), x.get_SourcePositionStart()));
  }
}
