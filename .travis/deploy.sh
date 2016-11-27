#!/bin/sh
chmod 600 ~/.ssh/id_rsa
git clone git@github.com:hideki-a/android-app-api-sdk-dev.git -b master
cp -af repository android-app-api-sdk-dev
cp -af docs android-app-api-sdk-dev
cp -af ApiSdkDev/mt-data-api-sdk-android/build/reports/tests/release android-app-api-sdk-dev/ApiSdkDev/mt-data-api-sdk-android/build/reports/tests
cd android-app-api-sdk-dev
git merge origin/develop --no-edit
git add .
git commit -m "Release by Travis CI Job $TRAVIS_JOB_NUMBER"
git push origin master > /dev/null 2>&1
