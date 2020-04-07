
# download source code
# available versions: https://rforge.net/Rserve/files/
mkdir /tmp/rserv
cd /tmp/rserv
wget https://rforge.net/Rserve/snapshot/Rserve_1.8-6.tar.gz
tar -xf Rserve_1.8-6.tar.gz
cd Rserve
ls

# execute R To install it:
export R_HOME=/usr/lib64/R
R CMD INSTALL Rserve_1.8-6.tar.gz
# test running it.
/usr/lib64/R/bin/Rserve

# make symlink
sudo ln -s /usr/lib64/R/bin/Rserve /bin/Rserv

