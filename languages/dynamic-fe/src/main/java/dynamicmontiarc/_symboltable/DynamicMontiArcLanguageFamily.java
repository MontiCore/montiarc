/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc._symboltable;

import de.monticore.ModelingLanguageFamily;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import montiarc._symboltable.MontiArcCD4ALanguage;
import montiarc._symboltable.MontiArcLanguage;

/**
 * The MontiArcLanguageFamily aggregates common languages for MontiArc usage; these are
 * {@link MontiArcLanguage}, {@link CD4AnalysisLanguage} and the {@link JavaDSLLanguage}.
 *
 */
public class DynamicMontiArcLanguageFamily extends ModelingLanguageFamily {

  public DynamicMontiArcLanguageFamily() {
    addModelingLanguage(new DynamicMontiArcLanguage());
    addModelingLanguage(new JavaDSLLanguage());
    
    MontiArcCD4ALanguage cdlang = new MontiArcCD4ALanguage();
    cdlang.addResolvingFilter(new CommonResolvingFilter<JTypeSymbol>(JTypeSymbol.KIND));
    cdlang.addResolvingFilter(new CommonResolvingFilter<JFieldSymbol>(JFieldSymbol.KIND));
    addModelingLanguage(cdlang);
    
  }
  
}
