#!/bin/bash


# because it will install dependencies globally it needs to be run as sudo

R -e 'install.packages("ggplot2" , repos="http://cran.us.r-project.org")'
R -e 'install.packages("svglite" , repos="http://cran.us.r-project.org")'
# q()