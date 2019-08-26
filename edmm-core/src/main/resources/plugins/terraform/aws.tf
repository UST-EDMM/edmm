
provider "aws" {
  version = "~> 2.0"
  region = var.region
}

variable "region" {
  default = "eu-west-1"
}

variable "key_name" {
  default = "id_rsa"
}

variable "public_key_path" {
  default = "id_rsa.pub"
}

variable "ssh_user" {
  default = "ubuntu"
}

resource "aws_key_pair" "auth" {
  key_name = var.key_name
  public_key = file(var.public_key_path)
}

<#if instances?size != 0>
resource "aws_vpc" "default" {
  cidr_block = "10.0.0.0/16"
}

resource "aws_subnet" "default" {
  vpc_id = aws_vpc.default.id
  cidr_block = "10.0.1.0/24"
  map_public_ip_on_launch = true
}

resource "aws_internet_gateway" "default" {
  vpc_id = aws_vpc.default.id
}

<#list instances as k, ec2>
resource "aws_security_group" "${ec2.name}_security_group" {
  name = "${ec2.name}_security_group"
  vpc_id = aws_vpc.default.id
  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  <#list ec2.ingressPorts as port>
  ingress {
    from_port = ${port}
    to_port = ${port}
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  </#list>
  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "${ec2.name}" {
  ami = "${ec2.ami}"
  instance_type = "${ec2.instanceType}"
  key_name = aws_key_pair.auth.id
  vpc_security_group_ids = [aws_security_group.${ec2.name}_security_group.id]
  subnet_id = aws_subnet.default.id
  <#list ec2.provisioners as provisioner>
  <#if provisioner.operations?size != 0>
  provisioner "remote-exec" {
    connection {
      type  = "ssh"
      user  = var.ssh_user
      agent = true
    }
    scripts = [
      <#list provisioner.operations as operation>
      "${operation}",
      </#list>
    ]
  }
  <#else>
  </#if>
  </#list>
}
</#list>
<#else>
</#if>
