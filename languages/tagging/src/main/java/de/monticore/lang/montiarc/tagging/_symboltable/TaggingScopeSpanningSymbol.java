package de.monticore.lang.montiarc.tagging._symboltable;

import java.util.Collection;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.Scopes;
import de.monticore.symboltable.SymbolKind;

/**
 * Created by Michael von Wenckstern on 03.06.2016.
 *
 * @author Michael von Wenckstern
 */
public class TaggingScopeSpanningSymbol extends CommonScopeSpanningSymbol
    implements IsTaggable {
  /**
   * @see CommonSymbol#CommonSymbol(String, SymbolKind)
   */
  public TaggingScopeSpanningSymbol(String name, SymbolKind kind) {
    super(name, kind);
  }

  /**
   * if component a/Controller.arc is loaded, then only tags in the
   * files a/*.tag are loaded
   * but this allows you to save different tagging information for the
   * same component in different files, e.g. a/PowerConsumption.tag
   * and a/Time.tag
   *
   * @return all tags of the components
   */
  @Override
  public Collection<TagSymbol> getTags() {
    return getMutableSpannedScope().<TagSymbol>resolveLocally(TagSymbol.KIND);
  }

  /**
   * returns only the tag of a special tag kind (e.g. if you want only
   * to evaluate PowerConsumption of a component, than call
   * getTags(PowerConsumption.TAGKIND)
   * --> methodology is the same as in PN's resolve function
   */
  @Override
  public <T extends TagSymbol> Collection<T> getTags(final TagKind tagKind) {
    return getMutableSpannedScope().<T>resolveLocally(tagKind);
  }

  /**
   * adds a tag to the symbol
   *
   * @param tag the tag symbol which should be added
   */
  @Override
  public void addTag(final TagSymbol tag) {    
    if (!Scopes.getAllEncapsulatedSymbols(getMutableSpannedScope()).contains(tag)) {
      getMutableSpannedScope().add(tag);
    }
  }

  /**
   * adds all tags to the symbol
   *
   * @param tags
   */
  @Override
  public void addTags(final TagSymbol... tags) {
    for (final TagSymbol tag : tags) {
      addTag(tag);
    }
  }

  /**
   * add all tags to the symbol
   *
   * @param tags
   */
  @Override
  public void addTags(Iterable<? extends TagSymbol> tags) {
    for (TagSymbol tag : tags) {
      addTag(tag);
    }
  }
}
