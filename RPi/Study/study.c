#include <stdio.h>
#include <stdlib.h>
#include <wiringPi.h>
#include <softTone.h>
#include <time.h>
#include "parson.h"
// 각 FND와 연결된 라즈베리파이 핀(S0, S1, …, S5)
const int FndSelectPin[6] = {4, 17, 18, 27, 22, 23};
// FND의 LED와 연결된 라즈베리파이 핀(A, B, …, H)
const int FndPin[8] = {6, 12, 13, 16, 19, 20, 26, 21};
// FND에 출력되는 문자 (0~9) 배열
const int FndFont[10] = {0x3F, 0x06, 0x5B, 0x4F, 0x66, 0x6D, 0x7D, 0x07, 0x7F, 0x67};
// 초음파 센서의 Trigger, Echo 핀
const int TP = 2; //Output
const int EP = 3; //Input
// Buzzer의 음역, 핀
const int melody[8] = {131,147,165,175,196,220,247,262};
const int BP = 5;
// 앉아있는지 여부 체크하는 Flag
int is_sit = 0;

// 초기화 함수, WiringPi 라이브러리 초기화, Select 핀 및 LED 핀 초기화를 담당)
void Init() {
    int i;
    if (wiringPiSetupGpio() == -1) {
        printf("wiringPiSetupGpio() error \n");
        exit(-1);
    }
    for (i = 0; i < 6; i++) {
        pinMode(FndSelectPin[i], OUTPUT);    // Select 핀을 출력으로 설정
        digitalWrite(FndSelectPin[i], HIGH); // Select 핀 OFF
    }
    for (i = 0; i < 8; i++) {
        pinMode(FndPin[i], OUTPUT);   // LED 핀을 출력으로 설정
        digitalWrite(FndPin[i], LOW); // LED 핀을 OFF
    }
    pinMode(TP, OUTPUT);
    pinMode(EP, INPUT);
    softToneCreate(BP);
}

// 초음파 센서를 이용해 Distance를 받아오는 함수
double getDistance() {
    double fDistance = 0.0;
    int nStartTime, nEndTime;
    nStartTime = nEndTime = 0;

    digitalWrite(TP, LOW);

    delayMicroseconds(10);
    digitalWrite(TP, HIGH);

    delayMicroseconds(10);
    digitalWrite(TP, LOW);

    while (digitalRead(EP) == LOW);
    nStartTime = micros();

    while (digitalRead(EP) == HIGH);
    nEndTime = micros();

    fDistance = (nEndTime - nStartTime) / 29. / 2.;
    
    return fDistance;
}

// FND를 선택하는 함수, S0 ~ S5 중 파라미터(position)에 해당하는 FND 선택
void FndSelect(int position) {
    int i;
    for (i = 0; i < 6; i++) {
        if (i == position) {
            digitalWrite(FndSelectPin[i], LOW); // 선택된 FND의 Select 핀 ON
        }
        else {
            digitalWrite(FndSelectPin[i], HIGH); // 선택되지 않은 FND의 Select 핀 OFF
        }
    }
}

// FND를 출력하는 함수
void FndDisplay(int position, int num) {
    int i, j;
    int flag = 0;     // FndPin[ ]을 ON/OFF
    int shift = 0x01; // FndFont와 And 연산하여 출력할 LED의 상태 결정
    for (i = 0; i < 8; i++) {
        flag = (FndFont[num] & shift); // i = 0, FndFont[ 0 ] = 0x3F라 하면 (0b00111111 & 0b00000100 = 1) 이다.
        digitalWrite(FndPin[i], flag); // FndPin[ ]을 flag( 0또는 1 )로 ON/OFF
        shift <<= 1;                   // 왼쪽으로 한 비트 쉬프트한다. I = 0이라 하면, ( shift = 0b00000001 )에서 ( shift = 0b00000010)로 변한다.
    }

    FndSelect(position);
}

