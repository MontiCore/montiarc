package montiarc._symboltable;

import de.monticore.ModelingLanguageFamily;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import montiarc.helper.CD4ALanguage;

/**
 * The MontiArcLanguageFamily aggregates common languages for MontiArc usage; these are
 * {@link MontiArcLanguage}, {@link CD4AnalysisLanguage} and the {@link JavaDSLLanguage}.
 *
 * @author Robert Heim, Andreas Wortmann
 */
public class MontiArcLanguageFamily extends ModelingLanguageFamily {
	
  public MontiArcLanguageFamily() {
    addModelingLanguage(new MontiArcLanguage());
    addModelingLanguage(new JavaDSLLanguage());
    // Use CD4ALanguage instead of CD4AnalysisLanguage. see CD4ALanguage.
    // Required for on-the-fly coco checks.

    addModelingLanguage(new CD4ALanguage());

    
  }

  
}
