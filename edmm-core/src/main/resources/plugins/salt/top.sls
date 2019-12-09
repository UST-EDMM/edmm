base:
  <#list minions as minion>
'${minion.name}':
  - ${minion.file}
  </#list>