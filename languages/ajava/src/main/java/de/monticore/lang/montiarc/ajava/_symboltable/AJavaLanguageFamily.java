package de.monticore.lang.montiarc.ajava._symboltable;

import de.monticore.ModelingLanguageFamily;
import de.monticore.automaton.ioautomatonjava._symboltable.IOAutomatonJavaLanguage;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.umlcd4a.CD4AnalysisLanguage;

public class AJavaLanguageFamily extends ModelingLanguageFamily {
  public AJavaLanguageFamily() {
    addModelingLanguage(new AJavaLanguage());
    addModelingLanguage(new CD4AnalysisLanguage());
    addModelingLanguage(new JavaDSLLanguage());
    addModelingLanguage(new IOAutomatonJavaLanguage());
  }
}