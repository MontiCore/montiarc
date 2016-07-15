package de.monticore.lang.montiarc.montiarc._symboltable;

import de.monticore.ModelingLanguageFamily;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.umlcd4a.CD4AnalysisLanguage;

/**
 * The MontiArcLanguageFamily aggregates common languages for MontiArc usage; these are
 * {@link MontiArcLanguage}, {@link CD4AnalysisLanguage} and the {@link JavaDSLLanguage}.
 *
 * @author Robert Heim
 */
public class MontiArcLanguageFamily extends ModelingLanguageFamily {
  public MontiArcLanguageFamily() {
    addModelingLanguage(new MontiArcLanguage());
    addModelingLanguage(new CD4AnalysisLanguage());
    addModelingLanguage(new JavaDSLLanguage());
  }
}
