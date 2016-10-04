package de.monticore.lang.montiarc.tagging._symboltable;

import java.util.Collection;

import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.resolving.ResolvingFilter;

/**
 * Created by Michael von Wenckstern on 03.06.2016.
 *
 * @author Michael von Wenckstern
 */
public class TaggingSymbol extends CommonSymbol implements IsTaggable {
  protected MutableScope scope = null;

  /**
   * @see CommonSymbol#CommonSymbol(String, SymbolKind)
   */
  public TaggingSymbol(String name, SymbolKind kind) {
    super(name, kind);
  }

  // do it lazy b/c most symbols will not have tags
  protected MutableScope getScope() {
    if (scope == null) {
      scope = new CommonScope();
      for (ResolvingFilter rf : this.getEnclosingScope().getResolvingFilters()) {
        scope.addResolver(rf);
      }
      ((MutableScope) this.getEnclosingScope()).addSubScope(scope);
    }
    return scope;
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
  public Collection<TagSymbol> getTags() {
    return getScope().<TagSymbol>resolveLocally(TagSymbol.KIND);
  }

  /**
   * returns only the tag of a special tag kind (e.g. if you want only
   * to evaluate PowerConsumption of a component, than call
   * getTags(PowerConsumption.TAGKIND)
   * --> methodology is the same as in PN's resolve function
   */
  public <T extends TagSymbol> Collection<T> getTags(final TagKind tagKind) {
    return getScope().<T>resolveLocally(tagKind);
  }

  /**
   * adds a tag to the symbol
   *
   * @param tag the tag symbol which should be added
   */
  public void addTag(final TagSymbol tag) {
    if (!getScope().getSymbols().containsKey(tag.getName())) {
      getScope().add(tag);
    }
  }

  /**
   * adds all tags to the symbol
   *
   * @param tags
   */
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
  public void addTags(Iterable<? extends TagSymbol> tags) {
    for (TagSymbol tag : tags) {
      addTag(tag);
    }
  }
}
