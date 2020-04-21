#!/bin/bash

curl -sSL https://repo.continuum.io/miniconda/Miniconda3-latest-Linux-x86_64.sh -o /tmp/miniconda.sh \
&& bash /tmp/miniconda.sh -bfp /usr/local \
&& rm -rf /tmp/miniconda.sh \
&& conda install -y python=3 \
&& conda update conda \
&& conda clean --all --yes
