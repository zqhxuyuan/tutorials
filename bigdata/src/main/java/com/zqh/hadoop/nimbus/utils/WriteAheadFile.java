package com.zqh.hadoop.nimbus.utils;

import java.io.IOException;

import com.zqh.hadoop.nimbus.main.NimbusConf;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

public class WriteAheadFile extends NimbusOutputStream {

	private static final Logger LOG = Logger.getLogger(WriteAheadFile.class);
	private Path file = null;
	private FileSystem fs = null;

	public WriteAheadFile(Path p) throws IOException {
		fs = FileSystem.get(NimbusConf.getConf());
		this.file = p;
		LOG.info("Creating " + file + " for write");
	}

	public void open() throws IOException {
		super.setOutputStream(fs.create(file));
	}

	public void delete() throws IOException {
		if (fs.exists(file)) {
			fs.delete(file, false);
		}
	}

	public Path getFile() {
		return file;
	}
}
