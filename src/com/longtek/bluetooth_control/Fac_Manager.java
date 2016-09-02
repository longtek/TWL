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
 * �Զ���Fac_Manager�࣬ʵ��ActivityManagerService (AMS)����
 * ���ڹ���Activity���������ڣ�ͬʱ����ϵͳ��Service��broadcast���Լ� provider��
 * */
public class Fac_Manager
{
	private static Fac_Manager m_UniqueInstance;
	public Fac_BroadcastReceiver bReceiver;
	private PC_Data m_Data;
	private Context m_About;
	private Context m_BoxSettings;
	private Context m_CANSettings;
	private Context m_Connect;
	private Context m_Demo;
	private Fac_com m_com;
	private Context m_context;
	private Fac_Handler m_handler;
	private BluetoothAdapter myBluetoothAdapter;
  
	public Fac_Manager(Context context)
	{
		this.m_context = context;
		this.m_Data = new PC_Data();
		this.bReceiver = new Fac_BroadcastReceiver(this);
		this.m_handler = new Fac_Handler(this);
		this.m_com = new Fac_com(this.m_handler);
		this.myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//��鱾�������Ƿ�򿪣�APP��������ʾ����Ӧ�ó������������
//		if (!this.myBluetoothAdapter.isEnabled())
//		{
//			this.myBluetoothAdapter.enable();
//			Log.d("Fac_Manager", "Bluetooth non activ��");
//		}
		//����registerReceiver����ע��㲥��������ʵ��ɨ�������豸�ͷ��ط��ֵ��豸
		this.m_context.registerReceiver(this.bReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"));
		this.m_context.registerReceiver(this.bReceiver, new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED"));
		m_UniqueInstance = this;
	}
  
	//��byte����ת��Ϊ����
	private int ByteToInt(byte b)
  	{
		ByteBuffer bb = ByteBuffer.wrap(new byte[] { b, 0, 0, 0 });
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
  	}
  
	public static Fac_Manager getManager()
	{
		return m_UniqueInstance;
	}
  
	//�����������ݺ͸��������ݣ����ص���̬������
	private ArrayList<String> parseIntsAndFloats(String str)
	{
		ArrayList localArrayList = new ArrayList();
		Matcher matcher = Pattern.compile("[-]?[0-9]*\\.?,?[0-9]+").matcher(str);     //��������ʽ���뵽ģʽ��ƥ������
		for (;;)
		{	//���ҽ����������е�������ƥ���ƥ������ģʽʱ��������������
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
  
	public void BoxDeleted(byte[] ArrayOfByte)
	{
		Dir();
	}
  
	public void BoxFlashed(byte[] ArrayOfByte)
	{
		String str = new String(ArrayOfByte);
		Dir();
		((BoxSettings)this.m_BoxSettings).BoxFlashed(ArrayOfByte.equals("y"));
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
  
	public void BoxReadyToReceiveData(byte[] ArrayOfByte)
	{
		switch (ArrayOfByte[0])
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
  
	public void CcgFlashed(byte[] ArrayOfByte)
	{
		String str = new String(ArrayOfByte);
		((CanSettings)this.m_CANSettings).CcgFlashed(ArrayOfByte.equals("y"));
	}
  
	public void CcgLoaded(byte[] ArrayOfByte)
	{
		String str = new String(ArrayOfByte);
		if (ArrayOfByte.equals("y"))
		{
			this.m_com.flashCcg();
			((CanSettings)this.m_CANSettings).CcgLoaded(ArrayOfByte.equals("y"));
			return;
		}
		((CanSettings)this.m_CANSettings).CcgLoaded(ArrayOfByte.equals("y"));
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
  
	/*public boolean Connect(int Int)
	{
		BluetoothDevice localBluetoothDevice = this.m_Data.getDevice(Int);
		if (this.myBluetoothAdapter.isDiscovering()) {
			this.myBluetoothAdapter.cancelDiscovery();
    }
    for (;;)
    {
    	if (!this.myBluetoothAdapter.isDiscovering())
    	{
    		boolean bool = this.m_com.connect(localBluetoothDevice);
    		if ((this.m_Connect != null) && (!bool)) {
//    			((Connect)this.m_Connect).ConnectionDone(false);
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
  */
  public void ConnectionDone(byte[] ArrayOfByte)
  {
	  String str = new String(ArrayOfByte);
	  if (this.m_Connect != null) {
//		  ((Connect)this.m_Connect).ConnectionDone(ArrayOfByte.equals("y"));
	  }
	  ((MainActivity)this.m_context).ConnectionDone(ArrayOfByte.equals("y"));
	  if (ArrayOfByte.equals("y")) {
		  this.m_com.getSoundCreatorVersion();
	  }
  }
  
  public void Debug(byte[] ArrayOfByte)
  {
	  ByteBuffer bytebuffer = ByteBuffer.wrap(new byte[] { ArrayOfByte[0], ArrayOfByte[1], ArrayOfByte[2], ArrayOfByte[3] });
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
//		  ((Connect)this.m_Connect).HideProgressBar();
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
	  
	  File CcgFile = new File(uri.getPath());
	  this.m_Data.setM_CcgName(uri.getLastPathSegment());
	  byte[] ccg = ReadCcgFile(CcgFile);         	//��������CGG�ļ�����������
	  this.m_Data.setM_CcgBytes(ccg);				//������������е��ļ�תΪ�ֽ���
	  this.m_com.sendCcgCmd();
  }
  
  //�����µ������豸
  public void NewDeviceDetected(BluetoothDevice BtDevice)
  {
	  this.m_Data.addDevice(BtDevice);
	  if (this.m_Connect != null) {
//		  ((Connect)this.m_Connect).NewDeviceDetected(BtDevice);
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
  
  public byte[] ReadCcgFile(File file)
  {
	  Object localObject2 = null;
	  int i = 0;
	  Object localObject1 = null;
	  int m, j = 0, k;
	  
	  try
	  {
		  FileReader fileReder = new FileReader(file);
	//	  localObject1 = fileReader;


        for (;;)
        {
        	Object localObject3 = null;
          String str = ((BufferedReader)localObject3).readLine();
        }

//        byte[] arrayOfByte = ReadCcgV0(paramFile);
      }
      catch (IOException paramFile1)
      {
        paramFile1.printStackTrace();
        return (byte[]) localObject2;
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
  
  //�����豸�ϴ���CAN����
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
//      ((Connect)this.m_Connect).DisconnectionDone(true);
    }
  }
  
  //��ȡBOX�ļ��б�
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
  
  //ȡ����ǰBOX�ļ�
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
