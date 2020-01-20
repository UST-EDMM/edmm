<#if auth0Instances??>
<#if auth0Instances?size != 0>
provider "auth0" {
  domain = var.domain
  client_id = var.client_id
  client_secret = var.client_secret
}

<#list auth0Instances as k, auth0>
variable "domain" {
  default = "${auth0.domain}"
}

variable "client_id" {
  default = "${auth0.clientId}"
}

variable "client_secret" {
  default = "${auth0.clientSecret}"
}

resource "auth0_resource_server" "${auth0.name}_resource_server" {
  name        = "${auth0.name}_resource_server"
  identifier  = "${auth0.identifier}"
  signing_alg = "RS256"
  <#list auth0.scopes as scope>
  scopes {
    value = "${scope}"
  }
  </#list>
  allow_offline_access = true
  token_lifetime = 8600
  skip_consent_for_verifiable_first_party_clients = true
}

</#list>
</#if>
</#if>
