/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MontiArcSymbolTableCreator extends MontiArcSymbolTableCreatorTOP {

  public MontiArcSymbolTableCreator(@NotNull IMontiArcScope enclosingScope) {
    super(enclosingScope);
  }

  public MontiArcSymbolTableCreator(
    @NotNull Deque<? extends IMontiArcScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public MontiArcArtifactScope createFromAST(@NotNull ASTMACompilationUnit rootNode) {
    Preconditions.checkArgument(rootNode != null);
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTMCImportStatement importStatement : rootNode.getImportStatementList()) {
      imports.add(new ImportStatement(importStatement.getQName(), importStatement.isStar()));
    }
    MontiArcArtifactScope artifactScope = montiarc.MontiArcMill.montiArcArtifactScopeBuilder()
      .setPackageName(rootNode.getPackage().getQName())
      .setImportList(imports)
      .build();
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  @Override
  public void visit(ASTMACompilationUnit node) {
  }

  @Override
  public void endVisit(ASTMACompilationUnit node) {
  }
}