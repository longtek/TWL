package com.longtek.bluetooth_control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

/**蓝牙操作步骤：
 * 1.使用蓝牙响应权限
 * 2.配置本机蓝牙适配器
 * 3.搜索蓝牙设备
 * 4.蓝牙Socket通讯
 * 	a.客户端实现
 * 	b.服务器端实现
 * 	c.连接管理（数据通讯）
 * */
public class Fac_Manager {
	
	private static Fac_Manager m_UniqueInstance;
	public Fac_BroadcastReceiver bReceiver;
	private Fac_com m_com;
	private PC_Data m_Data;
	private Fac_Handler m_handler;
	private BluetoothAdapter myBluetoothAdapter;
	private Context m_context;
	private Context m_Connect;
	
	public Fac_Manager(Context context)
	{
		this.m_context = context;
		this.m_Data = new PC_Data();
		this.bReceiver = new Fac_BroadcastReceiver(this);
		this.m_handler = new Fac_Handler(this);
		this.m_com = new Fac_com(this.m_handler);
		this.myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
		
		//检查本地蓝牙是否打开
		if (!this.myBluetoothAdapter.isEnabled())
		{
			this.myBluetoothAdapter.enable();
			Log.d("Fac_Manager", "Bluetooth is not active!");
		}
		//调用registerReceiver函数注册广播接收器，实现扫描蓝牙设备和返回发现的设备
		this.m_context.registerReceiver(this.bReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"));
		this.m_context.registerReceiver(this.bReceiver, new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED"));
		m_UniqueInstance = this;
	}
	
	//扫描周围的蓝牙设备
		public void ScanDevice()
		{
			if (this.myBluetoothAdapter.isDiscovering()) {
				this.myBluetoothAdapter.cancelDiscovery();
			}
			ClearDevices();
			if (!this.myBluetoothAdapter.startDiscovery()) {
				Log.d("Fac_Manager", "Scaning SoundCreadtor devices.");
			}
		}
		
		public void ClearDevices()
		{
			this.m_Data.clearDevices();
		}
		
		public void DiscoveryFinished()
		{
			if (this.m_Connect != null) {
				((Connect)this.m_Connect).HideProgressBar();
			}
		}
		//检测到新的蓝牙设备
		public void NewDeviceDetected(BluetoothDevice device)
		{
			this.m_Data.addDevice(device);
			if (this.m_Connect != null) {
				((Connect)this.m_Connect).NewDeviceDetected(device);
			}
		}
		  
		public boolean Connect(int indext)
		{
			BluetoothDevice device = this.m_Data.getDevice(indext);
			if (this.myBluetoothAdapter.isDiscovering()) {
				this.myBluetoothAdapter.cancelDiscovery();
			}
			for (;;)
			{
				if (!this.myBluetoothAdapter.isDiscovering())
				{
					boolean bool = this.m_com.connect(device);
					if ((this.m_Connect != null) && (!bool)) {
						((Connect)this.m_Connect).ConnectionDone(false);
					}
					return bool;
				}
				try
				{
					Thread.sleep(100L);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	  
		public void ConnectionDone(byte[] ArrayOfByte)
		{
			String str = new String();
			if (this.m_Connect != null) {
				((Connect)this.m_Connect).ConnectionDone(str.equals("y"));
			}
			((MainActivity)this.m_context).ConnectionDone(str.equals("y"));
			if (str.equals("y")) {
				this.m_com.getSoundCreatorVersion();
			}
		}	
		
		public boolean IsConnected()
		{
			return this.m_com.IsConnected();
		}
		
		public void disconnect()
		{
			this.m_com.disconnect();
			this.m_Data.setM_ListBox(null);
			((MainActivity)this.m_context).ConnectionDone(false);
			if (this.m_Connect != null) {
				((Connect)this.m_Connect).DisconnectionDone(true);
			}
		}
		
		public PC_Data getM_Data()
		{
			return this.m_Data;
		}
		
//	  public void Previous()
//	  {
//		  if (!IsConnected())
//		  {
//			  ((MainActivity)this.m_context).PleaseDoConnection();
//			  return;
//		  }
//		  this.m_Data.BoxCurrentDecrease();
//		  this.m_com.LoadBoxCmd();
//	  }

}
