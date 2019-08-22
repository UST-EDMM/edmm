  provisioner "remote-exec" {
    connection {
      type  = "ssh"
      user  = var.ssh_user
      agent = true
    }
    scripts = [
${scripts}
    ]
  }