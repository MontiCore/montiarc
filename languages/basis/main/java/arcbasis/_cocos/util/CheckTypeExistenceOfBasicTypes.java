/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos.util;

import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * A visitor for MCBasicTypes to check that all symbols to which {@link ASTMCType} refers exist and are resolvable.
 */
public class CheckTypeExistenceOfBasicTypes implements MCBasicTypesVisitor2 {

  @Override
  public void visit(@NotNull ASTMCPrimitiveType type) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(type.getEnclosingScope());
    Preconditions.checkArgument(type.getEnclosingScope() instanceof IBasicSymbolsScope);

    IArcBasisScope enclScope = (IArcBasisScope) type.getEnclosingScope();
    String typeName = type.printType();
    Optional<TypeSymbol> primitiveSym = enclScope.resolveTypeMany(typeName).stream().findFirst();

    if(primitiveSym.isEmpty()) {
      Log.error(ArcError.MISSING_TYPE.format(typeName, type.get_SourcePositionStart()), type.get_SourcePositionStart(), type.get_SourcePositionEnd());
    }
  }

  @Override
  public void visit(@NotNull ASTMCQualifiedType type) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(type.getEnclosingScope());
    Preconditions.checkArgument(type.getEnclosingScope() instanceof IBasicSymbolsScope);

    IArcBasisScope enclScope = (IArcBasisScope) type.getEnclosingScope();
    String typeName = type.getMCQualifiedName().getQName();
    Optional<TypeSymbol> typeSym = enclScope.resolveTypeMany(typeName).stream().findFirst();

    if(typeSym.isEmpty()) {
      Log.error(ArcError.MISSING_TYPE.format(typeName, type.get_SourcePositionStart()), type.get_SourcePositionStart(), type.get_SourcePositionEnd());
    }
  }
}
