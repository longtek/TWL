package com.longtek.bluetoothUtil;

import java.io.Serializable;

/**
 * 用于传输的数据类 TransmitBean，实现Serializable 接口，将其对象转换成字节系列存储在字节数组中
 */
public class TransmitBean implements Serializable{

	/**
	 * 空接口类Serializable，只是简单标识TansmitBean类的对象可被系列化 
	 */
	private static final long serialVersionUID = 1L;
	private String msg = "";
	private String filename = "";
	private String filepath = "";		//把对象序列化（持久性存储）到硬盘的指定位置(反序列化就是把存在硬盘上的这个对象读取到内存中来)
	private String uppercent = "";
	private String tspeed = "";
	private boolean showflag;
	private byte[] file ;
//	private  BluetoothCommunThread communThread;	
	
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getMsg() {
		return this.msg;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getUppercent() {
		return uppercent;
	}

	public void setUppercent(String uppercent) {
		this.uppercent = uppercent;
	}

	public String getTspeed() {
		return tspeed;
	}

	public void setTspeed(String tspeed) {
		this.tspeed = tspeed;
	}

	public boolean isShowflag() {
		return showflag;
	}

	public void setShowflag(boolean showflag) {
		this.showflag = showflag;
	}

	
//	public BluetoothCommunThread getCommunThread() {
//		return communThread;
//	}
//
//	public void setCommunThread(BluetoothCommunThread communThread) {
//		this.communThread = communThread;
//	}
	
	
}
