#!/bin/sh
chmod 600 ~/.ssh/id_rsa
git clone git@github.com:hideki-a/travis-android-test.git -b master
cp -af repository travis-android-test
cd travis-android-test
git merge origin/develop
git add .
git commit -m "Release by Travis CI Job $TRAVIS_JOB_NUMBER"
git push origin master > /dev/null 2>&1
