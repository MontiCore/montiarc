/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc._symboltable;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.MontiArcLanguage;
import montiarc._symboltable.MontiArcSymbolTableCreator;

public class DynamicMontiArcModelLoader extends DynamicMontiArcModelLoaderTOP {

  public DynamicMontiArcModelLoader(DynamicMontiArcLanguage language) {
    super(language);
  }

  @Override
  protected void createSymbolTableFromAST(final montiarc._ast.ASTMACompilationUnit ast, final String modelName,
    final MutableScope enclosingScope, final ResolvingConfiguration resolvingConfiguration) {
    final DynamicMontiArcSymbolTableCreator symbolTableCreator =
            getModelingLanguage().getSymbolTableCreator(resolvingConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          DynamicMontiArcModelLoader.class.getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("0xA7FC1 Top scope of model " + modelName + " is expected to be an artifact scope, but"
          + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", DynamicMontiArcModelLoader.class.getSimpleName());
    }
    else {
      Log.warn("0xA7FC2 No symbol created, because '" + getModelingLanguage().getName()
        + "' does not define a symbol table creator.");
    }
  }
}
