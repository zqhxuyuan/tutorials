package com.zqh.hadoop.nimbus.zk;

import java.io.IOException;

public interface ConnectListener {

	public void connected() throws IOException;

	public void closing() throws IOException;
}
