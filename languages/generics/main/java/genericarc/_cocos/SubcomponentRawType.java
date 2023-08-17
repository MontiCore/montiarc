/* (c) https://github.com/MontiCore/monticore */
package genericarc._cocos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._cocos.ArcBasisASTComponentInstantiationCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.GenericArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A generic component may be used in raw form, i.e., without providing
 * arguments for its type parameters. We discourage raw usage of generic
 * components. This context-condition logs a warning when a generic
 * component is used in raw form as subcomponent.
 */
public class SubcomponentRawType implements ArcBasisASTComponentInstantiationCoCo {

  @Override
  public void check(ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);

    if (node.getMCType().getDefiningSymbol().isEmpty()) {
      Log.trace("Skip coco check for " + node.get_SourcePositionStart() + ", component link is missing.", this.getClass().getCanonicalName());
    } else if (node.getMCType().getDefiningSymbol().isPresent() &&
      node.getMCType().getDefiningSymbol().get() instanceof ComponentTypeSymbol &&
      ((ComponentTypeSymbol) node.getMCType().getDefiningSymbol().get()).getTypeParameters().size() > 0
      && !(node.getMCType() instanceof ASTMCBasicGenericType)) {
      Log.warn(GenericArcError.RAW_USE_OF_PARAMETRIZED_TYPE.format(node.getMCType().printType()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
