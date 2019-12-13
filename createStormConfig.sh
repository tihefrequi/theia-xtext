#!/bin/bash          
echo create storm config directory
mkdir -p ~/.storm/config
echo create storm default config
cp ./xtext-dsl-extension/build/stormlib/platform/config/Global.storm ~/.storm/config/Default.storm
sed -i 's/Global/Default/g' ~/.storm/config/Default.storm