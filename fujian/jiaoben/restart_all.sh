#!/bin/bash

# 设置脚本所在的目录，确保脚本可以从任何地方正确运行
cd /home/ec2-user/jiaoben || exit

# 统一定义日志函数，方便添加时间戳
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] - $1"
}

log "--- 开始执行应用和环境的完整重启流程 ---"

# 1. 执行 stop_app.sh
log "正在执行 stop_app.sh..."
./stop_app.sh
if [ $? -ne 0 ]; then
    log "错误: stop_app.sh 执行失败！中止流程。"
    exit 1
fi
log "stop_app.sh 执行成功。"
log "等待 10 秒..."
sleep 10

# 2. 执行 stop_env.sh
log "正在执行 stop_env.sh..."
./stop_env.sh
if [ $? -ne 0 ]; then
    log "错误: stop_env.sh 执行失败！中止流程。"
    exit 1
fi
log "stop_env.sh 执行成功。"
log "等待 10 秒..."
sleep 10

# 3. 执行 start_env.sh
log "正在执行 start_env.sh..."
./start_env.sh
if [ $? -ne 0 ]; then
    log "错误: start_env.sh 执行失败！中止流程。"
    exit 1
fi
log "start_env.sh 执行成功。"
log "等待 10 秒..."
sleep 10

# 4. 执行 start_app.sh
log "正在执行 start_app.sh..."
./start_app.sh
if [ $? -ne 0 ]; then
    log "错误: start_app.sh 执行失败！中止流程。"
    exit 1
fi
log "start_app.sh 执行成功。"

log "--- 完整重启流程已成功完成 ---"

exit 0