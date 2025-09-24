#!/bin/bash
# ----------------------------------------
# CentOS 7.9 Docker开发环境安装脚本
# 包含组件: JDK 1.8, MySQL 5.7, Redis 7.0, 
# Elastic Stack 7.17.3, MongoDB 5.0,
# RabbitMQ 3.10.5, Nginx 1.22
# ----------------------------------------

# 安装必要工具
sudo yum install -y wget unzip

# 准备数据存储目录
sudo mkdir -p /docker/data/{mysql,redis,es,kibana,mongodb,rabbitmq,nginx}
sudo chmod -R 777 /docker/data

# 创建自定义网络
docker network create dev-network

# 1. 拉取所有镜像
docker pull openjdk:8-jdk-alpine
docker pull mysql:5.7
docker pull redis:7.0
docker pull elasticsearch:7.17.3
docker pull kibana:7.17.3
docker pull logstash:7.17.3
docker pull mongo:5.0
docker pull rabbitmq:3.10.5-management
docker pull nginx:1.22

# 2. 启动MySQL 5.7
docker run -d --name mysql --net dev-network \
  -p 3306:3306 \
  -v /docker/data/mysql:/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD=yourpassword \
  mysql:5.7 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

# 3. 启动Redis 7.0
docker run -d --name redis --net dev-network \
  -p 6379:6379 \
  -v /docker/data/redis:/data \
  redis:7.0 redis-server --appendonly yes

# 4. 启动Elasticsearch 7.17.3
sudo sysctl -w vm.max_map_count=262144
docker run -d --name elasticsearch --net dev-network \
  -p 9200:9200 -p 9300:9300 \
  -v /docker/data/es:/usr/share/elasticsearch/data \
  -e "discovery.type=single-node" \
  -e "ES_JAVA_OPTS=-Xms1g -Xmx1g" \
  elasticsearch:7.17.3

# 5. 启动Kibana 7.17.3
docker run -d --name kibana --net dev-network \
  -p 5601:5601 \
  -v /docker/data/kibana:/usr/share/kibana/config \
  --link elasticsearch:elasticsearch \
  -e "ELASTICSEARCH_HOSTS=http://elasticsearch:9200" \
  kibana:7.17.3

# 6. 启动Logstash 7.17.3
cat > logstash.conf <<EOF
input {
  tcp {
    port => 5044
    codec => json_lines
  }
}
output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "logstash-%{+YYYY.MM.dd}"
  }
}
EOF

docker run -d --name logstash --net dev-network \
  -p 5044:5044 \
  -v $(pwd)/logstash.conf:/usr/share/logstash/pipeline/logstash.conf \
  logstash:7.17.3

# 7. 启动MongoDB 5.0
docker run -d --name mongodb --net dev-network \
  -p 27017:27017 \
  -v /docker/data/mongodb:/data/db \
  mongo:5.0

# 8. 启动RabbitMQ 3.10.5
docker run -d --name rabbitmq --net dev-network \
  -p 5672:5672 -p 15672:15672 \
  -v /docker/data/rabbitmq:/var/lib/rabbitmq \
  -e RABBITMQ_DEFAULT_USER=admin \
  -e RABBITMQ_DEFAULT_PASS=adminpassword \
  rabbitmq:3.10.5-management

# 9. 启动Nginx 1.22
docker run -d --name nginx --net dev-network \
  -p 80:80 -p 443:443 \
  -v /docker/data/nginx/html:/usr/share/nginx/html \
  -v /docker/data/nginx/conf:/etc/nginx/conf.d \
  nginx:1.22

# 10. 创建测试Java容器
docker run -d --name java-app --net dev-network \
  openjdk:8-jdk-alpine sleep infinity

# 验证安装结果
echo "安装完成! 请验证以下服务:"
echo "------------------------------------"
echo "MySQL:        端口 3306"
echo "Redis:        端口 6379"
echo "Elasticsearch:端口 9200"
echo "Kibana:       http://<服务器IP>:5601"
echo "Logstash:     端口 5044"
echo "MongoDB:      端口 27017"
echo "RabbitMQ:     http://<服务器IP>:15672 (账号: admin, 密码: adminpassword)"
echo "Nginx:        http://<服务器IP>"
echo "------------------------------------"
echo "所有容器运行状态:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"


