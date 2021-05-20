package fr.mercury.combattag.utils.jsons;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DiscUtil {
	private static final String UTF8 = "UTF-8";
	
	public static byte[] readBytes(File file) throws IOException {
		int length = (int) file.length();
		byte[] output = new byte[length];
		InputStream in = new FileInputStream(file);
		int offset = 0;
		while (offset < length) {
			offset += in.read(output, offset, length - offset);
		}
		in.close();
		return output;
	}

	public static void writeBytes(File file, byte[] bytes) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(bytes);
		out.close();
	}

	public static void write(File file, String content) throws IOException {
		writeBytes(file, utf8(content));
	}

	public static String read(File file) throws IOException {
		return utf8(readBytes(file));
	}

	private static HashMap<String, Lock> locks = new HashMap<>();

	public static boolean writeCatch(JavaPlugin owner, final File file, String content, boolean sync) {
		final byte[] bytes = utf8(content);
		String name = file.getName();

		final Lock lock;
		if (locks.containsKey(name)) {
			lock = (Lock) locks.get(name);
		} else {
			ReadWriteLock rwl = new ReentrantReadWriteLock();
			lock = rwl.writeLock();
			locks.put(name, lock);
		}

		if (!sync) {
			lock.lock();
			try {
				FileOutputStream out = new FileOutputStream(file);
				out.write(bytes);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		} else {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					lock.lock();
					try {
						FileOutputStream out = new FileOutputStream(file);
						out.write(bytes);
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						lock.unlock();
					}
				}
			}, 0);
		}
		return true;
	}

	public static String readCatch(File file) {
		try {
			return read(file);
		} catch (IOException e) {
		}
		return null;
	}

	public static byte[] utf8(String string) {
		try {
			return string.getBytes(UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String utf8(byte[] bytes) {
		try {
			return new String(bytes, UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
