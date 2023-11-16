package blog.syua.lbtcpserver.dto;

import lombok.Getter;

@Getter
public enum Protocol {

	TCP(0xFFFF),
	UDP(0xFFFF);

	private final int maxReceiveSize;

	Protocol(int maxReceiveSize) {
		this.maxReceiveSize = maxReceiveSize;
	}

}