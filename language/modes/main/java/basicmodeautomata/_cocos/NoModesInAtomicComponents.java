/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import montiarc.util.ArcError;
import basicmodeautomata.BasicModeAutomataMill;
import basicmodeautomata.util.ComponentModeTool;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Stream;

public class NoModesInAtomicComponents implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType component) {
    Preconditions.checkArgument(Preconditions.checkNotNull(component).isPresentSymbol(), "Symbol missing");
    if(component.getSymbol().isAtomic()){
      ComponentModeTool helper = BasicModeAutomataMill.getModeTool();
      Stream.concat(
          helper.streamDeclarations(component),
          helper.streamAutomata(component)
      ).forEach(x -> Log.error(
          ArcError.MODE_ELEMENTS_IN_ATOMIC_COMPONENTS.format(),
          x.get_SourcePositionStart()
      ));
    }
  }
}
