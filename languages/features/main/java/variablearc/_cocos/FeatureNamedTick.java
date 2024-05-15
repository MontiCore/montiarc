/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeature;

public class FeatureNamedTick implements VariableArcASTArcFeatureCoCo {

  @Override
  public void check(@NotNull ASTArcFeature feature) {

    Preconditions.checkNotNull(feature);
    if (feature.getName().equals("Tick")) {
      Log.error(ArcError.FEATURE_NAMED_TICK.format(feature.getName()), feature.get_SourcePositionStart(), feature.get_SourcePositionEnd());
    }
  }
}