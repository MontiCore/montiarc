<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/component/modes/ModeUtil.ftl" as ModeUtil>

<#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
<#assign modes = helper.getModes(modeAutomaton)>
<#list modes as mode>
  @Override
  public void <@MethodNames.modeInit mode.getSymbol()/>() {
    <#list helper.getInstancesFromMode(mode) as sub>
      <#assign subSymbol = sub.getSymbol()>
      <#assign subCompName>this.${prefixes.subcomp()}${mode.getName()}_${subSymbol.getName()}${helper.subcomponentVariantSuffix(ast, subSymbol)}</#assign>
      ${subCompName}.init();
    </#list>
  }
</#list>
