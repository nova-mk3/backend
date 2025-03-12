# 젠킨스 공식 이미지 사용
FROM jenkins/jenkins:lts-jdk21

# 루트 권한으로 실행
USER root

# Docker CLI 설치를 위한 패키지 업데이트 및 설치
RUN apt-get update && apt-get install -y \
    apt-transport-https ca-certificates curl gnupg lsb-release

# Docker 공식 GPG 키 추가
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | tee /usr/share/keyrings/docker-archive-keyring.asc

# Docker 저장소 추가
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.asc] \
    https://download.docker.com/linux/debian $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list

# 패키지 리스트 업데이트 후 Docker CLI 설치
RUN apt-get update && apt-get install -y docker-ce-cli

# Jenkins 플러그인 설치 (예: BlueOcean 및 Docker 지원)
RUN jenkins-plugin-cli --plugins "blueocean docker-workflow"

# 다시 Jenkins 사용자로 변경
USER jenkins