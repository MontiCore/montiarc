/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._cocos.util.CheckTypeExistence4ArcBasis;
import arcbasis._cocos.util.ICheckTypeExistence;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks for each variable whether it type exists.
 */
public class FieldTypeExists implements ArcBasisASTArcFieldDeclarationCoCo {

  /** Visitor used to check whether all symbols to which ast types refer to exist. */
  protected ICheckTypeExistence existenceChecker;

  public FieldTypeExists() {
    this(new CheckTypeExistence4ArcBasis());
  }

  public FieldTypeExists(@NotNull ICheckTypeExistence existenceChecker) {
    this.existenceChecker = Preconditions.checkNotNull(existenceChecker);
  }

  @Override
  public void check(@NotNull ASTArcFieldDeclaration node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(!node.getArcFieldList().isEmpty());
    Preconditions.checkNotNull(node.getEnclosingScope());

    existenceChecker.checkExistenceOf(node.getMCType());
  }
}