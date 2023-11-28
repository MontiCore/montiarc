<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
protected ${ast.getName()}${suffixes.modeAutomaton()}<@Util.printTypeParameters ast false/> modeAutomaton = new ${ast.getName()}${suffixes.modeAutomaton()}<@Util.printTypeParameters ast false/>(this);

protected ${ast.getName()}${suffixes.modeAutomaton()}<@Util.printTypeParameters ast false/> <@MethodNames.getModeAutomaton/>() { return this.modeAutomaton; }

${tc.include("montiarc.generator.ma2jsim.dynamics.componentReferences.dynamicSubComps.DynamicSubComps.ftl")}

${tc.include("montiarc.generator.ma2jsim.dynamics.componentReferences.HandleModeAutomaton.ftl")}
