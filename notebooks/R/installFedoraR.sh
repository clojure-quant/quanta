#!/bin/bash

# Fedora does not have a package from Rserve, so we need to build it from source.
# see: https://support.rstudio.com/hc/en-us/articles/218004217-Building-R-from-source

# install build tools
sudo yum install yum-utils

# see: https://stackoverflow.com/questions/17473547/error-with-readline-yes-default-and-headers-libs-are-not-available
sudo dnf install readline-devel

# see: https://unix.stackexchange.com/questions/215728/with-x-yes-default-and-x11-headers-libs-are-not-available
sudo dnf install xorg-x11-server-devel libX11-devel libXt-devel

# see: https://stackoverflow.com/questions/38690232/installing-r-on-linux-configure-error-libcurl-7-28-0-library-and-headers-a
sudo dnf install libcurl-devel

# to fix the error when installing R-serv package:
# /usr/bin/ld: cannot find -lssl
# collect2: error: ld returned 1 exit status
sudo yum install openssl-devel

# dependency for svg-plot package.
sudo dnf install cairo-devel


# download source code
mkdir /tmp/r
cd /tmp/r
# available versions: https://cran.rstudio.com/src/base/R-3/
wget https://cran.rstudio.com/src/base/R-3/R-3.6.3.tar.gz
tar -xf R-3.6.3.tar.gz
cd R-3.6.3
ls

# build it
./configure --prefix=/opt/R/3.6.3 --enable-R-shlib
#./configure --prefix=/opt/R/3.6.3 --enable-R-shlib --with-blas --with-lapac

make
sudo make install

# this seems not to be required
# sudo ln -s /opt/R/3.6.3/bin/R /bin/R 

# install.packages("Rserve",,"http://rforge.net")
echo "see: https://rforge.net/Rserve/doc.html#intro "
echo "Please start R and enter:"
echo "install.packages(\"Rserve\",,\"http://rforge.net\") "


