#include "pkt.h"
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include <unistd.h>

using namespace std;

// ref:
// https://www.includehelp.com/cpp-programs/get-mac-address-of-linux-based-network-device.aspx
void getMacAddress(char * uc_Mac, char* dev)
{
    int fd;

    struct ifreq ifr;
    char *iface = dev;
    u_char *mac;

    fd = socket(AF_INET, SOCK_DGRAM, 0);

    ifr.ifr_addr.sa_family = AF_INET;
    strncpy((char *)ifr.ifr_name , (const char *)iface , IFNAMSIZ-1);

    ioctl(fd, SIOCGIFHWADDR, &ifr);

    close(fd);

    mac = (u_char *)ifr.ifr_hwaddr.sa_data;

    //display mac address
    sprintf((char *)uc_Mac,(const char *)"%.2x:%.2x:%.2x:%.2x:%.2x:%.2x" , mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);

}
