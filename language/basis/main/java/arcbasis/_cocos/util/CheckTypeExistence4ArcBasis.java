/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos.util;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A composed visitor for ArcBasis to check that all symbols to which {@link ASTMCType} refers exist and are resolvable.
 */
public class CheckTypeExistence4ArcBasis implements ICheckTypeExistence{

  protected ArcBasisTraverser traverser;

  public CheckTypeExistence4ArcBasis() {
    init();
  }

  protected void init() {
    this.traverser = ArcBasisMill.traverser();

    CheckTypeExistenceOfBasicTypes checkBasicTypes = new CheckTypeExistenceOfBasicTypes();
    this.traverser.add4MCBasicTypes(checkBasicTypes);
  }

  @Override
  public void checkExistenceOf(@NotNull ASTMCType type) {
    Preconditions.checkNotNull(type);
    type.accept(this.traverser);
  }
}
