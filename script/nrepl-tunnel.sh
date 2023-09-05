#!/bin/sh

echo "creating nrepl tunnel (nrepl port: 19100)" 
ssh -N florian@quant.hoertlehner.com -L 19100:localhost:9100

