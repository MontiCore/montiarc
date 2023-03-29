/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MontiArcScopesGenitor extends MontiArcScopesGenitorTOP {

  @Override
  public IMontiArcArtifactScope createFromAST(@NotNull ASTMACompilationUnit root) {
    Preconditions.checkNotNull(root);
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTMCImportStatement importStatement : root.getImportStatementList()) {
      imports.add(new ImportStatement(importStatement.getQName(), importStatement.isStar()));
    }
    IMontiArcArtifactScope as = MontiArcMill.artifactScope();
    as.setPackageName(root.isPresentPackage() ? root.getPackage().getQName() : "");
    as.setImportsList(imports);
    as.setAstNode(root);
    this.putOnStack(as);
    root.accept(getTraverser());
    this.removeCurrentScope();
    return as;
  }
}
