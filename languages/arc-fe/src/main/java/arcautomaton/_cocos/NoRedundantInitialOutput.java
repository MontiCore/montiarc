/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTInitialOutputDeclaration;
import arcbasis.util.ArcError;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that for each state there is at maximum one {@link ASTInitialOutputDeclaration}.
 */
public class NoRedundantInitialOutput implements ArcAutomatonASTArcStatechartCoCo {

  @Override
  public void check(ASTArcStatechart statechart) {
    Set<String> duplicates = new HashSet<>();
    for(ASTInitialOutputDeclaration decl : statechart.streamInitialOutput().collect(Collectors.toList())) {
      if (duplicates.contains(decl.getName())) {
        Log.error(ArcError.REDUNDANT_INITIAL_DECLARATION.format(decl.getName()),
          decl.get_SourcePositionStart(), decl.get_SourcePositionEnd()
        );
      } else {
        duplicates.add(decl.getName());
      }
    }
  }
}