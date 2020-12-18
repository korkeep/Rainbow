#include "pkt.h"

void usage() {
    printf("Syntax: packet <interface> <mac address>\n");
    printf("Sample: packet monitor aa:bb:cc:dd:ee:ff\n");
}

int main(int argc, char* argv[]){

    if (argc != 3) {
        usage();
        return -1;
    }

    char* dev = argv[1];

    char node_mac[17];

    char* my_device_mac = argv[2];

    BuzzerInit();
    
    getMacAddress (node_mac, dev);
 
    packet_capture (dev, node_mac, my_device_mac);
}
