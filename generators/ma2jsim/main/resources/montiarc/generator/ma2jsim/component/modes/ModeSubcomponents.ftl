<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
<#list helper.getModes(modeAutomaton) as mode>
  <#list helper.getInstancesFromMode(mode) as sub>
    <#assign subSym = sub.getSymbol()>
    ${tc.includeArgs("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentField.ftl", [subSym, mode.getName()])}
    ${tc.includeArgs("montiarc.generator.ma2jsim.component.subcomponents.SubcomponentGetter.ftl", [subSym, mode.getName()])}
  </#list>
</#list>
