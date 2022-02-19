/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTInitialOutputDeclaration;
import arcbehaviorbasis.BehaviorError;

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
        BehaviorError.REDUNDANT_INITIAL_DECLARATION.logAt(decl, decl.getName());
      } else {
        duplicates.add(decl.getName());
      }
    }
  }
}