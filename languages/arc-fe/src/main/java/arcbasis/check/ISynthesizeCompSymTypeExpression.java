/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A common interface that can be used to synthesize {@link CompSymTypeExpression}s from {@link ASTMCType}.
 */
public interface ISynthesizeCompSymTypeExpression {

  /**
   * Initializes the traverser with the correct visitors and handlers.
   */
  void init();

  MCBasicTypesTraverser getTraverser();

  /**
   * Collects the synthesized {@link CompSymTypeExpression} after using the traverser to traverse the {@link ASTMCType}
   */
  Optional<CompSymTypeExpression> getResult();

  /**
   * Synthesizes a {@link CompSymTypeExpression} from a {@link ASTMCType}
   */
  default Optional<CompSymTypeExpression> synthesizeFrom(@NotNull ASTMCType mcType) {
    Preconditions.checkNotNull(mcType);
    this.init();
    mcType.accept(this.getTraverser());
    return this.getResult();
  }
}
