#!/bin/bash

set -e
shopt -s extglob

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

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

export TERM=dumb

# Travis uses the git:// URL. When pushing, GitHub will return an error.
# Use the HTTPS version instead
# See http://stackoverflow.com/q/7548661/1275092
git remote set-url origin https://github.com/thatJavaNerd/JRAW

# Fetch the other branches since Travis only clones gh-pages
git fetch origin gh-pages:gh-pages

rm -rf "$BUILD_DOC" # Remove all old javadoc
# Build the javadoc and give it a more descriptive title
./gradlew javadoc -Djavadoc-version="commit $COMMIT_SHA ($LATEST_TAG+)"
cp -r "$BUILD_DOC" -r .. # Move the javadoc out of git's reach

git checkout gh-pages
if [ -d "$OUT_DIR" ]; then
    echo "Docs already uploaded. Exiting"
    exit
fi

mkdir -p "$OUT_DIR"
mv ../$DOC_FOLDER/* "$OUT_DIR" # Move the javadoc to its corresponding folder
cp -r "$OUT_DIR" "$OUT_DIR_LATEST"
rm -r ../$DOC_FOLDER/

# Configure git
git config user.name "$GIT_NAME"
git config user.email "$GIT_EMAIL"
git config credential.helper "store --file=.git/credentials"

echo "https://$GIT_USER:$GIT_PASS@github.com" > .git/credentials

git add docs/
git commit -m "$COMMIT_MSG"
git push --set-upstream origin gh-pages
git checkout master

