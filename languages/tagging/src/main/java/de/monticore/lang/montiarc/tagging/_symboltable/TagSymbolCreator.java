package de.monticore.lang.montiarc.tagging._symboltable;

import de.monticore.lang.montiarc.tagging._ast.ASTTaggingUnit;
import de.monticore.symboltable.Scope;

/**
 * Created by Michael von Wenckstern on 31.05.2016.
 *
 * @author Michael von Wenckstern
 */
public interface TagSymbolCreator {
  void create(ASTTaggingUnit unit, Scope gs);
}
