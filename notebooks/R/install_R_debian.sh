
sudo nano /etc/apt/sources.list
# Add this line to /etc/apt/sources:
# deb http://cran.wustl.edu/linux/debian buster-cran35/

sudo apt-get update
sudo apt-get install r-recommended

# Inside R ggplot2 needs to be installed
# because it will install dependencies globally it needs to be run as sudo
# install.packages("ggplot2")
# install.packages("svglite")
# q()
sudo R

# check if Rscript works - this is needed by gorilla r-wrapper
Rscript


