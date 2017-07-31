package de.monticore.lang.montiarc.montiarcautomaton.cdadapter;

import de.monticore.ModelingLanguageFamily;
import de.monticore.automaton.ioautomatonjava._symboltable.IOAutomatonJavaLanguage;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.lang.montiarc.adapter.CDFieldSymbol2JavaFieldFilter;
import de.monticore.lang.montiarc.adapter.CDTypeSymbol2JavaTypeFilter;
import de.monticore.lang.montiarc.helper.CD4ALanguage;
import de.monticore.lang.montiarc.montiarcautomaton._symboltable.MontiArcAutomatonLanguage;

// TODO required for resolving types of an expression that uses cd stuff. See
// de.monticore.automaton.ioautomaton.TypeCompatibilityChecker for further
// information.
// This class also adds a modified version of CD4ALanguage as modeling language
// that performs on-the-fly coco checks. For further information see: CD4ALanguage
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
    
    // Use CD4ALanguage instead of CD4AnalysisLanguage. see CD4ALanguage.
    // Required for on-the-fly coco checks.
    addModelingLanguage(new CD4ALanguage());
    // addModelingLanguage(new CD4AnalysisLanguage());
    
    addModelingLanguage(new JavaDSLLanguage());
    addModelingLanguage(new IOAutomatonJavaLanguage());
  }
}