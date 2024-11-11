## 1. 프로젝트 소개
민원을 쉽고 간편하게! 민원고
민원고는 길거리 보도 불편사항을 간편하게 사진만 촬영하면 자동으로 민원을 신고해주는 서비스 입니다.
<br>

## 2. 팀원 소개 (한국 폴리텍 대학교 서울 정수 캠퍼스 인공지능 소프트웨어과)

| 이름     | 역할                | 기술 스택                                                                                                                                           | 소개                                                                                                      | 
|:---------|:--------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------|
| [나예은](https://github.com/yeeun03030) | 팀원                | ![Spring Boot](https://img.shields.io/badge/spring--boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white) <br> ![Kotlin](https://img.shields.io/badge/kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white) <br> ![Android Studio](https://img.shields.io/badge/android--studio-3DDC84?style=flat-square&logo=android-studio&logoColor=white) | 앱 개발 담당 <br> Spring Boot 기반 민원 서버 제작 <br> Thymeleaf를 활용한 Spring Boot 프론트엔드 구현 <br> 발표자료 작성 및 PPT 제작|
| [유승호](https://github.com/00seungho) | 팀원                | ![Python](https://img.shields.io/badge/python-3776AB?style=flat-square&logo=python&logoColor=white) <br> ![Flask](https://img.shields.io/badge/flask-000000?style=flat-square&logo=flask&logoColor=white) <br> ![PyTorch](https://img.shields.io/badge/pytorch-EE4C2C?style=flat-square&logo=pytorch&logoColor=white) <br> ![Spring Boot](https://img.shields.io/badge/spring--boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white) | Flask 기반 AI 관제 서버 개발 <br> Azure 커스텀 비전 모델 훈련 <br> Spring Boot REST API 통신 구축 <br> Azure 서버 환경 설정 <br> 프로젝트 아키텍처 설계 |


## 3. 개요
- **프로젝트 이름**: 민원고
- **구축 서버**: ![Microsoft Azure](https://img.shields.io/badge/microsoft--azure-0078D4?style=flat-square&logo=microsoft-azure&logoColor=white)

- **OS**: ![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=flat-square&logo=ubuntu&logoColor=white)
- **개발 언어**: ![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=Java&logoColor=white) ![Python](https://img.shields.io/badge/python-3776AB?style=flat-square&logo=python&logoColor=white) ![Kotlin](https://img.shields.io/badge/kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
- **개발 프레임워크**: ![Spring Boot](https://img.shields.io/badge/spring_boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white) ![Flask](https://img.shields.io/badge/flask-000000?style=flat-square&logo=flask&logoColor=white) 
- **서버**: ![Apache Tomcat](https://img.shields.io/badge/Apache%20Tomcat-F8DB2D?style=flat-square&logo=apachetomcat&logoColor=black) ![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=nginx&logoColor=white) ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)

## 4. 구현 일정

| 구분 | 추진 내용 | 추진 일정 | 1주 |
|------|-----------|-----------|-----|
| 실행 | 구축     |           | ■   |
| 실행 | 개발     |           | ■   |
## 5. 서버 아키텍쳐 설계
![서버아키텍쳐](img/Architecture.png)

## 6. 구현 핵심 기술
### Azure Custom Vison
- Object Dection 모델 제작
- RestAPI 형태의 엔드포인트 지원으로 손쉬운 추론

### Spring Boot 관리자 서버
-   Spring boot 사용하여 RESTful API 서버 개발
-   SpringSecurity를 사용하여 사용자 인증 및 권한 관리
-   세션기반의 인증으로 인증절차 간소화

### Flask 기반 AI관제 서버
-   Azure Custom Vison에 이미지 전송을 위한 전처리
-   사용자의 위치정보를 받아 KaKao 지도 API를 이용해 행정구역 반환

## 5. 시현 내용
유튜브 링크
[![Video Label](http://img.youtube.com/vi/Wf4--zl_GYM/0.jpg)](https://youtu.be/Wf4--zl_GYM?t=0s)
