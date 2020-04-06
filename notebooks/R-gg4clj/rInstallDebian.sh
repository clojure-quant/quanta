Debian R installation guide 

** Install R **
```
  Add this line to /etc/apt/sources:
  sudo nano /etc/apt/sources.list
  deb http://cran.wustl.edu/linux/debian buster-cran35/

  sudo apt-get update
  sudo apt-get install r-recommended
```

**Inside R: install ggplot2**
because it will install dependencies globally it needs to be run as sudo
sudo R
```
 install.packages("ggplot2")
 install.packages("svglite")
 q()
 ```

print(as.data.frame(installed.packages()[,c(1,3:4)]),row.names=FALSE)


** Check if Rscript works **
Rscript is needed by gorilla r-wrapper

R zoo: Z's ordered observations for irregular time series (reimplemented by incanter)


