/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.types.check.CompKindCheckResult;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesHandler;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;
import de.se_rwth.commons.logging.Log;
import org.jspecify.annotations.NonNull;

public class ArcBasisSynthesizeCompKindFromMCCollectionTypes implements MCCollectionTypesHandler {

  protected MCCollectionTypesTraverser traverser;

  /**
   * Common state with other visitors, if this visitor is part of a visitor composition.
   */
  protected CompKindCheckResult resultWrapper;

  public ArcBasisSynthesizeCompKindFromMCCollectionTypes(@NonNull CompKindCheckResult resultWrapper) {
    this.resultWrapper = Preconditions.checkNotNull(resultWrapper);
  }

  @Override
  public MCCollectionTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NonNull MCCollectionTypesTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  @Override
  public void handle(ASTMCMapType node) {
    Log.error(String.format("0xD0104 Cannot resolve component '%s'", node.printType()),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void handle(ASTMCSetType node) {
    Log.error(String.format("0xD0104 Cannot resolve component '%s'", node.printType()),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void handle(ASTMCOptionalType node) {
    Log.error(String.format("0xD0104 Cannot resolve component '%s'", node.printType()),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }

  @Override
  public void handle(ASTMCListType node) {
    Log.error(String.format("0xD0104 Cannot resolve component '%s'", node.printType()),
      node.get_SourcePositionStart(), node.get_SourcePositionEnd()
    );
  }
}
