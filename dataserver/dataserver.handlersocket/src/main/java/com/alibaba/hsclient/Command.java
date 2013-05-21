package com.alibaba.hsclient;

import java.io.UnsupportedEncodingException;

public interface Command {
	public byte[] toByte(String encoding) throws UnsupportedEncodingException;
}
