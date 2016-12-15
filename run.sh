#!/bin/bash

export PHANTOM_JS_PATH=/phantomjs-2.1.1-linux-x86_64/bin/phantomjs
#export PHANTOM_JS_PATH=/usr/local/Cellar/phantomjs/2.1.1/bin/phantomjs
# maven
export M3_HOME=/apache-maven-3.3.9
export M3=$M3_HOME/bin
export PATH=$M3:$PATH

mvn package
mvn exec:java -e
