provider "aws" {
  profile = var.aws_profile
  region = var.aws_region
  allowed_account_ids = [
    var.aws_account_id
  ]
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
}

resource "aws_iam_role_policy" "codebuild_policy" {
  role = aws_iam_role.codebuild_role.id
  policy = <<POLICY
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
POLICY
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
    image                       = "aws/codebuild/standard:2.0"
    type                        = "LINUX_CONTAINER"
    image_pull_credentials_type = "CODEBUILD"
  }

  source {
    type = "GITHUB"
    location = var.github_location
    git_clone_depth = 1
  }
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
}