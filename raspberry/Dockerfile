FROM ubuntu:20.04
ARG QEMU=build/qemu/qemu-system-aarch64
ARG JAVA_DEPENDENCIES=build/emulator-simulator-proxy/dependency
ARG RASP_IMG=build/raspberry_img
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y openjdk-11-jre-headless libglib2.0-dev libfdt-dev libpixman-1-dev zlib1g-dev libzmq3-dev qemu-system-data
RUN addgroup --system spring && adduser --system spring --ingroup spring
RUN mkdir /raspberry && chmod 777 /raspberry
USER spring:spring
COPY ${QEMU} /qemu/
COPY ${JAVA_DEPENDENCIES}/BOOT-INF/lib /app/lib
COPY ${JAVA_DEPENDENCIES}/META-INF /app/META-INF
COPY ${RASP_IMG}/bootpart_raspios /raspberry/bootpart_raspios
COPY ${JAVA_DEPENDENCIES}/BOOT-INF/classes /app
ENTRYPOINT ["java", "-cp", "app:app/lib/*","org.raspinloop.emulator.proxyserver.RiaProxyServerApplication"]