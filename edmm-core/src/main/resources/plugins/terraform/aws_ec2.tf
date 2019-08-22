resource "aws_instance" "${aws_ec2_name}" {
  ami = "${aws_ec2_ami}"
  instance_type = "${aws_ec2_instance_type}"
  key_name = aws_key_pair.auth.id
  vpc_security_group_ids = [aws_security_group.${aws_security_group_name}.id]
  subnet_id = aws_subnet.default.id
${provisioner}
}
