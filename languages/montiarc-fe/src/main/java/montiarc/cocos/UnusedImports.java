/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.monticore.java.javadsl._ast.ASTIdentifierAndTypeArgument;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTImportStatementLOCAL;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcASTMACompilationUnitCoCo;
import montiarc._visitor.MontiArcVisitor;

import java.util.HashMap;
import java.util.stream.Collectors;

public class UnusedImports implements MontiArcASTMACompilationUnitCoCo {

@Override
public void check(ASTMACompilationUnit node) {
  
  HashMap<ASTImportStatementLOCAL, Boolean> imports = new HashMap<>();
  for (ASTImportStatementLOCAL _import : node.getImportStatementLOCALList()) {
    imports.put(_import, false);
  }
  if (!node.getSpannedScopeOpt().isPresent()) {
    Log.error("Could not find symbol table");
  }
  Scope scope = node.getSpannedScopeOpt().get();
  MontiArcVisitor visitor = new ImportUsageFinder(imports, scope);
  
  node.accept(visitor);
  
  imports.forEach((_import, used) -> {
    if (!used) {
      Log.warn(
          String.format("0xMA011 The import %s is not used!",
              _import.streamImports().collect(Collectors.joining(".")) + "."),
          _import.get_SourcePositionStart());
    }
  });
  
}

  private static class ImportUsageFinder implements MontiArcVisitor {

    private final HashMap<ASTImportStatementLOCAL, Boolean> imports;
    private final Scope scope;

    public ImportUsageFinder(HashMap<ASTImportStatementLOCAL, Boolean> imports, Scope scope) {
      this.imports = imports;
      this.scope = scope;
    }

    @Override
    public void visit(ASTSimpleReferenceType type) {
      imports.keySet().forEach((_import) -> {
        if (!imports.get(_import)) {
          if (_import.isStar()) {

            String fqn = _import.streamImports().collect(Collectors.joining(".")) + "." + type.getName(type.getNameList().size() - 1);
            imports.replace(_import, scope.resolveMany(fqn, JTypeSymbol.KIND).size() > 0);

          } else {
            String importName = _import.getImport(_import.getImportList().size() - 1);
            String compName = type.getName(0);
            imports.replace(_import, compName.equals(importName));
          }
        }
      });
    }

    @Override
    public void visit(ASTIdentifierAndTypeArgument type) {
      imports.keySet().forEach((impot) -> {
        if (!imports.get(impot)) {
          if (impot.isStar()) {

            String fqn = impot.streamImports().collect(Collectors.joining(".")) + "." + type.getName();
            imports.replace(impot, scope.resolveMany(fqn, JTypeSymbol.KIND).size() > 0);

          } else {
            String importName = impot.getImport(impot.getImportList().size() - 1);
            String compName = type.getName();
            imports.replace(impot, compName.equals(importName));
          }
        }
      });
    }

    public void visit(ASTNameExpression type){
      imports.keySet().forEach((impot) -> {
        if (!imports.get(impot)) {
          if (impot.isStar()) {

            String fqn = impot.streamImports().collect(Collectors.joining(".")) + "." + type.getName();
            imports.replace(impot, scope.resolveMany(fqn, JTypeSymbol.KIND).size() > 0);

          } else {
            String importName = impot.getImport(impot.getImportList().size() - 1);
            String compName = type.getName();
            imports.replace(impot, compName.equals(importName));
          }
        }
      });
    }
  }
}
