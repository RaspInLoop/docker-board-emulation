
#########################################################################################################################################
#
# Usage : make mvn_setting=~/private/raspinloop/settings.xml qemu_exec=/home/frma/private/raspinloop/qemu/build/qemu-system-aarch64     #
# 	- With qemu_exec pointing to the latest build from https://github.com/RaspInLoop/qemu-raspinloop
# 
# Known Problem:
#
#  - In Ubuntu you need to change access on /boot/vmlinuz*:  chmod 755 /boot/vmlinuz*
#  - VirtualBox cannot be running
#
##########################################################################################################################################
SHELL:=/bin/bash
#Dockerfile vars
board_emumlation_version=latest
mvn_setting=~/.m2/settings.xml
qemu_exec=""  # see comment above 
raspios_name=2020-12-02-raspios-buster-armhf-lite

#vars
RASP=raspberry_img
QEMU=qemu
JAVA_CORE = emulator-hardware-core
JAVA_HARDWARE=raspberry/emulator-hardware
JAVA_APP=emulator-simulator-proxy
JAR=${JAVA_APP}.jar
BUILD_DIR=build

ROOT_DIR=$(abspath $(shell pwd)/..)
IMAGENAME=raspberry-board-emulation
REPO=raspinloop
IMAGEFULLNAME=${REPO}/${IMAGENAME}:${board_emumlation_version}

TITLE=echo -e "\e[0;34m"
TITLE_END=echo -en "\e[00m"

.PHONY: help 

help:
	    @echo "Makefile arguments:"
	    @echo ""
	    @echo "alpver - Alpine Version"
	    @echo ""
	    @echo "mvn_setting - Sepcific Maven settings file (e.g. ~/private/raspinloop/settings.xml)"
	    @echo ""
	    @echo "Makefile commands:"
	    @echo "build"
	    @echo "push"
	    @echo "all"

.DEFAULT_GOAL := build

$(JAR):
	@$(TITLE) "--- Building java application ---";$(TITLE_END)
	cd ${ROOT_DIR}/${JAVA_CORE};mvn --settings ${mvn_setting} -DskipTests clean install
	cd ${ROOT_DIR}/${JAVA_HARDWARE};mvn --settings ${mvn_setting} -DskipTests clean install
	cd ${ROOT_DIR}/${JAVA_APP};./mvnw --settings ${mvn_setting} -DskipTests clean package

dependencies: $(JAR)
	mkdir -p ${BUILD_DIR}/${JAVA_APP}/dependency
	rm -rf  ${BUILD_DIR}/${JAVA_APP}/dependency/*
	cd ${BUILD_DIR}/${JAVA_APP}/dependency;jar -xf  ${ROOT_DIR}/${JAVA_APP}/target/*.jar

qemu:
	@$(TITLE) "--- Copying QEMU executable ---";$(TITLE_END)
	mkdir -p ${BUILD_DIR}/${QEMU}
	cp ${qemu_exec} ${BUILD_DIR}/${QEMU}


${BUILD_DIR}/${RASP}/${raspios_name}.img: 
	@$(TITLE) "--- Downloading raspberry pi OS image---";$(TITLE_END)
	mkdir -p ${BUILD_DIR}/${RASP}
	# curl https://downloads.raspberrypi.org/raspios_lite_armhf/images/raspios_lite_armhf-2020-12-04/${raspios_name}.zip --output $@
	# We need a modified image that support ssh connection on startup and have latest libsimultime installed
	cp img/${raspios_name}.zip ${BUILD_DIR}/${RASP}/${raspios_name}.zip
	unzip -o ${BUILD_DIR}/${RASP}/${raspios_name}.zip -d ${BUILD_DIR}/${RASP}
	chmod 666 $@
	qemu-img resize $@ 2G

rasp_img_splitted: ${BUILD_DIR}/${RASP}/${raspios_name}.img
	@$(TITLE) "--- Splitting raspberry pi OS image---";$(TITLE_END)
	mkdir -p ${BUILD_DIR}/${RASP}/bootpart_raspios
	echo "copy-out / ${BUILD_DIR}/${RASP}/bootpart_raspios/" > commands
	echo "quit" >> commands
	guestfish --ro -a ${BUILD_DIR}/${RASP}/${raspios_name}.img -m /dev/sda1 -f commands
	rm commands
	dtc -O dtb -o ${BUILD_DIR}/${RASP}/bootpart_raspios/bcm2710-rpi-3-b.dtb dts/bcm2710-rpi-3-b.dts

build: qemu rasp_img_splitted dependencies
	@$(TITLE) "--- Building docker image---";$(TITLE_END)
	docker build --pull --build-arg JAVA_DEPENDENCIES=${BUILD_DIR}/${JAVA_APP}/dependency --build-arg RASP_IMG=${BUILD_DIR}/${RASP}/ -t ${IMAGEFULLNAME} .

push:
	@$(TITLE) "--- Pushing docker image---";$(TITLE_END)
	docker push ${IMAGEFULLNAME}

all: build push