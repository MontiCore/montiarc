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
  for (ASTImportStatementLOCAL impot : node.getImportStatementLOCALList()) {
    imports.put(impot, false);
  }
  if (!node.getSpannedScopeOpt().isPresent()) {
    Log.error("Counld not find Symboltable");
  }
  Scope scope = node.getSpannedScopeOpt().get();
  MontiArcVisitor visitor = new MontiArcVisitor() {
    
    @Override
    public void visit(ASTSimpleReferenceType type) {
      imports.keySet().forEach((impot) -> {
        if (!imports.get(impot)) {
          if (impot.isStar()) {
            
            String fqn = impot.streamImports().collect(Collectors.joining(".")) + "." + type.getName(type.getNameList().size() - 1);
            imports.replace(impot, scope.resolveMany(fqn, JTypeSymbol.KIND).size() > 0);
            
          } else {
            String importName = impot.getImport(impot.getImportList().size() - 1);
            String compName = type.getName(0);
            imports.replace(impot, compName.equals(importName));
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
  };
  
  node.accept(visitor);
  
  imports.forEach((impot, used) -> {
    if (!used) {
      Log.warn(String.format("0xMA011 The import %s is not used!", impot.streamImports().collect(Collectors.joining(".")) + "."), impot.get_SourcePositionStart());
    }
  });
  
}
}
