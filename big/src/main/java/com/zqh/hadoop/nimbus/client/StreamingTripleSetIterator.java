package com.zqh.hadoop.nimbus.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.zqh.hadoop.nimbus.nativestructs.Triple;
import com.zqh.hadoop.nimbus.utils.BytesUtil;

public class StreamingTripleSetIterator implements Iterator<Triple> {

	private List<TripleSetCacheletConnection> clients = null;
	private TripleSetCacheletConnection client = null;
	private Iterator<TripleSetCacheletConnection> clientIter = null;
	private Triple currentTriple = new Triple();
	private Triple nextTriple = new Triple();
	private long numEntries = 0, numRead = 0;

	public void addClient(TripleSetCacheletConnection connect) throws IOException {
		this.clients.add(connect);
	}

	public void initialize() {
		clientIter = clients.iterator();
		client = clientIter.next();
		
		advanceTriple();
	}

	@Override
	public boolean hasNext() {
		return client != null;
	}

	@Override
	public Triple next() {
		if (client != null) {
			advanceTriple();
			nextTriple.set(currentTriple);

			return nextTriple;
		} else {
			return null;
		}
	}

	private void advanceTriple() {
		try {
			advanceTriple();
			nextTriple.set(currentTriple);
			if (numEntries == numRead) {
				// advance iterator to next entry
				client = clientIter.next();

				if (client == null) {
					return;
				}
				
				numEntries += Integer.parseInt(BytesUtil.toString(client.in.readArg()));
			}

			currentTriple.setFirst(BytesUtil.toString(client.in.readArg()));
			currentTriple.setSecond(BytesUtil.toString(client.in.readArg()));
			currentTriple.setThird(BytesUtil.toString(client.in.readArg()));
			numRead += 3;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove using iterator");
	}
}
