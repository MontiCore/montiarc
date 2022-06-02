/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._cocos.util.CheckTypeExistenceOfBasicTypes;
import arcbasis._cocos.util.ICheckTypeExistence;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A composed visitor for MontiArc to check that all symbols to which {@link ASTMCType} refers exist and are resolvable.
 */
public class CheckTypeExistence4MontiArc implements ICheckTypeExistence {

  protected MontiArcTraverser traverser;

  public CheckTypeExistence4MontiArc() {
    init();
  }

  protected void init() {
    this.traverser = MontiArcMill.traverser();

    CheckTypeExistenceOfBasicTypes checkBasicTypes = new CheckTypeExistenceOfBasicTypes();
    CheckTypeExistenceOfCollectionTypes checkCollectionTypes = new CheckTypeExistenceOfCollectionTypes();
    CheckTypeExistenceOfSimpleGenericTypes checkSimpleGenerics = new CheckTypeExistenceOfSimpleGenericTypes();
    this.traverser.add4MCBasicTypes(checkBasicTypes);
    this.traverser.add4MCCollectionTypes(checkCollectionTypes);
    this.traverser.add4MCSimpleGenericTypes(checkSimpleGenerics);
  }

  @Override
  public void checkExistenceOf(@NotNull ASTMCType type) {
    Preconditions.checkNotNull(type);
    type.accept(this.traverser);
  }
}
