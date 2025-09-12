`docker logs mall-gateway` 命令一直在运行，这通常是因为您使用了 `-f` 或 `--follow` 参数来实时跟踪日志。这个命令本身并不是卡住了，而是在**等待容器产生新的日志并输出到您的终端**。这是一种正常行为，类似于在Linux中使用 `tail -f`命令。

### 如何停止

要停止正在运行的 `docker logs` 命令，您只需要在终端中按下键盘快捷键：

**`Ctrl + C`**

按下这个组合键后，终端会发送一个中断信号给 `docker logs` 进程，该进程会立即停止，您将返回到命令提示符。

-----

### 常用 `docker logs` 命令总结

为了方便您以后使用，这里列出几种常见的 `docker logs` 用法：

1.  **查看所有当前日志并退出（不会持续运行）：**
    这是最基础的用法，命令执行完毕后会自动退出。

    ```bash
    docker logs mall-gateway
    ```

2.  **实时跟踪日志（您当前遇到的情况）：**
    使用 `-f` 或 `--follow` 参数。按 `Ctrl + C` 退出。

    ```bash
    docker logs -f mall-gateway
    ```

3.  **只查看最近的N行日志：**
    使用 `--tail` 参数，这在只想看最新日志时非常有用。例如，查看最近的 100 行：

    ```bash
    docker logs --tail 100 mall-gateway
    ```

4.  **跟踪并显示最近的N行日志：**
    组合使用 `-f` 和 `--tail`，可以先看到最近的200行日志，然后继续实时跟踪新日志。

    ```bash
    docker logs -f --tail 200 mall-gateway
    ```

**总结：** 您遇到的情况是正常的，只需按下 **`Ctrl + C`** 即可停止日志跟踪。