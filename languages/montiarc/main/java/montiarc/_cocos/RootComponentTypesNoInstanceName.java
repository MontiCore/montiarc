/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTComponentType;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * [Hab16] B2:Top-level component type definitions do not have instance names. An inner component type may directly be
 * instantiated by declaring instantiation names (and parameter bindings) between the defined component type's head and
 * body. However, this is illegal for root component types that are not nested in other component types. This coco
 * checks for this well-formedness rule.
 *
 * @see ASTComponentType
 */
public class RootComponentTypesNoInstanceName implements MontiArcASTMACompilationUnitCoCo {

  @Override
  public void check(@NotNull ASTMACompilationUnit node) {
    Preconditions.checkNotNull(node);

    ASTComponentType rootComp = node.getComponentType();

    if (!rootComp.getComponentInstanceList().isEmpty()) {
      String packageName = "UNKNOWN";
      if (node.isPresentPackage()) {
        packageName = node.getPackage().getQName();
      }

      Log.error(ArcError.ROOT_COMPONENT_TYPES_MISS_INSTANCE_NAMES.format(
  packageName + "." + rootComp.getName(), rootComp.getInstanceName(0)),
        node.get_SourcePositionStart());
    }
  }
}
