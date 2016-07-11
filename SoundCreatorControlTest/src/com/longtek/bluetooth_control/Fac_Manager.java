package com.longtek.bluetooth_control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/***
 * 自定义Fac_Manager类，实现ActivityManagerService (AMS)功能
 * 用于管理Activity的生命周期，同时管理系统的Service，broadcast，以及 provider等
 * */
public class Fac_Manager
{
	private static Fac_Manager m_UniqueInstance = null;
	public Fac_BroadcastReceiver bReceiver;
	private PC_Data m_Data = null;
	private Context m_About = null;
	private Context m_BoxSettings = null;
	private Context m_CANSettings = null;
	private Context m_Connect = null;
	private Context m_Demo = null;
	private Fac_com m_com = null;
	private Context m_context = null;
	private Fac_Handler m_handler = null;
	private BluetoothAdapter myBluetoothAdapter;
  
	public Fac_Manager(Context paramContext)
	{
		this.m_context = paramContext;
		this.m_Data = new PC_Data();
		this.bReceiver = new Fac_BroadcastReceiver(this);
		this.m_handler = new Fac_Handler(this);
		this.m_com = new Fac_com(this.m_handler);
		this.myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//检查本地蓝牙是否打开
		if (!this.myBluetoothAdapter.isEnabled())
		{
			this.myBluetoothAdapter.enable();
			Log.d("Fac_Manager", "Bluetooth non activé");
		}
		//调用registerReceiver函数注册广播接收器，实现扫描蓝牙设备和返回发现的设备
		this.m_context.registerReceiver(this.bReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"));
		this.m_context.registerReceiver(this.bReceiver, new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED"));
		m_UniqueInstance = this;
	}
  
	private int ByteToInt(byte paramByte)
  	{
		ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[] { paramByte, 0, 0, 0 });
		localByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		return localByteBuffer.getInt();
  	}
  
	public static Fac_Manager getManager()
	{
		return m_UniqueInstance;
	}
  
	//解析整型数据和浮点型数据，返回到数组类表中
	private ArrayList<String> parseIntsAndFloats(String paramString)
	{
		ArrayList localArrayList = new ArrayList();
		Matcher matcher = Pattern.compile("[-]?[0-9]*\\.?,?[0-9]+").matcher(paramString);     //将正则表达式放入到模式的匹配器中
		for (;;)
		{	//当且仅当输入序列的子序列匹配此匹配器的模式时，返回数组序列
			if (!matcher.find()) {
				return localArrayList;
			}
			localArrayList.add(matcher.group());
		}
	}
  
	public void AddDebugMessage(String string)
	{
		string = this.m_Data.getM_Logs() + "\n" + string;
		this.m_Data.setM_Logs(string);
	}
  
	public void BoxDeleted(byte[] paramArrayOfByte)
	{
		Dir();
	}
  
	public void BoxFlashed(byte[] paramArrayOfByte)
	{
		String str = new String(paramArrayOfByte);
		Dir();
		((BoxSettings)this.m_BoxSettings).BoxFlashed(paramArrayOfByte.equals("y"));
	}
  
	public void BoxLoaded(String ArrayOfByte)
	{
		ArrayOfByte = new String(ArrayOfByte);
		if (ArrayOfByte.equals("y"))
		{
			((BoxSettings)this.m_BoxSettings).BoxLoaded(ArrayOfByte.equals("y"));
			this.m_com.flashBox();
			return;
		}
		((BoxSettings)this.m_BoxSettings).BoxLoaded(ArrayOfByte.equals("y"));
	}
  
	public void BoxMuted(byte[] paramArrayOfByte)
	{
		boolean bool = new String(paramArrayOfByte).equals("y");
		((MainActivity)this.m_context).BoxMuted(bool);
		this.m_Data.setM_Muted(bool);
	}
  
	public void BoxReadyToReceiveData(byte[] paramArrayOfByte)
	{
		switch (paramArrayOfByte[0])
		{
		case 1: 
			this.m_com.sendBoxData(this.m_Data.getM_BoxBytes());
			break;
		case 2: 
			this.m_com.sendCcgData(this.m_Data.getM_CcgBytes());
			break;
		default: 
			this.m_com.LoadBox(this.m_Data.getM_BoxCurrent());
			break;
		}
  	}
  
	public void CcgFlashed(byte[] paramArrayOfByte)
	{
		String str = new String(paramArrayOfByte);
		((CanSettings)this.m_CANSettings).CcgFlashed(paramArrayOfByte.equals("y"));
	}
  
	public void CcgLoaded(byte[] paramArrayOfByte)
	{
		String str = new String(paramArrayOfByte);
		if (paramArrayOfByte.equals("y"))
		{
			this.m_com.flashCcg();
			((CanSettings)this.m_CANSettings).CcgLoaded(paramArrayOfByte.equals("y"));
			return;
		}
		((CanSettings)this.m_CANSettings).CcgLoaded(paramArrayOfByte.equals("y"));
	}
  
	public void ChooseBox(int paramInt)
	{
		if (!IsConnected())
		{
			((MainActivity)this.m_context).PleaseDoConnection();
			return;
		}
		this.m_Data.setM_BoxCurrent(paramInt);
    	this.m_com.LoadBoxCmd();
	}
  
	public void ClearDevices()
	{
		this.m_Data.clearDevices();
	}
  
	public boolean Connect(int paramInt)
	{
		BluetoothDevice localBluetoothDevice = this.m_Data.getDevice(paramInt);
		if (this.myBluetoothAdapter.isDiscovering()) {
			this.myBluetoothAdapter.cancelDiscovery();
    }
    for (;;)
    {
    	if (!this.myBluetoothAdapter.isDiscovering())
    	{
    		boolean bool = this.m_com.connect(localBluetoothDevice);
    		if ((this.m_Connect != null) && (!bool)) {
    			((Connect)this.m_Connect).ConnectionDone(false);
    		}
    		return bool;
    	}
    	try
    	{
    		Thread.sleep(100L);
    	}
    	catch (InterruptedException localInterruptedException)
    	{
    		localInterruptedException.printStackTrace();
    	}
    }
  }
  
  public void ConnectionDone(byte[] paramArrayOfByte)
  {
	  String str = new String(paramArrayOfByte);
	  if (this.m_Connect != null) {
		  ((Connect)this.m_Connect).ConnectionDone(paramArrayOfByte.equals("y"));
	  }
	  ((MainActivity)this.m_context).ConnectionDone(paramArrayOfByte.equals("y"));
	  if (paramArrayOfByte.equals("y")) {
		  this.m_com.getSoundCreatorVersion();
	  }
  }
  
  public void Debug(byte[] paramArrayOfByte)
  {
	  ByteBuffer bytebuffer = ByteBuffer.wrap(new byte[] { paramArrayOfByte[0], paramArrayOfByte[1], paramArrayOfByte[2], paramArrayOfByte[3] });
	  bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
	  ((MainActivity)this.m_context).Debug(bytebuffer.getInt());
  }
  
  public void Delete()
  {
	  if (!IsConnected())
	  {
		  ((MainActivity)this.m_context).PleaseDoConnection();
		  return;
	  }
	  this.m_com.delete();
  }
  
  public void Dir()
  {
	  if (!IsConnected())
	  {
		  ((MainActivity)this.m_context).PleaseDoConnection();
		  return;
	  }
	  this.m_com.Dir();
  }
  
  public void DiscoveryFinished()
  {
	  if (this.m_Connect != null) {
		  ((Connect)this.m_Connect).HideProgressBar();
	  }
  }
  
  public void Error(byte[] ArrayOfByte)
  {
	  ((MainActivity)this.m_context).Error();
  }
  
  public void GetSoundCreatorVersion()
  {
	  if (IsConnected()) {
		  this.m_com.getSoundCreatorVersion();
	  }
  }
  
  public boolean IsConnected()
  {
	  return this.m_com.IsConnected();
  }
  
  public void LoadBox(Uri uri)
  {
	  if (!IsConnected())
	  {
		  ((MainActivity)this.m_context).PleaseDoConnection();
		  return;
	  }
	  File file= new File(uri.getPath());
	  int j = this.m_Data.getM_NBytesHeader();
	  int k = this.m_Data.getM_NBytesParam();
	  byte[] box = new byte[k + j + this.m_Data.getM_NBytesNom()];
   
	  for (;;)
	  {
		  try
		  {
			  FileInputStream fis = new FileInputStream((File)file);
		  }
		  catch (FileNotFoundException localFileNotFoundException)
		  {
			  Object localObject2;
			  int i;
			  localFileNotFoundException.printStackTrace();
			  continue;
	//		  box[(k + j + i)] = localFileNotFoundException[i];
	//		  i += 1;
	//		  continue;
		  }
		  try
		  {
			  FileInputStream fis = null;
			  fis.read(box, j, k);
//			  fis.read(buffer, byteOffset, byteCount);
//			((FileInputStream)fis).read(uri, k, j);
//			  ((FileInputStream)fis).read(uri, 0, k);
			  String name = file.getName();
			  String str1 =  name.substring(0, Math.min((name.length() - 4), 20));
			  byte [] str = name.getBytes(Charset.forName("Latin-1"));
			  int i = 0;
			  if (i >= ((String)str1).length())
			  {
				  this.m_Data.setM_BoxBytes(str);
				  this.m_com.sendBoxCmd();
				  return;
			  }
		  }
		  catch (IOException localIOException)
		  {
			  localIOException.printStackTrace();
		  }
	  }
  }
  
  public void LoadCcg(Uri uri)
  {
	  if (!IsConnected())
	  {
		  ((MainActivity)this.m_context).PleaseDoConnection();
		  return;
	  }
	  
	  File localFile = new File(uri.getPath());
	  this.m_Data.setM_CcgName(uri.getLastPathSegment());
	  byte[] ccg = ReadCcgFile(localFile);         	//将读到的CGG文件存在数组中
	  this.m_Data.setM_CcgBytes(ccg);				//将存放在数组中的文件转为字节码
	  this.m_com.sendCcgCmd();
  }
  
  //发现新的蓝牙设备
  public void NewDeviceDetected(BluetoothDevice BtDevice)
  {
	  this.m_Data.addDevice(BtDevice);
	  if (this.m_Connect != null) {
		  ((Connect)this.m_Connect).NewDeviceDetected(BtDevice);
	  }
  }
  
  public void Next()
  {
	  if (!IsConnected())
	  {
		  ((MainActivity)this.m_context).PleaseDoConnection();
		  return;
	  }
	  this.m_Data.BoxCurrentIncrease();
	  this.m_com.LoadBoxCmd();
  }
  
  public void Previous()
  {
	  if (!IsConnected())
	  {
		  ((MainActivity)this.m_context).PleaseDoConnection();
		  return;
	  }
	  this.m_Data.BoxCurrentDecrease();
	  this.m_com.LoadBoxCmd();
  }
  
  public byte[] ReadCcgFile(File paramFile)
  {
	  Object localObject2 = null;
	  int i = 0;
	  Object localObject1 = null;
	  int m, j = 0, k;
	  
	  try
	  {
		  FileReader fileReder = new FileReader(paramFile);
	//	  localObject1 = fileReader;
	  }
    catch (FileNotFoundException e)
    {
      try
      {
        for (;;)
        {
        	Object localObject3 = null;
          String str = ((BufferedReader)localObject3).readLine();
          if (str == null)
          {
            return (byte[]) localObject2;
 //           localFileNotFoundException = localFileNotFoundException;
     //       e.printStackTrace();
          }
          else
          {
            localObject1 = localObject2;
            if (str != null)
            {
              localObject1 = localObject2;
              if (str.length() > 0)
              {
                str = str.trim();
                localObject1 = localObject2;
                if (str != null)
                {
                  localObject1 = localObject2;
                  if (str.length() > 0)
                  {
                    localObject1 = localObject2;
                    if (!str.substring(0, 1).equals("*"))
                    {
                      localObject1 = localObject2;
                      if (!str.substring(0, 1).equals("\n"))
                      {
                        localObject1 = localObject2;
                        if (!str.substring(0, 1).equals(""))
                        {
                          m = j + 1;
                          j = m;
                          localObject1 = localObject2;
                          if (m == 2)
                          {
                            localObject1 = new Scanner(str);
                            k = i;
                            if (str.getBytes()[0] == "V".getBytes()[0])
                            {
                              k = i;
                              if (str.getBytes()[1] == "_".getBytes()[0]) {
                                k = Integer.parseInt(str.replaceAll("[^0-9]", ""));
                              }
                            }
                            ((Scanner)localObject1).close();
                            try
                            {
                              ((Closeable) e).close();
                              switch (k)
                              {
                              default: 
                                localObject1 = null;
                                j = m;
                                i = k;
                              }
                            }
                            catch (IOException localIOException)
                            {
                              for (;;)
                              {
                                localIOException.printStackTrace();
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
//        byte[] arrayOfByte = ReadCcgV0(paramFile);
      }
      catch (IOException paramFile1)
      {
        paramFile1.printStackTrace();
        return (byte[]) localObject2;
      }
    }
    BufferedReader localObject3 = new BufferedReader((Reader)localObject1);
    
    localObject1 = localObject2;
    for (;;)
    {
      localObject2 = localObject1;
      String str;
  
    //  continue;
      byte[] arrayOfByte = ReadCcgV1(paramFile);
 
    }
  }
  
  /* Error */
  public byte[] ReadCcgV0(File paramFile)
  {
	  return null;
  }
  
  /* Error */
  public byte[] ReadCcgV1(File paramFile)
  {
	  return null;
  }
  
  //接收设备上传的CAN数据
  public void ReceivedCAN(byte[] ArrayOfByte)
  {
	  int i = ByteToInt(ArrayOfByte[2]);
	  int j = ByteToInt(ArrayOfByte[3]) / 2;
	  int k = ByteToInt(ArrayOfByte[4]);
	  
	  if (this.m_CANSettings != null) {
		  ((CanSettings)this.m_CANSettings).UpdateRPMSpeedThrottle(i * 32, k, j);
	  }
  }
  
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
  
  public void SetSoundCreatorVersion(byte[] ArrayOfByte)
  {
	  String str = new String(ArrayOfByte);
	  this.m_Data.setM_SoundCreatorVersion(str);
	  VolPlus();
  }
  
  public void SpyOff()
  {
	  if (IsConnected()) {
		  this.m_com.SpyOff();
	  }
  }
  
  public void SpyOn()
  {
    if (IsConnected()) {
      this.m_com.SpyOn();
    }
  }
  
  public void SpyOnOff(byte[] paramArrayOfByte)
  {
    String  str = new String(paramArrayOfByte);
    if (this.m_CANSettings != null) {
      ((CanSettings)this.m_CANSettings).SpyOnOff(paramArrayOfByte.equals("y"));
    }
  }
  
  public void SwitchVolumeMuted()
  {
    if (!IsConnected())
    {
      ((MainActivity)this.m_context).PleaseDoConnection();
      return;
    }
    if (this.m_Data.isM_Muted())
    {
      this.m_com.unmute();
      return;
    }
    this.m_com.mute();
  }
  
  public void VolMoins()
  {
    if (!IsConnected())
    {
      ((MainActivity)this.m_context).PleaseDoConnection();
      return;
    }
    if (this.m_Data.isM_Muted())
    {
      this.m_com.unmute();
      return;
    }
    this.m_com.VolMoins();
  }
  
  public void VolPlus()
  {
    if (!IsConnected())
    {
      ((MainActivity)this.m_context).PleaseDoConnection();
      return;
    }
    if (this.m_Data.isM_Muted())
    {
      this.m_com.unmute();
      return;
    }
    this.m_com.VolPlus();
  }
  
  public void WrongMessage(String string)
  {
	  ((MainActivity)this.m_context).WrongMessage(string);
  }
  
  public void deleteAbout()
  {
    this.m_About = null;
  }
  
  public void deleteBoxSettings()
  {
    this.m_BoxSettings = null;
  }
  
  public void deleteCANSettings()
  {
    this.m_CANSettings = null;
  }
  
  public void deleteConnect()
  {
    this.m_Connect = null;
  }
  
  public void deleteDemo()
  {
    this.m_Demo = null;
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
  
  public void extractBoxList(byte[] ArrayOfByte)
  {
	  int j = ArrayOfByte[0];
	  int k = ArrayOfByte[1];
	  String[] arrayOfString = new String[j];
	  String str = new String(ArrayOfByte, Charset.forName("Latin-1"));
	  int i = 0;
    
	  for (;;)
	  {
		  if (i >= j)
		  {
			  new String(ArrayOfByte);
			  this.m_Data.setM_ListBox(arrayOfString);
			  this.m_Data.setM_NbBox(j);
			  ((MainActivity)this.m_context).BoxListAvailable(arrayOfString);
			  if (this.m_BoxSettings != null) {
				  ((BoxSettings)this.m_BoxSettings).BoxListAvailable(arrayOfString);
			  }
			  return;
      }
      arrayOfString[ArrayOfByte[((k + 1) * i + 2)]] = str.substring((k + 1) * i + 2 + 1, (i + 1) * (k + 1) + 2);
      i += 1;
	  }
  }
  
  public void extractNBox(byte[] ArrayOfByte)
  {
	  int i = this.m_Data.getM_BoxCurrent();
	  if (i != ArrayOfByte[0]) {
		  ChooseBox(i);
	  }
	  do
	  { 
		  this.m_Data.setM_BoxCurrent(ArrayOfByte[0]);
		  ((MainActivity)this.m_context).updateNBox(ArrayOfByte[0]);
		  if (this.m_Demo != null) {
			  ((Demo)this.m_Demo).updateNBox(ArrayOfByte[0]);
		  }
	  } while (this.m_BoxSettings == null);
	  ((BoxSettings)this.m_BoxSettings).updateNBox(ArrayOfByte[0]);
  }
  
  public void extractVolume(byte[] ArrayOfByte)
  {
	  ByteBuffer bytebuffer = ByteBuffer.wrap(new byte[] {ArrayOfByte[0], ArrayOfByte[1], ArrayOfByte[2],   ArrayOfByte[3] });
	  bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
	  int i = bytebuffer.getInt();
	  ((MainActivity)this.m_context).updateVolume(i);
	  if (this.m_Data.getM_ListBox() == null) {
		  Dir();
	  }
  }
  
  public CharSequence getCcgFileName()
  {
	  return this.m_Data.getM_CcgName();
  }
  
  public PC_Data getM_Data()
  {
	  return this.m_Data;
  }
  
  public Context getM_About()
  {
	  return this.m_About;
  }
  
  public Context getM_BoxSettings()
  {
	  return this.m_BoxSettings;
  }
  
  public Context getM_CANSettings()
  {
	  return this.m_CANSettings;
  }
  
  public Context getM_Connect()
  {
	  return this.m_Connect;
  }
  
  public Context getM_Demo()
  {
	  return this.m_Demo;
  }
  
  public Fac_com getM_com()
  {
	  return this.m_com;
  }
  
  public Context getM_context()
  {
	  return this.m_context;
  }
  
  public void setM_Data(PC_Data pc_Data)
  {
	  this.m_Data = pc_Data;
  }
  
  public void setM_About(Context context)
  {
	  this.m_About = context;
  }
  
  public void setM_BoxSettings(Context context)
  {
	  this.m_BoxSettings = context;
  }

  public void setM_CANSettings(Context context)
  {
	  this.m_CANSettings = context;
  }
  
  public void setM_Connect(Context context)
  {
	  this.m_Connect = context;
  }
  
  public void setM_Demo(Context context)
  {
	  this.m_Demo = context;
  }
  
  public void setM_com(Fac_com fac_com)
  {
	  this.m_com = fac_com;
  }
  
  public void setM_context(Context context)
  {
	  this.m_context = context;
  }
}
