package blog.syua.lbtcpserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ControlRequest {

	private ControlType cmd;
	private Protocol protocol;
	private int port;

}
