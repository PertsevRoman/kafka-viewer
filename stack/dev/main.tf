provider "aws" {
  profile = var.aws_profile
  region = var.aws_region
  allowed_account_ids = [
    var.aws_account_id
  ]

  alias = "aws_develop_env"
}

resource "aws_iam_role" "codebuild_role" {
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "codebuild.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

  provider = "aws.aws_develop_env"
}

resource "aws_iam_role_policy" "codebuild_policy" {
  role = aws_iam_role.codebuild_role.id
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Resource": [
                "arn:aws:logs:${var.aws_region}:${var.aws_account_id}:log-group:/aws/codebuild/${var.project_name}-build",
                "arn:aws:logs:${var.aws_region}:${var.aws_account_id}:log-group:/aws/codebuild/${var.project_name}-build:*"
            ],
            "Action": [
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents"
            ]
        },
        {
            "Effect": "Allow",
            "Resource": [
                "arn:aws:s3:::codepipeline-${var.aws_region}-*"
            ],
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:GetObjectVersion",
                "s3:GetBucketAcl",
                "s3:GetBucketLocation"
            ]
        },
        {
            "Effect": "Allow",
            "Resource": [
                "${aws_ssm_parameter.docker_username.arn}",
                "${aws_ssm_parameter.docker_password.arn}",
                "${aws_ssm_parameter.docker_registry.arn}",
                "${aws_ssm_parameter.docker_image.arn}"
            ],
            "Action": [
                "ssm:GetParameters"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "codebuild:CreateReportGroup",
                "codebuild:CreateReport",
                "codebuild:UpdateReport",
                "codebuild:BatchPutTestCases"
            ],
            "Resource": [
                "arn:aws:codebuild:${var.aws_region}:${var.aws_account_id}:report-group/${var.project_name}-*"
            ]
        }
    ]
}
EOF

  provider = "aws.aws_develop_env"
}

resource "aws_codebuild_project" "code-build" {
  name = "${var.project_name}-build"
  service_role = aws_iam_role.codebuild_role.arn
  badge_enabled = true

  artifacts {
    type = "NO_ARTIFACTS"
  }

  environment {
    compute_type                = "BUILD_GENERAL1_SMALL"
    image                       = "aws/codebuild/standard:3.0"
    type                        = "LINUX_CONTAINER"
    image_pull_credentials_type = "CODEBUILD"
    privileged_mode             = true
  }

  source {
    type = "GITHUB"
    location = var.github_location
    git_clone_depth = 1
  }

  provider = "aws.aws_develop_env"
}

resource "aws_codebuild_webhook" "all_push_webhook" {
  project_name = "${var.project_name}-build"

  filter_group {
    filter {
      type = "EVENT"
      pattern = "PUSH"
    }

    filter {
      type = "HEAD_REF"
      pattern = "^refs/heads/develop$"
    }
  }

  provider = "aws.aws_develop_env"
}