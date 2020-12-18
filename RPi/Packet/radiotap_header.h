#include <stdint.h>

#pragma once

#define IEEE_LEN 24

//https://www.semfionetworks.com/uploads/2/9/8/3/29831147/wireshark_802.11_filters_-_reference_sheet.pdf
#define PROBE_REQUEST 0x40
#define QoS_NULL 0xc

//radiotap_header is change depending on the adapter device

//Adapter model name : AWUS036NH(ALFA), PAU09(PANDA)
//length : 18
struct radiotap_header {
        uint8_t        it_version;          /* set to 0 */
        uint8_t        it_pad;
        uint16_t       it_length;           /* entire length */
        uint32_t       it_present_flags;    /* fields present */
        uint8_t        it_flags;
        uint8_t        it_data_Rate;
        uint16_t       it_channel_frequency;
        uint16_t       it_channel_flags;
        uint8_t        it_antenna_signal;
        uint8_t        it_antenna;
        uint16_t       it_RX_flags;
};

//Adapter model : iptime
//length : 24
struct radiotap_header_IPTIME {
        uint8_t        it_version;          /* set to 0 */
        uint8_t        it_pad;
        uint16_t       it_length;           /* entire length */
        uint64_t       it_present_flags;    /* fields present */
        uint8_t        it_flags;
        uint8_t        it_data_Rate;
        uint16_t       it_channel_frequency;
        uint16_t       it_channel_flags;
        uint8_t        it_antenna_signal;
        uint8_t        it_empty;            /* empty */
        uint16_t       it_RX_flags;
        uint8_t        it_antenna_signal2;
        uint8_t        it_antenna;
};

struct ieee80211_header {
        uint8_t        type_subtype;
        uint8_t        flags;
        uint16_t       duration;
        /*
         Beacon Frame, Probe Request, Probe Response, Authentication, Deauthentication, Action
            add1 = Receiver, Destination
            add2 = Transmitter, Source
            add3 = BSSID
         Data
            add1 = Rec, Des, STA
            add2 = Trans, BSSID
            add3 = Source
         Qos Null function, Qos Data, Null function
            add1 = Receiver, BSSID
            add2 = Transmitter, Source, STA
            add3 = Destination
          */
        uint8_t add1[6];
        uint8_t add2[6];
        uint8_t add3[6];
        uint16_t       fragment_sequence;
};
