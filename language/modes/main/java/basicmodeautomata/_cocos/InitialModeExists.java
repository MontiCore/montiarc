/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import montiarc.util.ArcError;
import basicmodeautomata.BasicModeAutomataMill;
import basicmodeautomata._ast.ASTModeAutomaton;
import basicmodeautomata._symboltable.ModeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;

import java.util.Set;
import java.util.stream.Collectors;

public class InitialModeExists implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType component) {
    Preconditions.checkNotNull(component);
    Preconditions.checkArgument(component.isPresentSymbol(), "Create Symbol-Table first");
    Set<String> symbolNames = BasicModeAutomataMill.getModeTool().streamModes(component).map(ModeSymbol::getName).collect(Collectors.toSet());
    BasicModeAutomataMill.getModeTool()
        .streamAutomata(component)
        .map(ASTModeAutomaton::getInitialMode)
        .filter(d -> !symbolNames.contains(d.getMode()))
        .forEach(d -> Log.error(ArcError.INITIAL_MODE_DOES_NOT_EXIST.format(d.getMode(), component.getName()), d.get_SourcePositionStart()));
  }
}