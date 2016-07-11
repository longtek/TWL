package com.longtek.bluetooth_control;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Fac_BroadcastReceiver extends BroadcastReceiver
{
	private Fac_Manager m_manager = null;
  
	public Fac_BroadcastReceiver() {
		
	}
  
	public Fac_BroadcastReceiver(Fac_Manager paramFac_Manager)
	{
		this.m_manager = paramFac_Manager;
	}
  
	//注册广播接收器 回调onReceive()函数，接收扫描到的蓝牙设备
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if ("android.bluetooth.device.action.FOUND".equals(action))
		{
			BluetoothDevice BtDevice = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
			this.m_manager.NewDeviceDetected(BtDevice);
		}
		if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
			this.m_manager.DiscoveryFinished();
		}
	}
}

