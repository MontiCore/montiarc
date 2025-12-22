<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

<#assign modeAutomaton = helper.getComponentHelper().getModeAutomaton(ast).get()>
<#assign modes = helper.getModeHelper().getModes(modeAutomaton)>

@de.se_rwth.commons.Generated("montiarc.generators.ma2jsim")
protected enum Mode {
<#list modes as mode>
  ${mode.getName()}("${mode.getName()}")<#sep>, </#sep>
</#list>;

  public final String name;
  Mode(String name) { this.name = name; }
}
