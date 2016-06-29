package com.longtek.bluetooth_control;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Fac_BroadcastReceiver extends BroadcastReceiver {
	
	private Fac_Manager m_manager;
	
	public Fac_BroadcastReceiver(Fac_Manager fac_Manager)
	{
		this.m_manager = fac_Manager;
	}
	
	//注册广播接收器，回调onReceiver()方法，接收扫描到的蓝牙设备
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if("android.bluetooth.device.action.FOUND".equals(action))
		{
			BluetoothDevice device = intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
			this.m_manager.NewDeviceDetected(device);
		}
		if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
			this.m_manager.DiscoveryFinished();
		}
	}

}
