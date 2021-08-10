# Sources
## docker
Run qemu in docker: source : https://github.com/lukechilds/dockerpi

interresting article: https://appfleet.com/blog/raspberry-pi-cluster-emulation-with-docker-compose/
repo: https://github.com/mrhavens/pidoc

https://repositories.lib.utexas.edu/bitstream/handle/2152/46169/PLATT-MASTERSREPORT-2016.pdf

# qemu
## Modified raspberry Pi kernel + qemu versatilpb machine
https://github.com/dhruvvyas90/qemu-rpi-kernel
## Original raspebrry pi kernel + qemu raspi3 machine
https://translatedcode.wordpress.com/2018/04/25/debian-on-qemus-raspberry-pi-3-model/
https://stackoverflow.com/questions/61562014/qemu-kernel-for-raspberry-pi-3-with-networking-and-virtio-support

## Original raspebrry pi kernel + qemu virt machine
https://github.com/wimvanderbauwhede/limited-systems/wiki/Debian-%22buster%22-for-Raspberry-Pi-3-on-QEMU

## build our own qemu machine
https://stackoverflow.com/questions/37028940/how-to-create-a-qemu-arm-machine-with-custom-peripherals-and-memory-maps


## tuto
https://www.youtube.com/watch?v=3yP3QOT-h98

# Raspberry pi 
## Debian images
https://wiki.debian.org/RaspberryPiImages

## device tree
https://blog.stabel.family/raspberry-pi-4-device-tree/


# Raspberry Pi using gpio code sample

https://elinux.org/RPi_GPIO_Code_Samplesq

# Another guy who wants to do the same thing

https://www.embeddedrelated.com/showthread/comp.arch.embedded/272409-1.php and https://github.com/wzab/BR_Internet_Radio/tree/gpio



# Build 
## qemu
build qemu 5.1.0 from source

## Getting the image
Download the Raspberry Pi OS Lite from https://downloads.raspberrypi.org/raspios_lite_armhf/images/raspios_lite_armhf-2020-12-04/2020-12-02-raspios-buster-armhf-lite.zip  
extract the 2020-12-02-raspios-buster-armhf-lite.img from zip in current directory  
`mkdir bootpart_raspios`  
`sudo chmod 0644 /boot/vmlinuz* # Ubuntu changed  aceess on kernel (https://bugs.launchpad.net/fuel/+bug/1467579)`
`guestfish --ro -a 2020-12-02-raspios-buster-armhf-lite.img -m /dev/sda1`  
Then at the guestfish prompt type:
> copy-out / bootpart_raspios/

> quit

## tweak the DeviceTree
disassemble dtb: `dtc -I dtb -O dts bootpart_raspios/bcm2710-rpi-3-b.dtb > bcm2710-rpi-3-b.dts`  
edit bcm2710-rpi-3-b.dts and add at line 11 (before "aliase")  
``` dtc
serial-number = "000000008e8dd26c";
system {
        linux,revision = <0x00a02082>;
};
```
Note: 0x00a02082 meaning can be found [here](https://www.raspberrypi.org/documentation/hardware/raspberrypi/revision-codes/README.md)

recompile:`dtc -O dtb -o bootpart_raspios/bcm2710-rpi-3-b.dtb bcm2710-rpi-3-b.dts` 


# run
`qemu-system-aarch64   -kernel bootpart_raspios/kernel8.img   -dtb bootpart_raspios/bcm2710-rpi-3-b.dtb  -M raspi3 -m 1024  -append "console=ttyAMA0 root=/dev/mmcblk0p2 rw rootwait rootfstype=ext4" -nographic   -sd 2020-12-02-raspios-buster-armhf-lite.img -device usb-net,netdev=net0 -netdev user,id=net0,hostfwd=tcp::5555-:22`

with usb-storage: -qemu-system-aarch64   -kernel bootpart_raspios/kernel8.img   -dtb bootpart_raspios/bcm2710-rpi-3-b2.dtb  -M raspi3 -m 1024  -append "console=ttyAMA0 root=/dev/mmcblk0p2 rw rootwait rootfstype=ext4" -nographic   -sd 2020-12-02-raspios-buster-armhf-lite.img -device usb-net,netdev=net0 -netdev user,id=net0,hostfwd=tcp::5555-:22 -drive if=none,id=stick,file=myimage.img -device usb-storage,drive=stick

with usb-serial and fifo: 
`qemu-system-aarch64   -kernel bootpart_raspios/kernel8.img   -dtb bootpart_raspios/bcm2710-rpi-3-b2.dtb  -M raspi3 -m 1024  -append  "root=/dev/mmcblk0p2 rw rootwait rootfstype=ext4"   -sd 2020-12-02-raspios-buster-armhf-lite.img -device usb-net,netdev=net0 -netdev user,id=net0,hostfwd=tcp::5555-:22 -chardev pipe,id=ch0,path=/tmp/serial0  -device usb-serial,chardev=ch0 -D ./log.txt -monitor stdio`

`qemu-system-aarch64   -kernel bootpart_raspios/kernel8.img   -dtb bootpart_raspios/bcm2710-rpi-3-b2.dtb  -M raspi3 -m 1024  -append  "root=/dev/mmcblk0p2 rw rootwait rootfstype=ext4"   -sd 2020-12-02-raspios-buster-armhf-lite.img -device usb-net,netdev=net0 -netdev user,id=net0,hostfwd=tcp::5555-:22 -chardev pipe,id=ch0,path=/tmp/serial0  -device usb-serial,chardev=ch0 `

with usb-serial and socket
`qemu-system-aarch64   -kernel bootpart_raspios/kernel8.img   -dtb bootpart_raspios/bcm2710-rpi-3-b2.dtb  -M raspi3 -m 1024  -append  "root=/dev/mmcblk0p2 rw rootwait rootfstype=ext4"   -sd 2020-12-02-raspios-buster-armhf-lite.img -device usb-net,netdev=net0 -netdev user,id=net0,hostfwd=tcp::5555-:22 -chardev socket,id=sock0,host=0.0.0.0,port=5554,server,nowait,telnet -device usb-serial,chardev=sock0 `