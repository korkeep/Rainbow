#pragma once

#include <iostream>
#include <stdint.h>
#include <string.h>
#include <wiringPi.h>
#include <stdio.h>

using namespace std;

void getMacAddress (char* node_mac, char* dev);

int packet_capture(char* dev, char* node_mac, char* my_device_mac);

void parsing(const u_char* packet, char* node_mac, char* my_device_mac);

void BuzzerInit();

int BuzzerControl(int flag);