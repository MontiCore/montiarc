package de.monticore.lang.montiarc.helper;

import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;

public class CD4ALanguage extends CD4AnalysisLanguage {
  
  @Override
  protected CD4AnalysisModelLoader provideModelLoader() {
    return new CD4AModelLoader(this);
  }
}
