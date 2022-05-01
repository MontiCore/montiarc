/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * For imports, checks that
 * 1) There is a scope structure matching the package path to the imported symbol(s)
 * 2) Imported symbols exist.
 *
 * Note: 1) is not activated currently.
 */
public class UnresolvableImport implements MontiArcASTMACompilationUnitCoCo {

  protected static final Predicate<TypeSymbol> isNoTypeVar = type -> !(type instanceof TypeVarSymbol);

  @Override
  public void check(@NotNull ASTMACompilationUnit node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getSpannedScope(), "Cannot check coco '%s'. Have you run the scopes genitor " +
      "before checking cocos?", this.getClass().getSimpleName());

    node.getImportStatementList().forEach(this::checkImportResolvable);
  }

  protected void checkImportResolvable(@NotNull ASTMCImportStatement astImport) {
    Preconditions.checkNotNull(astImport);
    Preconditions.checkState(astImport.getEnclosingScope() instanceof IMontiArcScope);

    IMontiArcScope enclScope = (IMontiArcScope) astImport.getEnclosingScope();

    if(!astImport.isStar()) {
      String printedImport = astImport.getQName();

      List<ISymbol> resolvedSymbols = new ArrayList<>();
      resolvedSymbols.addAll( enclScope.resolveTypeMany(printedImport, isNoTypeVar) );
      resolvedSymbols.addAll( enclScope.resolveComponentTypeMany(printedImport) );
      resolvedSymbols.addAll( enclScope.resolveFieldMany(printedImport, FieldSymbol::isIsStatic) );
      resolvedSymbols.addAll( enclScope.resolveMethodMany(printedImport, MethodSymbol::isIsStatic) );
      resolvedSymbols.addAll( this.resolveStaticMembersRobust(astImport.getMCQualifiedName()) );

      if(resolvedSymbols.isEmpty()) {
        Log.error(ArcError.UNRESOLVABLE_IMPORT.format(printedImport),
          astImport.get_SourcePositionStart(), astImport.get_SourcePositionEnd());
      }
    }
  }

  /**
   * If the qualified name has at least two name parts, then this method assumes that the last one is a public static
   * field or method and searches for such a member in the preceding part of the import statement. If there are less
   * than two name parts, or no such static field / method is found, then this method returns an empty list.
   */
  protected List<ISymbol> resolveStaticMembersRobust(@NotNull ASTMCQualifiedName astName) {
    Preconditions.checkNotNull(astName);
    Preconditions.checkState(astName.getEnclosingScope() instanceof IMontiArcScope);

    IMontiArcScope enclScope = (IMontiArcScope) astName.getEnclosingScope();
    String printedImport = astName.getQName();

    List<ISymbol> robustResolvedMembers = new ArrayList<>();

    if(astName.getPartsList().size() >= 2) {
      int nameLen = printedImport.length();
      String memberName = astName.getBaseName();
      String owner = astName.getQName().substring(0, nameLen - memberName.length() - 1); // -1 to remove the .dot

      List<TypeSymbol> owners = enclScope.resolveTypeMany(owner);

      owners.stream()
        .flatMap(o -> o.getFunctionList(memberName).stream())
        .filter(MethodSymbol.class::isInstance).map(MethodSymbol.class::cast)
        .filter(MethodSymbol::isIsStatic)
        .filter(MethodSymbol::isIsPublic)
        .forEach(robustResolvedMembers::add);

      owners.stream()
        .flatMap(o -> o.getVariableList(memberName).stream())
        .filter(FieldSymbol.class::isInstance).map(FieldSymbol.class::cast)
        .filter(FieldSymbol::isIsStatic)
        .filter(FieldSymbol::isIsPublic)
        .forEach(robustResolvedMembers::add);
    }

    return robustResolvedMembers;
  }
}
