terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "3.56.0"
    }
  }
  backend "s3" {
    bucket = "pgr301-kaam004-terraform"
    key    = "pgr301-kaam004-terraform/pgr301-kaam004-terraform.state"
    region = "eu-west-1"
  }
}