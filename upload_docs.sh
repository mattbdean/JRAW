#!/bin/bash

set -e
shopt -s extglob

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

# Make sure we're on master before we try to invoke any Gradle scripts
git checkout master

## Where the docs will be found after building them in master
BUILD_DOC="build/docs/javadoc/"
## Name of the 'javadoc' folder
DOC_FOLDER=$(basename $BUILD_DOC)
## First seven characters of the latest commit SHA
COMMIT_SHA=$(git rev-parse --short HEAD --verify)
## Last release (v0.5.0, etc.)
LATEST_TAG=$(git describe --abbrev=0 --tags)
## Generic commit message
COMMIT_MSG="Update Javadoc to commit $COMMIT_SHA"
## Where the docs will be placed in the gh-pages branch
OUT_DIR="docs/git/$COMMIT_SHA"
## Location of the latest git commit docs
OUT_DIR_LATEST="docs/git/latest"

rm -rf "$BUILD_DOC" # Remove all old javadoc
# Build the javadoc and give it a more descriptive title
./gradlew javadoc -Djavadoc-version="commit $COMMIT_SHA ($LATEST_TAG+)"
cp -r "$BUILD_DOC" -r .. # Move the javadoc out of git's reach

git checkout gh-pages
mkdir -p "$OUT_DIR"
mv ../$DOC_FOLDER/* "$OUT_DIR" # Move the javadoc to its corresponding folder
cp -r "$OUT_DIR" "$OUT_DIR_LATEST"
rm -r ../$DOC_FOLDER/

git commit -am "$COMMIT_MSG"
git push
git checkout master

