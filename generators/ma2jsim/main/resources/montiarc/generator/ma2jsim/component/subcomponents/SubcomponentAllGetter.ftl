<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign modeAutomatonOpt = helper.getComponentHelper().getModeAutomaton(ast)>

@Override
public java.util.List${"<"}montiarc.rte.component.SimComponent${">"} getAllSubcomponents() {
  final java.util.ArrayList${"<"}montiarc.rte.component.SimComponent${">"} allSubcomponentList = new java.util.ArrayList<>();

  <#list ast.getSymbol().getSubcomponents() as subcomponent>
    allSubcomponentList.add(<@subCompAccessor subcomponent/>);
  </#list>

  <#if modeAutomatonOpt.isPresent()>
  switch (this.modeAutomaton.currentMode) {
    <#list helper.getModeHelper().getModes(modeAutomatonOpt.get()) as mode>
      case ${mode.getName()}:
        <#list helper.getModeHelper().getInstancesFromMode(mode) as modeSub>
          allSubcomponentList.add(<@modeSubCompAccessor modeSub.getSymbol() mode/>);
        </#list>
        break;
    </#list>
  }
  </#if>

  return allSubcomponentList;
}

<#macro subCompAccessor subSymbol>
  this.${prefixes.subcomp()}${subSymbol.getName()}()
</#macro>

<#macro modeSubCompAccessor subSymbol mode>
  this.${prefixes.subcomp()}${mode.getName()}_${subSymbol.getName()}()
</#macro>
