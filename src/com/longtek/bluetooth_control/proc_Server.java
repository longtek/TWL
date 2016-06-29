package com.longtek.bluetooth_control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class proc_Server extends Thread {

	private Handler m_Handler;
	private BluetoothSocket m_Socket;
	private InputStream m_InStream;
	private OutputStream m_OutStream;
	private byte[] m_message;
	private int m_MaxBufferSize;
	
	private boolean m_stopThread = false;
	
	public proc_Server(BluetoothSocket BtSocket, Fac_Handler fac_Handler)
	{
		this.m_Socket = BtSocket;
		try
		{
			this.m_InStream = this.m_Socket.getInputStream();
			this.m_OutStream = this.m_Socket.getOutputStream();
 
			this.m_message = new byte[this.m_MaxBufferSize];
			this.m_Handler = fac_Handler;
			return;
		}
		catch (IOException e)
		{
		 
		}
	}
  
	public void Stop()
	{
		this.m_stopThread = true;
	}
	
	public void write(byte[] paramArrayOfByte)
	{
		try
		{
			String str = new String(paramArrayOfByte);
		    byte[] Str = ("LOG trying to send " + paramArrayOfByte.length + " bytes: " + ((String)str).charAt(0)).getBytes();
		    Message msg = new Message();
		    Bundle localBundle = new Bundle();
		    localBundle.putByteArray("data", (byte[])Str);
		    ((Message)msg).setData(localBundle);
		    this.m_Handler.sendMessage((Message)msg);
		    this.m_OutStream.write(paramArrayOfByte);
		    
		    paramArrayOfByte = "LOG success".getBytes();
//		    Message msg = new Message();
//		    Bundle bundle = new Bundle();
//		    ((Bundle)bundle).putByteArray("data", paramArrayOfByte);
//		    ((Message)msg).setData((Bundle)bundle);
//		    this.m_Handler.sendMessage((Message)msg);
		    return;
		}
	    catch (IOException e)
	    {
	    	Log.e("procServer", "2");
	    	paramArrayOfByte = "LOG fail IOException".getBytes();
//	    	msg = new Message();
//	    	bundle = new Bundle();
//	    	((Bundle)localObject2).putByteArray("data", paramArrayOfByte);
//	    	((Message)localObject1).setData((Bundle)localObject2);
//	    	this.m_Handler.sendMessage((Message)localObject1);
	    	return;
	    }
	    catch (Exception ee)
	    {
	    	paramArrayOfByte = "LOG fail other Exception".getBytes();
//	    	Object localObject1 = new Message();
//	    	Object localObject2 = new Bundle();
//	    	((Bundle)localObject2).putByteArray("data", paramArrayOfByte);
//	    	((Message)localObject1).setData((Bundle)localObject2);
//	    	this.m_Handler.sendMessage((Message)localObject1);
	    }
	}
}
