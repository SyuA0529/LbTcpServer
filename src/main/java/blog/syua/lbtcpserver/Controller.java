package blog.syua.lbtcpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import blog.syua.lbtcpserver.dto.ControlRequest;
import blog.syua.lbtcpserver.dto.ControlSuccessResponse;
import blog.syua.lbtcpserver.dto.ControlType;
import blog.syua.lbtcpserver.dto.Protocol;

@RestController
public class Controller {

	@Value("${loadbalancer.ip}")
	private String lbIpAddr;

	@Value("${loadbalancer.port}")
	private int lbPort;

	@Value("${server.port}")
	private int port;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@PostConstruct
	public void init() throws IOException {
		new HealthCheckRequestHandler(5555).startHandle();
		doControlRequest(ControlType.REGISTER);
	}

	@GetMapping("/register")
	public String handleRegisterRequest() throws IOException {
		doControlRequest(ControlType.REGISTER);
		return "Success Register";
	}

	@GetMapping("/un-register")
	public String handleUnRegisterRequest() throws IOException {
		doControlRequest(ControlType.UNREGISTER);
		return "Success UnRegister";
	}

	@GetMapping("/**")
	public String echoName(String str) throws UnknownHostException {
		return "[" + InetAddress.getLocalHost().getHostAddress() + " " + port + "] receive string: " + str;
	}

	private void doControlRequest(ControlType type) throws IOException {
		try (Socket socket = new Socket(InetAddress.getByName(lbIpAddr), lbPort)) {
			try (InputStream inputStream = socket.getInputStream();
				 OutputStream outputStream = socket.getOutputStream()) {
				outputStream.write(objectMapper.writeValueAsBytes(
					new ControlRequest(type, Protocol.TCP, port)));
				outputStream.flush();
				socket.shutdownOutput();
				byte[] bytes = inputStream.readAllBytes();
				System.out.println(new String(bytes, StandardCharsets.UTF_8));
				objectMapper.readValue(bytes, ControlSuccessResponse.class);
			}

		}
	}

}
