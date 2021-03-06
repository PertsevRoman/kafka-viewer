resource "aws_ssm_parameter" "docker_username" {
  name = "/dockerhub/username"
  description = "DockerHub username"
  type = "SecureString"
  value = var.docker_username
  overwrite = false

  provider = "aws.aws_develop_env"
}

resource "aws_ssm_parameter" "docker_password" {
  name = "/dockerhub/password"
  description = "DockerHub password"
  type = "SecureString"
  value = var.docker_password
  overwrite = false

  provider = "aws.aws_develop_env"
}

resource "aws_ssm_parameter" "docker_registry" {
  name = "/dockerhub/registry"
  description = "DockerHub registry name"
  type = "SecureString"
  value = var.docker_registry
  overwrite = false

  provider = "aws.aws_develop_env"
}

resource "aws_ssm_parameter" "docker_image" {
  name = "/dockerhub/image"
  description = "DockerHub registry image name"
  type = "SecureString"
  value = var.docker_image
  overwrite = false

  provider = "aws.aws_develop_env"
}