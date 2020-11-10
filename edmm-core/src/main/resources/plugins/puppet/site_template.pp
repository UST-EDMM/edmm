<#list nodes as node, components>
node '${node}' {
    <#list components as component>
    include ${component}
    </#list>
}
</#list>
