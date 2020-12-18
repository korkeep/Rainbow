TEMPLATE = app
CONFIG += console c++11
CONFIG -= app_bundle
CONFIG -= qt

LIBS += -lpcap -lcurl

SOURCES += main.cpp \
    getmac.cpp \
    parser.cpp

HEADERS += \
    radiotap_header.h \
    pkt.h

