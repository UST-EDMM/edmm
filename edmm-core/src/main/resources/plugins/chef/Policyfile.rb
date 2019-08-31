# Policyfile.rb - Describe how you want Chef to build your system.
#
# For more information on the Policyfile feature, visit
# https://docs.chef.io/policyfile.html

# A name that describes what the system you're building with Chef does.
name <#if policyfile.name??>'${policyfile.name}'<#else>'generated'</#if>

# Where to find external cookbooks [current default = public Chef Supermarket]:
default_source :supermarket
default_source :chef_repo, '../cookbooks'

# run_list: chef-client will run these recipes in the order specified.
run_list ${policyfile.runningOrder}

<#if policyfile.cookbooks??>
# Specify a custom source for a single cookbook:
<#list policyfile.cookbooks as cookbook>
cookbook '${cookbook.name}', path: '${cookbook.path}'
</#list>
</#if>