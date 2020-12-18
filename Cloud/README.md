# DataBase
디비&서버 입니당! 앞으로 sql문을 업로드 할게용

** 11월 14일 업데이트 밑에 내용 바꿀건 다음에 할게여!***

람다함수 설명 현재 총 8개 아마 9개면 될듯?
이름이 헷갈리니 잘 구분해야합니다

-------- 라즈베리파이 -> rds ---------
1. rainbow-post-Study
post 매소드이므로 https url로 사용못하고 postman을 쓰거나 curl을 사용해야합니다.
전 그냥 api gateway 테스트 썼는데 되더라구요

2. rainbow-post-Detect
위와 동일한데, Detect 테이블에 데이터 넣는함수입니다.

-------- App <-> rds --------
1. rainbow-app-get-achieved
목표를 달성한 날, 못한 날을 불러오는 함수

https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/rainbow-get-achieved
위의 url에 접속하면 람다함수가 가동되어 위의 페이지에 달성한 날이 뜹니다.
https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/rainbow-get-achieved?achieved=1
위와 같이 하면 목표를 달성한 날을 achieved=0을 하면 목표를 달성하지 못한 날을 불러옵니다.

2. rainbow-get-count_interrupt
원하는 날짜의 핸드폰 사용횟수를 보여줍니다.

https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/rainbow-get-count-interrupt
위의 url에 접속하면 람다함수가 가동되어 원하는 데이터를 가져옵니다.
https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/rainbow-get-count-interrupt?target=2020-11-08
위와 같이 ?뒤에 target=[원하는 날짜]를 입력하면 해당 날짜에서 핸드폰 사용횟수를 보여줍니다.

3. rainbow-get-goal
원하는 날짜의 목표 공부량을 보여줍니다.

https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/rainbow-get-goal
위의 url에 접속하면 람다함수가 가동됩니다.
https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/rainbow-get-goal?target=2020-11-08
위와 같이?뒤에 target=[원하는 날짜]를 입력하면 해당 날짜의 목표시간을 보여줍니다.
현재는 rds에 저장된 초단위로 된 수를 가져오는데 원하면 시간, 분, 초 단위로 바꿔서 보여줄 수 있습니다.
추후 상의해서 바꿔볼게요

4. rainbow-post-goal
앱에서 설정한 목표 시간을 rds에 보내주는 함수

https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/rainbow-post-goal
위의 url을 이용하면 람다함수가 가동됩니다.
단, post이므로 위에 있는 get함수들 처럼 url에 파라미터를 넣는다고 되는게 아니고
postman을 쓰거나 curl을 사용해서 body에 값을 넣어야합니다.

5. rainbow-app-get-calendar
rds에 저장된 Study_date들을 가져오는 함수

https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/rainbow-get-calendar
위의 url을 이용하면 람다함수가 가동되며
?로 뒤에 파라미터를 넣을 필요는 없습니다.

6. rainbow-get-studytime
원하는 날짜의 총 공부시간을 보여주는 함수

https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/rainbow-get-studytime
위의 url을 이용하면 람다함수가 가동되며
https://r89kbtj8x9.execute-api.us-east-1.amazonaws.com/dev/rainbow-get-studytime?target=2020-11-14
위처럼 ?뒤에 target=[원하는 날짜]를 넣어주면 그 날짜의 총 공부시간을 보여줍니다.

디비& 서버입니다
중간 부분까지 들어갈 내용은 다음과 같습니다.

기반이 될 데이터베이스 스키마 생성

아래의 ER 다이어 그램을 바탕으로 스키마를 생성했습니다.

<img width="297" alt="20201101_012631" src="https://user-images.githubusercontent.com/60510921/97784391-9ec16c80-1be1-11eb-95bc-9f40f688fff7.png">

스터디 테이블과 패킷 테이블은 서로 약한 엔티티 타입과 강한 엔티티 타입으로 방해한다는 관계를 가집니다.
따라서 약한 엔티티 타입인 패킷 테이블은 스터디 테이블의 날짜 어트리뷰트를 참조하면서 동시에 기본키를 구성합니다.
이 때 디텍트 테이블은 패킷 테이블의 날짜를 외래키로 해서 그 날짜 중 언제 언제 패킷이 감지되었는지를 담고 있습니다.

아래는 스터디 테이블의 sql문입니다.

<img width="299" alt="study 테이블" src="https://user-images.githubusercontent.com/60510921/97783910-3624c080-1bde-11eb-8310-91da7bcbf0ea.png">

스터디 테이블의 어트리뷰트들을 설명하자면
Study_date는 기본키로 날짜별 공부 시간을 구분해줄 어트리뷰트입니다.
Study_time은 각 날짜별 공부량이 어느정도인지 알려주는 어트리뷰트이고
Study_goal은 사용자가 설정할 공부 목표량이며 Study_achieved들의 값을 얻어낼때 도움을 받고자 넣었습니다.
Study_achieved는 사용자가 하루의 목표량을 달성했는지 알려주기 위한 어트리뷰트입니다.

아래는 패킷 테이블의 sql문입니다.

<img width="338" alt="변경된 패킷 sql" src="https://user-images.githubusercontent.com/60510921/97792826-f7672880-1c26-11eb-8a4e-b27f08596db3.png">

패킷 테이블의 어트리뷰트들은
복합키로 기본키를 구성하는 Packet_time과 Packet_date가 있습니다.
Packet_date는 스터디 테이블의 Study_date의 외래키이며 날짜를 나타내고 Packet_time은 하루 중 전체 접속시간이 얼마인지 나타냅니다.
Packet_RSSI는 감지된 패킷의 신호 세기가 얼마인지를 나타냅니다.

마지막으로 디택트 테이블의 sql문입니다.

<img width="377" alt="detect table" src="https://user-images.githubusercontent.com/60510921/97792832-02ba5400-1c27-11eb-867c-edffc7fb2eef.png">

이 테이블은 패킷 테이블에 하루 중 언제 감지가 되었는지를 표현하기엔 무리가 있어서 만들었습니다.
detect_date로 패킷 테이블을 참조하며
detect_time으로 감지된 시간들을 표시합니다.


위의 데이터 베이스를 aws에 구축하기 위해 ec2와 rds를 이용했습니다.

아래는 ec2 접속 사진이고

<img width="412" alt="20201031_234212" src="https://user-images.githubusercontent.com/60510921/97783933-56547f80-1bde-11eb-90f7-50452175f307.png"> 

아래는 rds 구축 사진입니다.

<img width="464" alt="20201101_000106" src="https://user-images.githubusercontent.com/60510921/97783936-5e142400-1bde-11eb-903e-11acf481bb71.png">

