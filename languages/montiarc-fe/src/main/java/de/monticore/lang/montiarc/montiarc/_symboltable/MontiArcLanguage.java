/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.LinkedHashSet;

import com.google.common.collect.ImmutableSet;
import de.monticore.ast.ASTNode;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbolCreator;
import de.monticore.lang.montiarc.tagging._symboltable.TagableModelingLanguage;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.types.JAttributeSymbol;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;

/**
 * The MontiArc Language
 *
 * @author Robert Heim, Michael von Wenckstern
 */
public class MontiArcLanguage extends MontiArcLanguageTOP implements TagableModelingLanguage {

  public static final String FILE_ENDING = "arc";

  protected LinkedHashSet<TagSymbolCreator> tagSymbolCreators = new LinkedHashSet<>();

  public MontiArcLanguage() {
    super("MontiArc Language", FILE_ENDING);
  }

  @Override
  protected void initResolvingFilters() {
    super.initResolvingFilters();
    // is done in generated TOP-language addResolver(new
    // CommonResolvingFilter<ComponentSymbol>(ComponentSymbol.class, ComponentSymbol.KIND));
    addResolver(new CommonResolvingFilter<ComponentInstanceSymbol>(ComponentInstanceSymbol.KIND));
    addResolver(new CommonResolvingFilter<PortSymbol>(PortSymbol.KIND));
    addResolver(new CommonResolvingFilter<ConnectorSymbol>(ConnectorSymbol.KIND));
    addResolver(new CommonResolvingFilter<ExpandedComponentInstanceSymbol>(ExpandedComponentInstanceSymbol.KIND));

    addResolver(CommonResolvingFilter.create(JTypeSymbol.KIND));
    addResolver(CommonResolvingFilter.create(JAttributeSymbol.KIND));
    addResolver(CommonResolvingFilter.create(JMethodSymbol.KIND));

    setModelNameCalculator(new MontiArcModelNameCalculator());
  }

  public void addTagSymbolCreator(TagSymbolCreator tagSymbolCreator) {
    this.tagSymbolCreators.add(tagSymbolCreator);
  }

  public ImmutableSet<TagSymbolCreator> getTagSymbolCreators() {
    return ImmutableSet.copyOf(this.tagSymbolCreators);
  }

  /**
   * @see de.monticore.CommonModelingLanguage#provideModelLoader()
   */
  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new MontiArcModelLoader(this);
  }
}
