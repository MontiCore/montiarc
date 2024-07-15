/* (c) https://github.com/MontiCore/monticore */
package scmapping._cocos;

import static montiarc.conformance.util.AutomataUtils.getOutPorts;

import arcbasis._ast.ASTComponentType;
import montiarc.conformance.util.AutomataUtils;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.ArrayList;
import java.util.List;
import scmapping.SCMappingMill;
import scmapping._ast.ASTMappingRule;
import scmapping._ast.ASTSCMapping;
import scmapping._visitor.SCMappingTraverser;
import scmapping._visitor.ValidName;
import scmapping.util.MappingUtil;

public class ValidNamesInRulesCoCo implements SCMappingASTSCMappingCoCo {

  private final List<String> validConNamesInStateRules = new ArrayList<>();
  private final List<String> validRefNamesInStateRules = new ArrayList<>();
  private final List<String> validConNamesInInputRules = new ArrayList<>();
  private final List<String> validRefNamesInInputRules = new ArrayList<>();
  private final List<String> validConNamesInOutputRules = new ArrayList<>();
  private final List<String> validRefNamesInOutputRules = new ArrayList<>();

  public ValidNamesInRulesCoCo(
      ASTComponentType refAut,
      ASTCDCompilationUnit refCD,
      ASTComponentType conAut,
      ASTCDCompilationUnit conCD) {

    // concrete names for state rules
    AutomataUtils.getStateList(conAut).forEach(p -> validConNamesInStateRules.add(p.getName()));
    validConNamesInStateRules.add("state");
    validConNamesInStateRules.add(conAut.getName() + ".state");
    validConNamesInStateRules.add(conAut.getName());
    validConNamesInStateRules.addAll(MappingUtil.getGlobalVarNames(conAut));

    // reference names for state rules
    AutomataUtils.getStateList(refAut).forEach(p -> validRefNamesInStateRules.add(p.getName()));
    validRefNamesInStateRules.add("state");
    validRefNamesInStateRules.add(refAut.getName() + ".state");
    validRefNamesInStateRules.add(refAut.getName());
    validRefNamesInStateRules.addAll(MappingUtil.getGlobalVarNames(refAut));

    // reference names for input rules
    AutomataUtils.getInPorts(refAut).forEach(p -> validRefNamesInInputRules.add(p.getName()));
    List<String> refInputTypes = MappingUtil.getInputTypes(refAut, refCD);
    validRefNamesInInputRules.addAll(refInputTypes);
    validRefNamesInInputRules.addAll(MappingUtil.getGlobalVarNames(refAut));
    refInputTypes.forEach(type -> validRefNamesInInputRules.addAll(MappingUtil.getEnumConstants(refCD, type)));

    // concrete names for input rules
    AutomataUtils.getInPorts(conAut).forEach(p -> validConNamesInInputRules.add(p.getName()));
    List<String> conInputTypes = MappingUtil.getInputTypes(refAut, conCD);
    validConNamesInInputRules.addAll(conInputTypes);
    validConNamesInInputRules.addAll(MappingUtil.getGlobalVarNames(conAut));
    conInputTypes.forEach(type -> validConNamesInInputRules.addAll(MappingUtil.getEnumConstants(conCD, type)));

    // reference names for output rules
    getOutPorts(refAut).forEach(p -> validRefNamesInOutputRules.add(p.getName()));
    List<String> refOutTypes = MappingUtil.getOutputTypes(refAut, refCD);
    validRefNamesInOutputRules.addAll(refOutTypes);
    validRefNamesInOutputRules.addAll(MappingUtil.getGlobalVarNames(refAut));
    refOutTypes.forEach(type -> validRefNamesInOutputRules.addAll(MappingUtil.getEnumConstants(refCD, type)));

    // concrete names for output rules
    getOutPorts(conAut).forEach(p -> validConNamesInOutputRules.add(p.getName()));
    List<String> conOutTypes = MappingUtil.getOutputTypes(refAut, refCD);
    validConNamesInOutputRules.addAll(conOutTypes);
    validConNamesInOutputRules.addAll(MappingUtil.getGlobalVarNames(conAut));
    conOutTypes.forEach(type -> validConNamesInOutputRules.addAll(MappingUtil.getEnumConstants(conCD, type)));
  }

  @Override
  public void check(ASTSCMapping node) {
    node.getStateRules()
        .forEach(rule -> checkRule(rule, validRefNamesInStateRules, validConNamesInStateRules));
    node.getInputRules()
        .forEach(rule -> checkRule(rule, validRefNamesInInputRules, validConNamesInInputRules));
    node.getOutputRules()
        .forEach(rule -> checkRule(rule, validRefNamesInOutputRules, validConNamesInOutputRules));
  }

  public void checkRule(ASTMappingRule rule, List<String> refNames, List<String> conNames) {
    SCMappingTraverser refTraverser = SCMappingMill.traverser();
    refTraverser.add4CommonExpressions(new ValidName(refNames));
    refTraverser.add4ExpressionsBasis(new ValidName(refNames));
    rule.getReference().accept(refTraverser);

    SCMappingTraverser conTraverser = SCMappingMill.traverser();
    conTraverser.add4CommonExpressions(new ValidName(conNames));
    conTraverser.add4ExpressionsBasis(new ValidName(conNames));
    rule.getConcrete().accept(conTraverser);
  }
}
