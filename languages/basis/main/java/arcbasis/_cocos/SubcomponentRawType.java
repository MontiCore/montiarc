/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentInstantiation;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.mccomponenttypes._ast.ASTMCGenericComponentType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;

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

    if (node.getMCComponentType().getDefiningSymbol().isEmpty()) {
      Log.trace(() -> "Skip coco check for " + node.get_SourcePositionStart() + ", component link is missing.", this.getClass().getCanonicalName());
    } else if (node.getMCComponentType().getDefiningSymbol().isPresent() &&
      node.getMCComponentType().getDefiningSymbol().get() instanceof ComponentTypeSymbol &&
      ((ComponentTypeSymbol) node.getMCComponentType().getDefiningSymbol().get()).getTypeParameters().size() > 0
      && !(node.getMCComponentType() instanceof ASTMCGenericComponentType)) {
      Log.warn(ArcError.RAW_USE_OF_PARAMETRIZED_TYPE.format(node.getMCComponentType().printType()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
