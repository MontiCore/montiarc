<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>

interface ${ast.getName()}${suffixes.modes()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/> {
  <#if helper.getModeAutomaton(ast).isPresent()>
    <#assign modeAutomaton = helper.getModeAutomaton(ast).get()>
    <#list helper.getModes(modeAutomaton) as mode>
      /* ${mode.getSymbol().getName()} */
      void <@MethodNames.modeSetup mode.getSymbol()/>();
      void <@MethodNames.modeInit mode.getSymbol()/>();
      void <@MethodNames.modeTeardown mode.getSymbol()/>();
    </#list>
  </#if>
}