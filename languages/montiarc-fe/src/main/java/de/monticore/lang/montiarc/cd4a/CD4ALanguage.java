package de.monticore.lang.montiarc.cd4a;

import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;

// TODO Required for performing CoCo checks of CD4A models whenever they are
// loaded. Therefore a modified version of CD4AnalysisModelLoader is required.
// Remove this class if there is a more elegant way in the future.
public class CD4ALanguage extends CD4AnalysisLanguage {
  
  @Override
  protected CD4AnalysisModelLoader provideModelLoader() {
    return new CD4AModelLoader(this);
  }
}
