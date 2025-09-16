#!/bin/bash

# --- 检查并重启所有已退出的 Docker 容器 ---

echo "正在检查处于 'exited' 状态的容器..."

# 使用 -q (quiet) 标志来只获取容器的ID，方便脚本处理
# 将所有找到的ID存储在一个变量中
EXITED_CONTAINER_IDS=$(docker ps -a -f status=exited -q)

# 检查变量是否为空
# -z 用于判断字符串是否为空
if [ -z "$EXITED_CONTAINER_IDS" ]; then
    # 如果为空，说明没有已退出的容器
    echo "没有发现已退出的容器。一切正常！"
else
    # 如果不为空，则打印提示信息和找到的容器ID
    echo "发现以下已退出的容器，将尝试重启它们："
    # 直接打印变量，由于ID是按行分隔的，会清晰地列出
    echo "$EXITED_CONTAINER_IDS"
    echo "----------------------------------------"

    # 循环遍历所有找到的ID
    # shell 会自动根据换行符来分隔ID列表
    for container_id in $EXITED_CONTAINER_IDS; do
        echo "正在重启容器: $container_id ..."
        # 执行重启命令
        docker restart "$container_id"
    done

    echo "----------------------------------------"
    echo "所有已退出的容器都已尝试重启。"
fi

exit 0