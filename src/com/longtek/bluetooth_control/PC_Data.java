package com.longtek.bluetooth_control;

import android.bluetooth.BluetoothDevice;
import java.util.ArrayList;

public class PC_Data
{
	  byte[] m_BoxBytes = null;
	  private int m_BoxCurrent = 0;
	  byte[] m_CcgBytes = null;
	  String m_CcgName = null;
	  private BluetoothDevice m_Device = null;
	  private String m_SoundCreatorVersion = null;
	  private String[] m_ListBox = null;
	  private String m_Logs = "";
	  private boolean m_Muted = false;
	  final int m_NBytesHeader = 16;
	  final int m_NBytesNom = 20;
	  final int m_NBytesParam = 12456;
	  final int m_NParamCcg = 3;
	  private int m_NbBox = 0;
	  final int m_NbBoxMax = 10;
	  private ArrayList<BluetoothDevice> m_listDevice = null;
  
	  public int BoxCurrentDecrease()
	  {
		  if (this.m_NbBox > 0) {
			  this.m_BoxCurrent = ((this.m_BoxCurrent - 1 + this.m_NbBox) % this.m_NbBox);
		  }
		  return this.m_BoxCurrent;
	  }
  
	  public int BoxCurrentIncrease()
	  {
		  if (this.m_NbBox > 0) {
			  this.m_BoxCurrent = ((this.m_BoxCurrent + 1) % this.m_NbBox);
		  }
		  return this.m_BoxCurrent;
	  }
  
	  public void addDevice(BluetoothDevice paramBluetoothDevice)
	  {
		  this.m_listDevice.add(paramBluetoothDevice);
	  }
  
	  public void clearDevices()
	  {
		  this.m_listDevice.clear();
	  }
  
	  public void clearLogs()
	  {
		  this.m_Logs = "";
	  }
  
	  public BluetoothDevice getDevice(int paramInt)
	  {
		  this.m_Device = ((BluetoothDevice)this.m_listDevice.get(paramInt));
		  return this.m_Device;
	  }
  
	  public ArrayList<BluetoothDevice> getListDevice()
	  {
		  return this.m_listDevice;
	  }
  
	  public byte[] getM_BoxBytes()
	  {
		  return this.m_BoxBytes;
	  }
  
	  public int getM_BoxCurrent()
	  {
		  return this.m_BoxCurrent;
	  }
  
	  public byte[] getM_CcgBytes()
	  {
		  return this.m_CcgBytes;
	  }
  
	  public String getM_CcgName()
	  {
		  return this.m_CcgName;
	  }
  
	  public BluetoothDevice getM_Device()
	  {
		  return this.m_Device;
	  }
  
	  public String getM_SoundCreatorVersion()
	  {
		  return this.m_SoundCreatorVersion;
	  }
  
	  public String[] getM_ListBox()
	  {
		  return this.m_ListBox;
	  }
  
	  public String getM_Logs()
	  {
		  return this.m_Logs;
	  }
  
	  public int getM_NBytesHeader()
	  {
		  return 16;
	  }
  
	  public int getM_NBytesNom()
	  {
		  return 20;
	  }
  
	  public int getM_NBytesParam()
	  {
		  return 12456;
	  }
  
	  public int getM_NParamCcg()
	  {
		  return 3;
	  }
  
	  public int getM_NbBox()
	  {
		  return this.m_NbBox;
	  }
  
	  public boolean isM_Muted()
	  {
		  return this.m_Muted;
	  }
  
	  public void setListDevice(ArrayList<BluetoothDevice> ArrayList)
	  {
		  this.m_listDevice = ArrayList;
	  }
  
	  public void setM_BoxBytes(byte[] paramArrayOfByte)
	  {
		  this.m_BoxBytes = paramArrayOfByte;
	  }
  
	  public void setM_BoxCurrent(int Int)
	  {
		  this.m_BoxCurrent = Int;
	  }
  
	  public void setM_CcgBytes(byte[] paramArrayOfByte)
	  {
		  this.m_CcgBytes = paramArrayOfByte;
	  }
  
	  public void setM_CcgName(String paramString)
	  {
		  this.m_CcgName = paramString;
	  }
  
	  public void setM_Device(BluetoothDevice paramBluetoothDevice)
	  {
		  this.m_Device = paramBluetoothDevice;
	  }
  
	  public void setM_SoundCreatorVersion(String string)
	  {
		  this.m_SoundCreatorVersion = string;
	  }
  
	  public void setM_ListBox(String[] ArrayOfString)
	  {
		  this.m_ListBox = ArrayOfString;
	  }
  
	  public void setM_Logs(String string)
	  {
		  this.m_Logs = string;
	  }
  
	  public void setM_Muted(boolean bool)
	  {
		  this.m_Muted = bool;
	  }
  
	  public void setM_NbBox(int Int)
	  {
		  this.m_NbBox = Int;
	  }
}
