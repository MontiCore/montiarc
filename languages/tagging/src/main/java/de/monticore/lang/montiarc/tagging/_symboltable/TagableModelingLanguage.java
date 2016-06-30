package de.monticore.lang.montiarc.tagging._symboltable;

import de.monticore.ModelingLanguage;

/**
 * Created by Michael von Wenckstern on 09.06.2016.
 */
public interface TagableModelingLanguage extends ModelingLanguage {
  public static final String TAG_FILE_ENDING = "tag";

  void addTagSymbolCreator(TagSymbolCreator tagSymbolCreator);
}
