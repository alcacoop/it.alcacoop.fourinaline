#!/bin/sh

for f in `find . -name *.java`; do
  cat HEADER_LICENSE $f > /tmp/oldfile
  mv /tmp/oldfile $f
  echo "License Header copied to $f"
done  