int main()
{
    double f1, f2, f3, f4, f5;
    int i, pos, now, s_data;
    int data[6] = {0, 0, 0, 0, 0, 0}; // 출력할 문자 데이터
    s_data = 0;

    JSON_Value *rootValue;
    JSON_Object *rootObject;

    rootValue = json_value_init_object();             // JSON_Value 생성 및 초기화
    rootObject = json_value_get_object(rootValue);    // JSON_Value에서 JSON_Object를 얻음

    // 오늘 날짜 구하기
    time_t tnow;
    struct tm* t;
    time(&tnow);
    t = (struct tm*) localtime(&tnow);

    char today[10];
    char tomorrow[10];
    sprintf(today, "%04d-%02d-%02d", t->tm_year + 1900, t->tm_mon + 1, t->tm_mday);
    sprintf(tomorrow, "%04d-%02d-%02d", t->tm_year + 1900, t->tm_mon + 1, t->tm_mday + 1);

    Init();

    while (1) {
        f1 = getDistance();
        delay(1);
        f2 = getDistance();
        delay(1);
        f3 = getDistance();
        delay(1); 
        f4 = getDistance();
        delay(1);
        f5 = getDistance();
        
        // 초음파 센서 감지, 버튼 딩동댕
        if(is_sit == 0){
            if (f1 < 30.0 || f2 < 30.0 || f3 < 30.0 || f4 < 30.0 || f5 < 30.0) {
                is_sit = 1;
                softToneWrite(BP, melody[0]);
                delay(250);
                softToneWrite(BP, melody[2]);
                delay(250);
                softToneWrite(BP, melody[4]);
                delay(250);
                softToneWrite(BP, 0);
                printf("\nDistance: %8.4f, %8.4f, %8.4f, %8.4f, %8.4f", f1, f2, f3, f4, f5);
            }
        }

        // 초음파 센서 감지, 버튼 댕동딩
        else if (is_sit == 1){
            if (f1 > 30.0 && f2 > 30.0 && f3 > 30.0 & f4 > 30.0 && f5 > 30.0) {
                is_sit = 0;
                softToneWrite(BP, melody[4]);
                delay(250);
                softToneWrite(BP, melody[2]);
                delay(250);
                softToneWrite(BP, melody[0]);
                delay(250);
                softToneWrite(BP, 0);

                // 객체에 키를 추가하고 공부날짜 저장
                json_object_set_string(rootObject, "Study_date", today);
                // 객체에 키를 추가하고 공부시간 저장
                json_object_set_number(rootObject, "Study_time", s_data);

                //Send Data to Cloud Service
                printf("\nDistance: %8.4f, %8.4f, %8.4f, %8.4f, %8.4f", f1, f2, f3, f4, f5);
                printf("\nSTUDY_DATE: %s", today);
                printf("\nSTUDY_TIME: %d\n\n", s_data);

                // JSON_Value를 사람이 읽기 쉬운 문자열(pretty)로 만든 뒤 파일에 저장
                json_serialize_to_file_pretty(rootValue, "today.json");

                // 객체에 키를 추가하고 공부날짜 저장
                json_object_set_string(rootObject, "Study_date", tomorrow);
                // 객체에 키를 추가하고 공부시간 저장
                json_object_set_number(rootObject, "Study_time", 0);
                json_object_set_number(rootObject, "Study_goal", 0);
                json_object_set_number(rootObject, "Study_achieved", 0);

                // JSON_Value를 사람이 읽기 쉬운 문자열(pretty)로 만든 뒤 파일에 저장
                json_serialize_to_file_pretty(rootValue, "tomorrow.json");
            }
        }

        // FND
        if (is_sit == 1) {
            for (i = 0; i < 5; i++) {
                if(i==1||i==3) {
                    if (data[i] == 6) {
                        data[i + 1]++;
                        data[i] = 0;
                    }
                }
                else if (data[i] == 10) {
                    data[i + 1]++;
                    data[i] = 0;
                }
            }
            now = millis();
            while (millis() - now < 1000) {
                for (pos = 0; pos < 6; pos++) {
                    FndDisplay(pos, data[pos]);
                    delay(1);
                }
            }
            data[0]++;
            s_data++;
        }
    }
    return 0;
}
