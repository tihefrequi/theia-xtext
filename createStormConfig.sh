#!/bin/bash          
echo create storm config directory
mkdir -p ~/.storm/config
echo create storm default config
cp ./example-workspace/src/config/project.storm ~/.storm/config/Default.storm
sed -i 's/Project/Default/g' ~/.storm/config/Default.storm