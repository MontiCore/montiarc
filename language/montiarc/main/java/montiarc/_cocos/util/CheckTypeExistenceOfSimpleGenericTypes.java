/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._symboltable.IArcBasisScope;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor2;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A visitor for MCSimpleGenericTypes to check that all symbols to which {@link ASTMCType} refers exist and are
 * resolvable.
 */
public class CheckTypeExistenceOfSimpleGenericTypes implements MCSimpleGenericTypesVisitor2 {

  @Override
  public void visit(@NotNull ASTMCBasicGenericType type) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(type.getEnclosingScope(), "ASTMCType node '%s' at '%s' has no enclosing scope. "
        + "Did you forget to run the scopes genitor before checking cocos?",
      Joiners.DOT.join(type.getNameList()), type.get_SourcePositionStart());
    Preconditions.checkArgument(type.getEnclosingScope() instanceof IBasicSymbolsScope);

    IArcBasisScope enclScope = (IArcBasisScope) type.getEnclosingScope();
    String typeName = Joiners.DOT.join(type.getNameList());
    Optional<TypeSymbol> typeSym = enclScope.resolveType(typeName);

    if(!typeSym.isPresent()) {
      Log.error(ArcError.MISSING_TYPE.format(typeName, type.get_SourcePositionStart()));
    }
  }
}
