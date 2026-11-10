#!/bin/sh
# Ganti username placeholder dengan username Anda. Pemakaian: ./rename.sh username_anda
# Setelah itu ganti nama folder proyek ini menjadi {username}-pbo2026-springboot-cashflow.
set -e
NEW="$1"
if [ -z "$NEW" ]; then echo "Pemakaian: ./rename.sh <username>"; exit 1; fi
OLD="username"
find . -type f \( -name "*.java" -o -name "pom.xml" -o -name "*.yaml" -o -name "*.md" \) \
  -exec sed -i "s/${OLD}-pbo2026/${NEW}-pbo2026/g; s/${OLD}pbo2026/${NEW}pbo2026/g" {} +
for base in src/main/java src/test/java; do
  if [ -d "$base/org/delcom/${OLD}pbo2026springbootcashflow" ]; then
    mv "$base/org/delcom/${OLD}pbo2026springbootcashflow" "$base/org/delcom/${NEW}pbo2026springbootcashflow"
  fi
done
echo "Selesai. Ganti nama folder proyek menjadi ${NEW}-pbo2026-springboot-cashflow"
