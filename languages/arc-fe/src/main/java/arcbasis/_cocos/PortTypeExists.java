/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTPortDeclaration;
import arcbasis._cocos.util.CheckTypeExistence4ArcBasis;
import arcbasis._cocos.util.ICheckTypeExistence;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks for each port whether its type exists.
 */
public class PortTypeExists implements ArcBasisASTPortDeclarationCoCo {

  /** Visitor used to check whether all symbols to which ast types refer to exist. */
  protected ICheckTypeExistence existenceChecker;

  public PortTypeExists() {
    this(new CheckTypeExistence4ArcBasis());
  }

  public PortTypeExists(@NotNull ICheckTypeExistence existenceChecker) {
    this.existenceChecker = Preconditions.checkNotNull(existenceChecker);
  }

  @Override
  public void check(@NotNull ASTPortDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(!node.getPortList().isEmpty());
    Preconditions.checkNotNull(node.getEnclosingScope(), "ASTPortDeclaration node '%s' at '%s' has no enclosing scope. "
      + "Did you forget to run the scopes genitor before checking cocos?", node.getPort(0).getName(),
      node.get_SourcePositionStart());

    existenceChecker.checkExistenceOf(node.getMCType());
  }
}