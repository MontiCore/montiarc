package de.monticore.lang.montiarc.tagging._symboltable;

import java.util.Collection;

/**
 * Created by Michael von Wenckstern on 30.05.2016.
 *
 * @author Michael von Wenckstern
 */
public interface IsTaggable {

  /**
   * if component a/Controller.arc is loaded, then only tags in the
   * files a/*.tag are loaded
   * but this allows you to save different tagging information for the
   * same component in different files, e.g. a/PowerConsumption.tag
   * and a/Time.tag
   *
   * @return all tags of the components
   */
  Collection<TagSymbol> getTags();

  /**
   * returns only the tag of a special tag kind (e.g. if you want only
   * to evaluate PowerConsumption of a component, than call
   * getTags(PowerConsumption.TAGKIND)
   * --> methodology is the same as in PN's resolve function
   */
  <T extends TagSymbol> Collection<T> getTags(final TagKind tagKind);

  /**
   * adds a tag to the symbol
   *
   * @param tag the tag symbol which should be added
   */
  void addTag(final TagSymbol tag);

  /**
   * adds all tags to the symbol
   *
   * @param tags
   */
  void addTags(final TagSymbol... tags);

  /**
   * add all tags to the symbol
   *
   * @param tags
   */
  void addTags(Iterable<? extends TagSymbol> tags);
}
