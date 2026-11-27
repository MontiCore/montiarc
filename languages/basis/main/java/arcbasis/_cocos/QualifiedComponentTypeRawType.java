/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.mccomponenttypes._ast.ASTMCQualifiedComponentType;
import de.monticore.types.mccomponenttypes._cocos.MCComponentTypesASTMCQualifiedComponentTypeCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that a generic component type is not used without type arguments.
 *
 * <p>Every non-parameterized component reference is represented by an
 * {@link ASTMCQualifiedComponentType}, independently of whether it occurs in
 * an extends clause, a refinement declaration, or a component instantiation.
 */
public class QualifiedComponentTypeRawType
    implements MCComponentTypesASTMCQualifiedComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTMCQualifiedComponentType node) {
    Preconditions.checkNotNull(node);

    if (node.getDefiningSymbol().isEmpty()) {
      Log.trace(() -> "Skip CoCo check for " + node.get_SourcePositionStart()
          + ", component symbol is missing.",
        this.getClass().getCanonicalName());
      return;
    }

    if (node.getDefiningSymbol().get() instanceof ComponentTypeSymbol component
        && !component.getTypeParameters().isEmpty()) {
      Log.error(ArcError.RAW_USE_OF_PARAMETRIZED_TYPE.format(node.printType()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
