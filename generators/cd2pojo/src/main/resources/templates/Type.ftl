<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("package", "kind", "type", "super", "helper", "existsHWC")}
<#import "/templates/Methods.ftl" as Methods>
<#import "/templates/Attributes.ftl" as Attributes>
/* (c) https://github.com/MontiCore/monticore */
package ${package};

import java.util.*;

public ${kind} ${type.getName()}<#if existsHWC>TOP</#if> ${super} {

<#if type.isIsEnum()>
    <#list type.getFieldList() as field>${field.getName()}<#sep>, </#sep></#list>;
<#elseif type.isIsClass()>
    <@Attributes.printAttributesAndGetterSetter type=type helper=helper/>
    <@Methods.printMockMethods type=type helper=helper/>
<#elseif type.isIsInterface()>
    <@Attributes.printAttributesForInterface type=type helper=helper/>
    <@Methods.printInterfaceMethods type=type helper=helper/>
</#if>
}