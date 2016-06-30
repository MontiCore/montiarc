package de.monticore.lang.montiarc.montiarc._symboltable;

import de.monticore.ModelingLanguageFamily;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguage;
import de.monticore.umlcd4a.CD4AnalysisLanguage;

public class MaWithCdLanguageFamily extends ModelingLanguageFamily {
  public MaWithCdLanguageFamily() {
    addModelingLanguage(new MontiArcLanguage());
    addModelingLanguage(new CD4AnalysisLanguage());
  }
}
