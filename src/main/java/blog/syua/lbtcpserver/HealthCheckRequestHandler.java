package blog.syua.lbtcpserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;

import blog.syua.lbtcpserver.dto.HealthCheckRequest;
import blog.syua.lbtcpserver.dto.HealthCheckResponse;

public class HealthCheckRequestHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final ExecutorService threadPool;
	private final int port;

	public HealthCheckRequestHandler(int port) {
		this.threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		this.port = port;
	}

	public void startHandle() {
		new Thread(() -> {
			try (ServerSocket serverSocket = new ServerSocket(port)) {
				Socket nodeSocket;
				while (Objects.nonNull(nodeSocket = serverSocket.accept())) {
					Socket finalNodeSocket = nodeSocket;
					threadPool.execute(() -> handleRequest(finalNodeSocket));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void handleRequest(Socket socket) {
		try (InputStream inputStream = socket.getInputStream();
			 OutputStream outputStream = socket.getOutputStream()) {
			byte[] bytes = inputStream.readAllBytes();
			HealthCheckRequest request = objectMapper.readValue(bytes, HealthCheckRequest.class);
			outputStream.write(objectMapper.writeValueAsBytes(new HealthCheckResponse()));
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
