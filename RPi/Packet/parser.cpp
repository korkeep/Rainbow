#include "pkt.h"
#include "radiotap_header.h"
#include <pcap.h>
#include <chrono>
#include <curl/curl.h>
#include <softTone.h>
#include <time.h>

#define LOW 0
#define HIGH 1

int BuzzerFlag = 0;
const int melody[8] = {131,147,165,175,196,220,247,262};
const int BP = 5;

int packet_capture(char* dev, char* node_mac, char* my_device_mac){
    char errbuf[PCAP_ERRBUF_SIZE];
    pcap_t* handle = pcap_open_live(dev, BUFSIZ, 1, 1000, errbuf);
    if (handle == nullptr) {
        fprintf(stderr, "pcap_open_live(%s) return nullptr - %s\n", dev, errbuf);
        return -1;
    }

    while (true) {
        struct pcap_pkthdr* header;
        const u_char* packet;
        int res = pcap_next_ex(handle, &header, &packet);
        if (res == 0) continue;
        if (res == -1 || res == -2) {
            printf("pcap_next_ex return %d(%s)\n", res, pcap_geterr(handle));
            break;
        }

        parsing(packet, node_mac, my_device_mac);

        BuzzerControl(BuzzerFlag);
        BuzzerFlag = 0;
    }

    pcap_close(handle);
    return 0;
}


void parsing(const u_char* packet, char* node_mac, char* my_device_mac){
    //packet header setting
    struct radiotap_header *rh = (struct radiotap_header *)packet;
    struct ieee80211_header *ih = (struct ieee80211_header *)(packet + rh->it_length);
    //uint8_t *wlh = (uint8_t *)ih + IEEE_LEN;               //wireless LAN header

    //my_device_mac change to hex
    uint8_t my_mac[6];
    char t[4];

    for(int i = 0; i <6; i++){
        memcpy(t, (my_device_mac +i*3), 3);
        t[3] = '\0';
        *(my_mac +i) = (uint8_t)strtoul(t, NULL, 16);
    }
    //printf("my_mac :  %02x:%02x:%02x:%02x:%02x:%02x   ",my_mac[0], my_mac[1], my_mac[2], my_mac[3], my_mac[4], my_mac[5]);
    
    //Catch Probe_request & parsing
    if( ih->type_subtype == PROBE_REQUEST && rh->it_antenna_signal > 186){
        static int cnt = 0;
        cnt++;
        printf("%3d Probe Request=============================================\n", cnt);

        char device_mac[17]; // enough size
        //%02X : 2 hex code 
        sprintf(device_mac, "%02x:%02x:%02x:%02x:%02x:%02x", ih->add2[0], ih->add2[1], ih->add2[2], ih->add2[3], ih->add2[4], ih->add2[5]);
        printf("device_mac : %s, ", device_mac);

        if((my_mac[0] == ih->add2[0])&&(my_mac[1] == ih->add2[1])&&(my_mac[2] == ih->add2[2])
            &&(my_mac[3] == ih->add2[3])&&(my_mac[4] == ih->add2[4])&&(my_mac[5] == ih->add2[5])){
            printf("\n=== This is from your phone ===\n");
            BuzzerFlag = 1;
            if(BuzzerFlag == 1){
                printf("BuzzerFlag is 1");
            }

            // 오늘 날짜 구하기
            time_t tnow;
            struct tm* t;
            time(&tnow);
            t = (struct tm*) localtime(&tnow);

            char today[10];
            int p_data;
            sprintf(today, "%04d-%02d-%02d", t->tm_year + 1900, t->tm_mon + 1, t->tm_mday);
            p_data = t->tm_hour * 3600 + t->tm_min * 60 + t->tm_sec;

            printf("\nDETECT_DATE: %s", today);
            printf("\nDETECT_TIME: %d\n\n", p_data);

            //Send Data to Cloud Service
            CURL *curl;
            CURLcode res;

            std::string strTargetURL;
            std::string strResourceJSON;
            std::string s_today(today);

            struct curl_slist *headerlist = nullptr;
            headerlist = curl_slist_append(headerlist, "Content-Type: application/json");

            strTargetURL = "https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/detect";
            strResourceJSON = "{\"Packet_date\": \"" + s_today + "\", " + "\"Packet_time\": \"" + std::to_string(p_data) +"\"}";
            
            curl_global_init(CURL_GLOBAL_ALL);
            curl = curl_easy_init();
           
            if (curl)
            {
                curl_easy_setopt(curl, CURLOPT_URL, strTargetURL.c_str());
                curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerlist);
                curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, false);
                curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, false);
                curl_easy_setopt(curl, CURLOPT_POST, 1L);
                curl_easy_setopt(curl, CURLOPT_POSTFIELDS, strResourceJSON.c_str());
                
                res = curl_easy_perform(curl);
                
                curl_easy_cleanup(curl);
                curl_slist_free_all(headerlist);
            }
        }

        int rssi = 0;
        if (rh->it_antenna_signal<127)
            rssi= rh->it_antenna_signal -1;
        else
            rssi= rh->it_antenna_signal -255 -1;
        printf("rssi : %d, ", rssi);


        const auto p1 = chrono::system_clock::now();
        int timestamp = chrono::duration_cast<chrono::seconds>(p1.time_since_epoch()).count();
        printf("timestamp : %d \n", timestamp);
        printf("\n");
    }
}

void BuzzerInit(){
    if (wiringPiSetupGpio() == -1) {
        printf("wiringPiSetupGpio() error \n");
        exit(-1);
    }

    softToneCreate(BP); 
}

int BuzzerControl(int flag){
    if(flag == 1){
        softToneWrite(BP, melody[6]);
        delay(200);
        softToneWrite(BP, 0);
        delay(50);
        softToneWrite(BP, melody[6]);
        delay(200);
        softToneWrite(BP, 0);
        delay(50);
    }
    return 0;
}