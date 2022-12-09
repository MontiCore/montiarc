<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Implicit Variable Definitions for easier Developing in Intellij -->
<#-- @ftlvariable name="storage" type="montiarc.arc2fd.fd.FDConstructionStorage" -->
<#-- @ftlvariable name="config" type="montiarc.arc2fd.fd.FDConfiguration" -->
<#-- @ftlvariable name="comp" type="arcbasis._ast.ASTComponentType " -->
<#compress>
<#-- We compress the following content in order to prevent unnecessary whitespaces -->
<#-- Assign passed values (by function) to variable for easier use -->
    ${signature("storage", "fdConfig")}
    <#assign storage=storage>
    <#assign config=fdConfig>

<#-- Extract the ComponentType (to get the name) -->
    <#assign comp = ast.getComponentType()>

<#-- Get the Component Name -->
    <#assign fd_name=comp.getName()>

<#-- From the Store Extract and store all Variables we're interested in -->
    <#assign optionals=storage.getStringOptionals()>
    <#assign simple_or=storage.getStringSimpleOr()>
    <#assign xor=storage.getStringXor()>
    <#assign remaining_conjunctions=storage.getStringRemainingConjunctions()>
    <#assign requires=storage.getStringRequires()>
    <#assign excludes=storage.getStringExcludes()>
</#compress>

<#-- Head of the Feature Diagram -->
featurediagram ${fd_name} {
<#-- Processing Optionals -->
<@iterate_and_print optionals config.getOptionalsEnum() fd_name/>

<#-- Processing Simple OR -->
<@iterate_and_print simple_or config.getSimpleOrEnum() fd_name/>

<#-- Processing XOR -->
<@iterate_and_print xor config.getXOREnum() fd_name/>

<#-- Processing AND (Remaining Conjunctions) -->
<@iterate_and_print remaining_conjunctions config.getRemainingConjunctionsEnum() fd_name/>

<#-- Processing Requires -->
<@iterate_and_print requires config.getRequiresEnum()/>

<#-- Processing Excludes -->
<@iterate_and_print excludes config.getExcludesEnum()/>
}

<#-- Makro for Iterating over the List and printing each item correctly -->
<#macro iterate_and_print data_list type name="">
<#-- Only Print something if we also have ocntent -->
    <#if data_list?has_content>
    <#-- Call Display Type -->
        <@display_type type/>
        <#list data_list as data>
            ${data};
        </#list>
    </#if>
</#macro>

<#-- Makro for Displaying the Type of Operand -->
<#macro display_type type>
    <#if config.shouldDisplayTypes()>
        /* ${config.mapTypeToDisplayedName(type)} */
    </#if>
</#macro>
