/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import montiarc.util.NameCapitalizationHelper;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeature;

/**
 * Convention: Checks that features start with a lower-case letter.
 */
public class FeatureNameCapitalization implements VariableArcASTArcFeatureCoCo {

  @Override
  public void check(@NotNull ASTArcFeature feature) {
    Preconditions.checkNotNull(feature);
    if (NameCapitalizationHelper.isNotLowerCase(feature.getName())) {
      NameCapitalizationHelper.warning(VariableArcError.FEATURE_LOWER_CASE, feature);
    }
  }
}