#!/bin/sh

rm .cpcache -r
rm node_modules -r
rm target -r
rm .webly -r
rm .shadow-cljs -r
rm package.json
rm package-lock.json
rm karma.conf.js
rm shadow-cljs.edn


rm profiles/demo/.cpcache -r
rm profiles/demo/node_modules -r
rm profiles/demo/target -r
rm profiles/demo/.webly -r
rm profiles/demo/.shadow-cljs -r
rm profiles/demo/package.json
rm profiles/demo/package-lock.json
rm profiles/demo/karma.conf.js
rm profiles/demo/shadow-cljs.edn

rm profiles/demo/classes -r