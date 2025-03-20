<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

<#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
<#assign modes = helper.getModes(modeAutomaton)>

protected enum Mode {
<#list modes as mode>
  ${mode.getName()}("${mode.getName()}")<#sep>, </#sep>
</#list>;

  public final String name;
  Mode(String name) { this.name = name; }
}