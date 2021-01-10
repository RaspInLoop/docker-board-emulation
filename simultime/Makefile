CC ?= gcc
INSTALL ?= install

PREFIX ?= /usr/local
LIBDIRNAME ?= /lib/simultime
PLATFORM ?=$(shell uname)

CFLAGS += -g -std=gnu99 -Wall -Wextra -Werror -Wno-nonnull-compare -fPIC -DPREFIX='"'$(PREFIX)'"' -DLIBDIRNAME='"'$(LIBDIRNAME)'"' $(SIMULTIME_COMPILE_CFLAGS)

LIB_LDFLAGS += -shared

LDFLAGS += -lpthread -pthread

LDADD += -ldl -lm -lrt
BIN_LDFLAGS += -lrt

SRC = libsimultime.c
LIBS_OBJ = libsimultime.o

SONAME = 1
LIBS = libsimultime.so.${SONAME}
#TEST_SRC = test/clock_nanosleep.c test/clock_nanosleepcopy.c
TEST_SRCS := $(wildcard test/*.c)
TEST_EXEC := $(basename ${TEST_SRCS})

all: ${LIBS}


${LIBS_OBJ}: src/libsimultime.c
	${CC} -o $@ -c ${CFLAGS} ${CPPFLAGS} ${EXTRA_FLAGS} $<

libsimultime.so.${SONAME}: libsimultime.o src/libsimultime.map
	${CC} -o $@ -Wl,-soname,$@ ${LDFLAGS} ${LIB_LDFLAGS} $< ${LDADD}

clean:
	@rm -f ${LIBS_OBJ} ${LIBS} $(TEST_EXEC)


distclean: clean
	@echo

install: ${LIBS}
	@echo
	@echo "Copying the simultime libraries to ${DESTDIR}${PREFIX}${LIBDIRNAME}"
	$(INSTALL) -dm0755 "${DESTDIR}${PREFIX}${LIBDIRNAME}/"
	$(INSTALL) -m0644 ${LIBS} "${DESTDIR}${PREFIX}${LIBDIRNAME}/"

uninstall:
	for f in ${LIBS}; do rm -f "${DESTDIR}${PREFIX}${LIBDIRNAME}/$$f"; done
	rmdir "${DESTDIR}${PREFIX}${LIBDIRNAME}"

$(TEST_EXEC): %: %.c
	$(CC) $(CFLAGS) -o $@ $<

test: ${LIBS} $(TEST_EXEC)
	avocado run test/*Test.py

.PHONY: all clean distclean install uninstall test