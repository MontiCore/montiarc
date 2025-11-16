<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type="arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="automaton" type="arcautomaton._ast.ASTArcStatechart" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("automaton")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@Override
public void init() {
  state.enterWithSub();
  setState(state.getInitialSubstate());
}
