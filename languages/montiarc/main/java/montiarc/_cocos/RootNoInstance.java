/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * [Hab16] B2:Top-level component type definitions do not have instance names. An inner component type may directly be
 * instantiated by declaring instantiation names (and parameter bindings) between the defined component type's head and
 * body. However, this is illegal for root component types that are not nested in other component types. This coco
 * checks for this well-formedness rule.
 */
public class RootNoInstance implements MontiArcASTMACompilationUnitCoCo {

  @Override
  public void check(@NotNull ASTMACompilationUnit node) {
    Preconditions.checkNotNull(node);

    if (!node.getComponentType().getComponentInstanceList().isEmpty()) {
      Log.error(MontiArcError.ROOT_NO_INSTANCE.toString(),
        node.getComponentType().getComponentInstance(0).get_SourcePositionStart(),
        node.getComponentType().getComponentInstance(0).get_SourcePositionEnd()
      );
    }
  }
}
