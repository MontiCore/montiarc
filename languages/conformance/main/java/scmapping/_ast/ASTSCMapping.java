/* (c) https://github.com/MontiCore/monticore */
package scmapping._ast;

import java.util.ArrayList;
import java.util.List;

public class ASTSCMapping extends ASTSCMappingTOP {

  public List<ASTMappingRule> getInputRules() {
    List<ASTMappingRule> rules = new ArrayList<>();

    for (ASTMappingElement element : getMappingElementList()) {
      if (element instanceof ASTInputRules) {
        rules.addAll(((ASTInputRules) element).getMappingRuleList());
      }
    }

    return rules;
  }

  public List<ASTMappingRule> getOutputRules() {
    List<ASTMappingRule> rules = new ArrayList<>();

    for (ASTMappingElement element : getMappingElementList()) {
      if (element instanceof ASTOutputRules) {
        rules.addAll(((ASTOutputRules) element).getMappingRuleList());
      }
    }

    return rules;
  }

  public List<ASTMappingRule> getStateRules() {
    List<ASTMappingRule> rules = new ArrayList<>();

    for (ASTMappingElement element : getMappingElementList()) {
      if (element instanceof ASTStateRules) {
        rules.addAll(((ASTStateRules) element).getMappingRuleList());
      }
    }

    return rules;
  }
}
