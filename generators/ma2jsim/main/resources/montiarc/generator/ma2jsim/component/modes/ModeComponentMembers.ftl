<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#assign modeAutomatonClass>${ast.getName()}${suffixes.modeAutomaton()}</#assign>

protected ${modeAutomatonClass} modeAutomaton;
protected boolean isModeAutomatonSync;

${modeAutomatonClass} getModeAutomaton() {
  return this.modeAutomaton;
}