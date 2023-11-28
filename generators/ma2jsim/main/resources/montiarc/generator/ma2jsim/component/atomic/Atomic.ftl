<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign automaton = helper.getAutomatonBehavior(ast)/>
<#assign hasAutomaton = automaton.isPresent()/>
<#assign isEvent = hasAutomaton && helper.isEventBased(automaton.get())/>

<#list ast.getSymbol().getAllIncomingPorts() as portSym>
    ${tc.includeArgs("montiarc.generator.ma2jsim.component.atomic.HandleMessageOn.ftl", [portSym, hasAutomaton, isEvent])}
</#list>

<#if hasAutomaton>
    ${tc.include("montiarc.generator.ma2jsim.behavior.automata.AutomatonReferencesInComponent.ftl")}
<#else>
    ${tc.include("montiarc.generator.ma2jsim.behavior.NoBehaviorReferencesInComponent.ftl")}
</#if>