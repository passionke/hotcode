#!/bin/bash

set -m
CASE_BASE_DIR="src/test/cases"
TARGET_BASE_DIR="target/cases"
PROJ_DIR=`pwd`
HOTCODE_AGENT_PATH="${PROJ_DIR}/target/hotcode.jar"
DEBUG_OPT="-server -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=9999,server=y,suspend=y"

MINGW32=`(uname | grep "MINGW32" | wc -l)`

if [ $MINGW32 -eq 1 ] ; then
    WIN_AGENT_PATH=`(echo ${HOTCODE_AGENT_PATH} | sed 's/\/\([a-zA-Z]\)\//\1:\//')`
    HOTCODE_AGENT_PATH=${WIN_AGENT_PATH}
fi

FAILED="false"
mkdir -p ${TARGET_BASE_DIR}
cd ${CASE_BASE_DIR}

if [ $# == 0 ]; then
    CASES=`find . -type d`
else
    CASES=$@
fi

for CASE in $CASES; do
    test "${CASE}" == "." && continue

    echo $'\e[32m'"Start running case: ${CASE}"$'\e[00m'

    CASE_SOURCE_DIR="${PROJ_DIR}/${CASE_BASE_DIR}/${CASE}"
    CASE_TARGET_DIR="${PROJ_DIR}/${TARGET_BASE_DIR}/${CASE}"

    if [ -a "${CASE_TARGET_DIR}" ]; then
        rm -rf ${CASE_TARGET_DIR}
    fi

    mkdir -p ${CASE_TARGET_DIR}

    ### Copy first version classes to target path and compile them.
    cd ${CASE_SOURCE_DIR}

    for NAME in `ls ?.java`; do
        cp $NAME ${CASE_TARGET_DIR}/$NAME
    done

    cp "${PROJ_DIR}/${CASE_BASE_DIR}/Base.java" "${CASE_TARGET_DIR}/Base.java"
    cd ${CASE_TARGET_DIR}
    javac *.java

    BASE_FILE_LAST_MODIFIED=`stat -c %Y Base.class`

    ### Run with HotCode
    java -javaagent:${HOTCODE_AGENT_PATH} -noverify Base ${CASE} &>result &
    JOB_ID=`jobs -r | awk '{print $1}' | grep -o '[0-9]\+'`

    ### Copy second version classes to target path and compile them.
    cd ${CASE_SOURCE_DIR}

    for NAME in `ls ??.java`; do
        TARGET_NAME=`echo $NAME | sed 's/.\./\./g'`
        cp $NAME ${CASE_TARGET_DIR}/${TARGET_NAME}
    done

    ### Waiting until Java Agent is been installed.
    cd ${CASE_TARGET_DIR}
    while [ 1 -eq 1 ] ; do
        BASE_FILE_CURRENT=`stat -c %Y Base.class`
        if [ "$BASE_FILE_CURRENT" = "$BASE_FILE_LAST_MODIFIED" ]; then
            break
        fi
    done

    cd ${CASE_TARGET_DIR}
    javac ?.java

    ### Trigger java process continue to run.
    touch Base.class

    ### Bring java process to fore ground.
    fg "%${JOB_ID}" &>/dev/null

    ### Check the result.
    RESULT=`cat result`
    IS_SUCCESS=`grep Success < result`
    
    if [ -z "${IS_SUCCESS}" ]; then
        echo $'\e[31m'"${RESULT}"$'\e[00m'
        FAILED="true"
    else
        echo $'\e[32m'"${RESULT}"$'\e[00m'
    fi
done

test "${FAILED}" == "true" && exit 1

echo $'\e[32m'"All test cases are pass!"$'\e[00m'
