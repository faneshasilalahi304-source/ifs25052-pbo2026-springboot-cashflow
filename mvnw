#!/bin/sh
# Maven wrapper sederhana: mengunduh Apache Maven otomatis pada pemakaian pertama.
set -e
BASE="$(cd "$(dirname "$0")" && pwd)"
VER="3.9.11"
DIR="$BASE/.mvn/wrapper"
MVN_HOME="$DIR/apache-maven-$VER"
if [ ! -x "$MVN_HOME/bin/mvn" ]; then
  echo "Mengunduh Apache Maven $VER ..."
  mkdir -p "$DIR"
  URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$VER/apache-maven-$VER-bin.zip"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$URL" -o "$DIR/maven.zip"
  else
    wget -q "$URL" -O "$DIR/maven.zip"
  fi
  unzip -q -o "$DIR/maven.zip" -d "$DIR"
  rm -f "$DIR/maven.zip"
  chmod +x "$MVN_HOME/bin/mvn"
fi
exec "$MVN_HOME/bin/mvn" "$@"
