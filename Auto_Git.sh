#!/bin/sh

git add *

date_time=$(date)

git commit -m "auto git push  AndroidApp_UESTC_BBS_MVP_JAVA: ${date_time}"
#git commit -m "把缺失的APP_ID字符串补上了: ${date_time}"

git push origin master

