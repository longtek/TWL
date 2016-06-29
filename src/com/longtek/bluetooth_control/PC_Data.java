package com.longtek.bluetooth_control;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;

public class PC_Data {
	
	private String[] m_ListBox;

	private BluetoothDevice m_Device;
	private ArrayList<BluetoothDevice> m_listDevice;
	
	public BluetoothDevice getDevice(int indext)
	{
		this.m_Device = ((BluetoothDevice)this.m_listDevice.get(indext));
		return this.m_Device;
	}

	public ArrayList<BluetoothDevice> getListDevice()
	{
		return this.m_listDevice;
	}
	  
	public void setListDevice(ArrayList<BluetoothDevice> ArrayList)
	{
		this.m_listDevice = ArrayList;
	}
	  
	public void setM_Device(BluetoothDevice device)
	{
		this.m_Device = device;
	}

	public void addDevice(BluetoothDevice device)
	{
		this.m_listDevice.add(device);
	}

	public BluetoothDevice getM_Device()
	{
		return this.m_Device;
	}
	  
	public void clearDevices()
	{
		this.m_listDevice.clear();
	}
	  
	public void setM_ListBox(String[] buffer)
	{
		this.m_ListBox = buffer;
	}
}
