package de.monticore.lang.montiarc.montiarcautomaton.cdadapter;

import de.monticore.ModelingLanguageFamily;
import de.monticore.automaton.ioautomatonjava._symboltable.IOAutomatonJavaLanguage;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.lang.montiarc.montiarcautomaton._symboltable.MontiArcAutomatonLanguage;
import de.monticore.umlcd4a.CD4AnalysisLanguage;

//TODO required for resolving types of an expression that uses cd stuff. See
//de.monticore.automaton.ioautomaton.TypeCompatibilityChecker for further
//information.
public class MontiArcAutomatonLanguageFamilyWithCDAdapter extends ModelingLanguageFamily {
  public MontiArcAutomatonLanguageFamilyWithCDAdapter() {
    addModelingLanguage(new MontiArcAutomatonLanguage() {
      @Override
      protected void initResolvingFilters() {
        super.initResolvingFilters();
       
        // TODO currently required to resolve types of expressions with cd in MontiArcAutomaton
        addResolver(new CDTypeSymbol2JavaTypeFilter());
        addResolver(new CDFieldSymbol2JavaFieldFilter());
      }
    });
    addModelingLanguage(new CD4AnalysisLanguage());
    addModelingLanguage(new JavaDSLLanguage());
    addModelingLanguage(new IOAutomatonJavaLanguage());
  }
}