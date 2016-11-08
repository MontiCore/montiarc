package de.monticore.automaton.ioautomatonjava._symboltable;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class IOAutomatonJavaModelLoader extends IOAutomatonJavaModelLoaderTOP {

  public IOAutomatonJavaModelLoader(IOAutomatonJavaLanguage language) {
    super(language);
  }
  
  @Override
  protected void createSymbolTableFromAST(final de.monticore.automaton.ioautomatonjava._ast.ASTIOACompilationUnit ast, final String modelName,
    final MutableScope enclosingScope, final ResolvingConfiguration resolverConfiguration) {
    final IOAutomatonJavaSymbolTableCreator symbolTableCreator =
            getModelingLanguage().getSymbolTableCreator(resolverConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          IOAutomatonJavaModelLoader.class.getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("0xA7001_904 Top scope of model " + modelName + " is expected to be an artifact scope, but"
          + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", IOAutomatonJavaModelLoader.class.getSimpleName());
    }
    else {
      Log.warn("0xA7002_904 No symbol created, because '" + getModelingLanguage().getName()
        + "' does not define a symbol table creator.");
    }
  }
  
}
