/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis._cocos.util.CheckTypeExistence4ArcBasis;
import arcbasis._cocos.util.ICheckTypeExistence;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks for each configuration parameter whether its type exists.
 */
public class ParameterTypeExists implements ArcBasisASTArcParameterCoCo {

  /** Visitor used to check whether all symbols to which ast types refer to exist. */
  protected ICheckTypeExistence existenceChecker;

  public ParameterTypeExists() {
    this(new CheckTypeExistence4ArcBasis());
  }

  public ParameterTypeExists(@NotNull ICheckTypeExistence existenceChecker) {
    this.existenceChecker = Preconditions.checkNotNull(existenceChecker);
  }

  @Override
  public void check(@NotNull ASTArcParameter node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope(), "ASTArcParameter node '%s' at '%s' has no enclosing scope. "
        + "Did you forget to run the scopes genitor before checking cocos?", node.getName(),
      node.get_SourcePositionStart());

    existenceChecker.checkExistenceOf(node.getMCType());
  }
}