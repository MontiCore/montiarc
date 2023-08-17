/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import arcbasis._ast.ASTComponentHead;
import arcbasis._cocos.ArcBasisASTComponentHeadCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.GenericArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A generic component may be used in raw form, i.e., without providing
 * arguments for its type parameters. We discourage raw usage of generic
 * components. This context-condition logs a warning when a component
 * extends a generic component in raw form.
 */
public class ComponentHeritageRawType implements ArcBasisASTComponentHeadCoCo {

  @Override
  public void check(ASTComponentHead node) {
    Preconditions.checkNotNull(node);

    if (!node.isPresentParent()) {
      Log.trace("Skip coco check for " + node.get_SourcePositionStart() + ", no parent component.", this.getClass().getCanonicalName());
    } else if (node.getParent().getDefiningSymbol().isEmpty()) {
      Log.trace("Skip coco check for " + node.get_SourcePositionStart() + ", parent link is missing.", this.getClass().getCanonicalName());
    } else if (node.getParent().getDefiningSymbol().get() instanceof ComponentTypeSymbol &&
      ((ComponentTypeSymbol) node.getParent().getDefiningSymbol().get()).getTypeParameters().size() > 0
      && !(node.getParent() instanceof ASTMCBasicGenericType)) {
      Log.warn(GenericArcError.RAW_USE_OF_PARAMETRIZED_TYPE.format(node.getParent().printType()),
        node.get_SourcePositionStart()
      );
    }
  }
}