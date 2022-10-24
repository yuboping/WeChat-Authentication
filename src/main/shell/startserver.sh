#!/bin/sh

JAVA_HOME=/lcims/jdk1.6.0_45
export JAVA_HOME

LANG=zh_CN.gb18030
export LANG

target=wxserver
export target

TOMCAT_HOME=/lcims/support70/tomcat
export TOMCAT_HOME

WORK_HOME=/lcims/support70/wxserver
export WORK_HOME

LOG_HOME=/lcims/support70/output/wxserver
export LOG_HOME
JAVA_OPTS="-Dtarget=${target} -Dwxserver_config=${WORK_HOME}/config/ct/sh/systemconfig.xml -Dlogpath=${LOG_HOME}/log"
JAVA_OPTS="${JAVA_OPTS} -Xms512m -Xmx1024m"
export JAVA_OPTS

CATALINA_OUT="${LOG_HOME}/tomcat/catalina.out"
export CATALINA_OUT

SERVER_CONFIG=${WORK_HOME}/shell/server.xml
export SERVER_CONFIG

cd ${TOMCAT_HOME}
sh ./bin/startup.sh -config ${SERVER_CONFIG}