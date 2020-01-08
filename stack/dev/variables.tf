variable "project_name" {
  type = string
}

variable "aws_profile" {
  type = string
  default = "default"
}

variable "aws_region" {
  type = string
  default = "us-east-1"
}

variable "aws_account_id" {
  type = string
}

variable "github_location" {
  type = string
}

// DockerHub parameters
variable "docker_username" {
  type = string
}

variable "docker_password" {
  type = string
}

variable "docker_registry" {
  type = string
}

variable "docker_image" {
  type = string
}