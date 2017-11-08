package de.monticore.automaton.ioautomatonjava._symboltable;

import de.monticore.ModelingLanguageFamily;
import de.monticore.java.lang.JavaDSLLanguage;

public class IOAutomatonJavaLanguageFamily extends ModelingLanguageFamily {
  public IOAutomatonJavaLanguageFamily() {
    addModelingLanguage(new IOAutomatonJavaLanguage());
    addModelingLanguage(new JavaDSLLanguage());
  }
}
