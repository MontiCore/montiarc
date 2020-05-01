/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import montiarc.helper.CD4AModelLoader;

/**
 * TODO: Write me!
 *
 *          $Date$
 *
 */
public class MontiArcCD4AModelLoader extends CD4AnalysisModelLoader {

  /**
   * Constructor for montiarc._symboltable.MontiArcCD4AModelloader
   * @param language
   */
  public MontiArcCD4AModelLoader(CD4AnalysisLanguage language) {
    super(language);
  }
  
  /**
   * @see de.monticore.umlcd4a.CD4AnalysisModelLoader#createSymbolTableFromAST(de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit, java.lang.String, de.monticore.symboltable.MutableScope, de.monticore.symboltable.ResolvingConfiguration)
   */
  @Override
  protected void createSymbolTableFromAST(ASTCDCompilationUnit ast, String modelName,
      MutableScope enclosingScope, ResolvingConfiguration resolvingConfiguration) {
    super.createSymbolTableFromAST(ast, modelName, enclosingScope, resolvingConfiguration);
    new MontiArcCD4ACoCos().getCheckerForAllCoCos().checkAll(ast);
  }
  
  
}
