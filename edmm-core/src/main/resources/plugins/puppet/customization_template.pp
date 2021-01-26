class { '${component}':
    <#list properties as key, value>
    <#if value[0] == "$" || value?is_boolean>
    ${key} => ${value},
    <#else>
    ${key} => '${value}',
    </#if>
    </#list>
}
