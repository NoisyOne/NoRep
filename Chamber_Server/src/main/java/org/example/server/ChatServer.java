package org.example.server;

import org.example.server.cache.UserCache;
import org.example.server.handler.ClientHandler;
import org.example.server.repository.UserRepository;
import org.example.server.service.UserService;
import org.example.server.util.DatabaseUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ChatServer {
    private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 30;

    private final ExecutorService threadPool;
    private final UserCache userCache;
    private final UserService userService;
    private volatile boolean isRunning = true; // 用于控制服务器运行状态

    public ChatServer() throws IOException {
        UserRepository userRepository = new UserRepository();
        this.userService = new UserService(userRepository); // 赋值给成员变量
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.userCache = new UserCache(this.userService);

        LOGGER.info("Chat server started on port " + PORT);

        // 初始化数据库连接
        DatabaseUtil.init();

        // 加载已存在的在线用户状态到缓存
        userCache.loadOnlineUsers();

        // 添加关闭钩子
        addShutdownHook();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Chat server is listening on port " + PORT);

            while (isRunning) {
                try {
                    Socket socket = serverSocket.accept();
                    LOGGER.info("New client connected: " + socket.getInetAddress().getHostAddress());

                    // 提交客户端处理任务到线程池
                    threadPool.submit(new ClientHandler(socket, userCache, userService));
                } catch (IOException e) {
                    if (isRunning) { // 只在服务器运行时记录错误
                        LOGGER.severe("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.severe("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            shutdown();
        }
    }

    /**
     * 优雅关闭服务器
     */
    public void shutdown() {
        isRunning = false;
        LOGGER.info("Shutting down server...");

        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                LOGGER.warning("Thread pool did not terminate gracefully, forcing shutdown");
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 清理资源
        userCache.clearAllUsers();
        DatabaseUtil.close();
        LOGGER.info("Server shutdown complete");
    }

    /**
     * 添加关闭钩子，确保程序被终止时能够优雅关闭
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Received shutdown signal");
            shutdown();
        }));
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException e) {
            LOGGER.severe("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
