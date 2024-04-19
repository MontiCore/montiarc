<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->

<#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
<#assign modes = helper.getModes(modeAutomaton)>

protected enum Mode {
<#list modes as mode>
  ${mode.getName()}("${mode.getName()}")<#sep>, </#sep>
</#list>;

  public final String name;
  Mode(String name) { this.name = name; }
}