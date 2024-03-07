<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("state", "subStateList")}
<#if subStateList?has_content>
    public static montiarc.rte.automaton.State ${prefixes.state()}${state.getName()} = new montiarc.rte.automaton.State("${state.getName()}",
    Arrays.asList(
    <#list subStateList as subState>
        state_${subState.getName()} <#sep >, </#sep>
    </#list>
    ),
    Arrays.asList(
    <#list subStateList as subState>
        <#if subState.getSCModifier().isInitial()>
            state_${subState.getName()} <#sep >, </#sep>
        </#if>
    </#list>
    )
    );
    <#else>
        public static montiarc.rte.automaton.State ${prefixes.state()}${state.getName()} = new montiarc.rte.automaton.State("${state.getName()}");
</#if>

