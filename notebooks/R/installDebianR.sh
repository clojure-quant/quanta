Debian R installation guide 

** Install R **
```
  Add this line to /etc/apt/sources:
  sudo nano /etc/apt/sources.list
  deb http://cran.wustl.edu/linux/debian buster-cran35/

  sudo apt-get update
  sudo apt-get install r-recommended
```


 ```

# print(as.data.frame(installed.packages()[,c(1,3:4)]),row.names=FALSE)


# Check if Rscript works (neded for r-wrappers)
Rscript

# R zoo: Z's ordered observations for irregular time series (reimplemented by incanter)


