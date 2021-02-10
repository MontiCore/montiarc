/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc.util.NameCapitalizationHelper;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * {@code implements} [Hab16] CV1: Instance names start with a lower-case letter.
 * (p. 71, Lst. 3.51)
 */
public class InstanceNameCapitalisation implements ArcBasisASTComponentInstantiationCoCo {

  @Override
  public void check(@NotNull ASTComponentInstantiation instantiation) {
    Preconditions.checkArgument(instantiation != null);
    instantiation.getInstancesNames().forEach(name -> {
      if(NameCapitalizationHelper.isNotLowerCase(name)) {
        NameCapitalizationHelper.warning(ArcError.INSTANCE_NAME_LOWER_CASE, instantiation, name);
      }
    });
  }
}