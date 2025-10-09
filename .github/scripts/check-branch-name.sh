#!/usr/bin/env bash

branch_name=${1:-$(git rev-parse --abbrev-ref HEAD)}

pattern='^(main|(feature|task|fix|bug)/[a-zA-Z0-9-]+)$'

if [[ "$branch_name"  =~ $pattern ]]; then
    echo -e "\e[1;32m Branch name '$branch_name' is valid. \e[0m"
    exit 0
else
  echo -e "\e[1;31m Branch name '$branch_name' violates the naming rules and is thereby invalid. \e[0m"
  echo -e "Allowed pattern: '\e[1;36m$pattern\e[0m'"
  exit 1
fi