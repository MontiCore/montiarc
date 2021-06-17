/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTInitialOutputDeclaration;
import arcbehaviorbasis.BehaviorError;
import de.monticore.scbasis._ast.ASTSCState;

import java.util.HashMap;
import java.util.Map;

public class NoRedundantInitialOutput implements ArcAutomatonASTArcStatechartCoCo {

  @Override
  public void check(ASTArcStatechart statechart) {
    Map<ASTSCState, ASTInitialOutputDeclaration> duplicates = new HashMap<>();
    statechart.streamInitialOutput().forEach(declaration -> {
      if(!declaration.isPresentNameDefinition()){
        BehaviorError.INITIAL_STATE_REFERENCE_MISSING.logAt(declaration, declaration.getName());
        return;
      }
      if(duplicates.containsKey(declaration.getNameDefinition())){
        BehaviorError.REDUNDANT_INITIAL_DECLARATION.logAt(declaration, declaration.getName());
      } else {
        duplicates.put(declaration.getNameDefinition(), declaration);
      }
    });
  }
}