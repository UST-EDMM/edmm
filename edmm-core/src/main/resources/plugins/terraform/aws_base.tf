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