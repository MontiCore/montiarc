/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.timing.Timing;
import arcbasis.timing.TimingCollector;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.umlstereotype._cocos.UMLStereotypeASTStereotypeCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;

import java.util.List;

/**
 * CoCo for checking that only one Timing is specified
 */
public class OnlyOneTiming implements UMLStereotypeASTStereotypeCoCo {

  @Override
  public void check(ASTStereotype node) {
    List<Timing> timings = TimingCollector.getTimings(node);

    if (timings.size() > 1) {
      Log.error(ArcError.TIMING_MULTIPLE.format(timings.size()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
