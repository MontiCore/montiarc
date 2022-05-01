/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._symboltable.IArcBasisScope;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor2;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A visitor for MCCollectionTypes to check that all symbols to which {@link ASTMCType} refers exist and are resolvable.
 */
public class CheckTypeExistenceOfCollectionTypes implements MCCollectionTypesVisitor2 {

  @Override
  public void visit(@NotNull ASTMCListType type) {
    Preconditions.checkNotNull(type);
    checkCollectionTypeOfName("List", type); // Type Args will automatically be checked during further visiting.
  }

  @Override
  public void visit(@NotNull ASTMCSetType type) {
    Preconditions.checkNotNull(type);
    checkCollectionTypeOfName("Set", type); // Type Args will automatically be checked during further visiting.
  }

  @Override
  public void visit(@NotNull ASTMCOptionalType type) {
    Preconditions.checkNotNull(type);
    checkCollectionTypeOfName("Optional", type);
    // Type Args will automatically be checked during further visiting.
  }

  @Override
  public void visit(@NotNull ASTMCMapType type) {
    Preconditions.checkNotNull(type);
    checkCollectionTypeOfName("Map", type); // Type Args will automatically be checked during further visiting.
  }

  /**
   * Checks whether the Collection type with name {@code name} is resolvable when starting from the ast node
   * {@code resolveStartPoint}
   */
  protected void checkCollectionTypeOfName(@NotNull String name, @NotNull ASTNode resolveStartPoint) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(resolveStartPoint);
    Preconditions.checkNotNull(resolveStartPoint.getEnclosingScope(), "ASTMCType '%s' at '%s' has no enclosing scope. "
        + "Did you forget to run the scopes genitor before checking cocos?", name,
      resolveStartPoint.get_SourcePositionStart());
    Preconditions.checkArgument(resolveStartPoint.getEnclosingScope() instanceof IBasicSymbolsScope);

    IArcBasisScope enclScope = (IArcBasisScope) resolveStartPoint.getEnclosingScope();
    Optional<TypeSymbol> typeSym = enclScope.resolveType(name);

    if(!typeSym.isPresent()) {
      Log.error(ArcError.MISSING_TYPE.format(name, resolveStartPoint.get_SourcePositionStart()));
    }
  }
}
