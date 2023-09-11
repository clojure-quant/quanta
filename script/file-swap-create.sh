#!/bin/sh

# BTRFS problem with CoW
# CoW is optional, but you can turn it of *only* on new (empty) 
# files that have never been written to.
# Roughly, create empty file, chattr +C, then write the right 
# amount of zeroes to it, then mkswap.

# A file with the 'C' attribute set will not be subject to
# copy-on-write updates.  This flag is only supported on
# file systems which perform copy-on-write.  (Note: For
# btrfs, the 'C' flag should be set on new or empty files.
sudo touch /mnt/swapfile
sudo chattr +C /mnt/swapfile

# This is 10 GiB of swap space.  
# sudo dd if=/dev/zero of=/mnt/swapfile bs=1MiB count=5120
# awb99: swapfile must be bigger than current ram, because
# otherwise hibernation will not work.
sudo dd if=/dev/zero of=/mnt/swapfile bs=1MiB count=5120

# For security, make the file readable and writable only by root.
sudo chmod 600 /mnt/swapfile
sudo mkswap /mnt/swapfile
sudo swapon /mnt/swapfile

echo "run glances to see if swap is working ..."
