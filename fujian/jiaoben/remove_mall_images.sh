#!/bin/bash

# 定义一个带时间戳的日志函数
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] - $1"
}

log "--- 开始清理 'mall' 相关的 Docker 镜像 ---"

# 使用 docker images --filter 命令精确查找所有仓库名为 'mall/*' 的镜像
# -q 参数表示只输出镜像的ID，这是最适合脚本处理的方式
MALL_IMAGE_IDS=$(docker images --filter="reference=mall/*" -q)

# 检查是否找到了任何相关的镜像
# -z 用于判断字符串是否为空
if [ -z "$MALL_IMAGE_IDS" ]; then
    log "没有找到 'mall' 相关的镜像，无需执行任何操作。"
else
    log "发现以下 'mall' 相关的镜像ID，将执行删除操作："
    # 直接打印变量，让用户可以看到将要删除哪些ID
    echo "$MALL_IMAGE_IDS"
    log "----------------------------------------"

    # 使用 xargs 将所有ID传递给 docker rmi 命令，这比 for 循环更高效
    # --no-run-if-empty 参数确保在ID列表为空时，不执行 rmi 命令
    echo "$MALL_IMAGE_IDS" | xargs --no-run-if-empty docker rmi -f

    # 检查删除命令的执行结果
    if [ $? -eq 0 ]; then
        log "成功删除所有 'mall' 相关的镜像。"
    else
        log "删除过程中可能出现错误。请检查是否有容器仍在使用这些镜像。"
        log "您可以先运行 'docker ps -a' 查看，并使用 'docker stop' 和 'docker rm' 来清理占用的容器。"
    fi
fi

log "--- 清理脚本执行完毕 ---"

exit 0