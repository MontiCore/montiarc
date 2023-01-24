/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import com.google.common.base.Preconditions;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.umlstereotype._cocos.UMLStereotypeASTStereotypeCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.Timing;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that stereotypes do not contain multiple timing annotations.
 */
public class OnlyOneTiming implements UMLStereotypeASTStereotypeCoCo {

  @Override
  public void check(@NotNull ASTStereotype node) {
    Preconditions.checkNotNull(node);

    final List<ASTStereoValue> timings = node.getValuesList().stream()
      .filter(v -> Timing.contains(v.getName()))
      .collect(Collectors.toUnmodifiableList());

    if (timings.size() > 1) {
      Log.error(ArcError.TIMING_MULTIPLE.toString(),
        timings.get(1).get_SourcePositionStart(),
        timings.get(1).get_SourcePositionEnd()
      );
    }
  }
}
