#!/bin/sh
# Maven wrapper sederhana: mengunduh Apache Maven otomatis pada pemakaian pertama.
# Tidak wajib punya "unzip": fallback ke "jar" (ada di JDK) atau python3.
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
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$URL" -O "$DIR/maven.zip"
  else
    echo "ERROR: butuh curl atau wget untuk mengunduh Maven" >&2
    exit 1
  fi

  if command -v unzip >/dev/null 2>&1; then
    unzip -q -o "$DIR/maven.zip" -d "$DIR"
  elif command -v jar >/dev/null 2>&1; then
    (cd "$DIR" && jar xf maven.zip)
  elif command -v python3 >/dev/null 2>&1; then
    python3 -c "import zipfile,sys; zipfile.ZipFile(sys.argv[1]).extractall(sys.argv[2])" "$DIR/maven.zip" "$DIR"
  else
    echo "ERROR: butuh unzip, jar, atau python3 untuk mengekstrak Maven" >&2
    exit 1
  fi

  rm -f "$DIR/maven.zip"
  chmod +x "$MVN_HOME/bin/mvn"
fi
exec "$MVN_HOME/bin/mvn" "$@"
