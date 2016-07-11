package com.longtek.bluetooth_control;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Fac_com
{
	private proc_Server m_Thread = null;
	private Fac_Handler m_handler = null;
	private BluetoothSocket m_socket;
	private UUID my_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  
	public Fac_com(Fac_Handler paramFac_Handler)
	{
		this.m_handler = paramFac_Handler;
	}
  
	  public void Dir()
	  {
		  this.m_Thread.write("d".getBytes());
	  }
  
	  public boolean IsConnected()
	  {
		  return (this.m_socket != null) && (this.m_socket.isConnected());
	  }
  
	  
	  public void LoadBox(int paramInt)
	  {
		  ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
		  localByteBuffer.putInt(paramInt);
		  int i = localByteBuffer.array()[3];
		  this.m_Thread.write(new byte[] { (byte) i });
	  }
  
	  public void LoadBoxCmd()
	  {
		  this.m_Thread.write("n".getBytes());
	  }
  
	  public void SpyOff()
	  {
		  this.m_Thread.write("s".getBytes());
	  }
  
	  public void SpyOn()
	  {
		  this.m_Thread.write("f".getBytes());
	  }
  
	  public void VolMoins()
	  {
		  this.m_Thread.write("-".getBytes());
	  }
  
	  public void VolPlus()
	  {
		  this.m_Thread.write("+".getBytes());
	  }
  
	  public boolean connect(BluetoothDevice paramBluetoothDevice)
	  {
		  disconnect();
		  connectclient(paramBluetoothDevice);
		  new Thread(new Runnable()
		  {
			  public void run()
			  {
				  try
				  {
					  Fac_com.this.m_socket.connect();
					  return;
				  }
				  catch (IOException localIOException1)
				  {
					  localIOException1.printStackTrace();
					  try
					  {
						  Fac_com.this.m_socket.close();
						  return;
					  }
					  catch (IOException localIOException2) {}
				  }
			  }
		  }).start();
		  
		  int i = 0;
		  for (;;)
		  {
			  int j;
			  if (!this.m_socket.isConnected())
			  {
				  j = i + 1;
				  if (i < 50) {}
			  }
			  else
			  {
				  if (!this.m_socket.isConnected()) {
					  break;
				  }
				  this.m_Thread = new proc_Server(this.m_socket, this.m_handler);
				  this.m_Thread.start();
				  this.m_Thread.write("a".getBytes());
				  return true;
			  }
			  try
			  {
				  Thread.sleep(100L);
				  i = j;
			  }
			  catch (InterruptedException e)
			  {
				  e.printStackTrace();
				  i = j;
			  }
		  }
		  return false;
	  }
  
 	 public boolean connectclient(BluetoothDevice paramBluetoothDevice)
 	 {
 		 try
 		 {
 			 this.m_socket = paramBluetoothDevice.createRfcommSocketToServiceRecord(this.my_uuid);
 			 return true;
 		 }
 		 catch (IOException e) {}
 		 return false;
 	 }
  
 	 public void delete()
 	 {
 		 this.m_Thread.write("x".getBytes());
  	}
  
 	 public void disconnect()
 	 {
 		 if (this.m_Thread != null) {
 			 this.m_Thread.Stop();
 		 }
 		 new Thread(new Runnable()
 		 {
 			 public void run()
 			 {
        if (Fac_com.this.IsConnected()) {}
        try
        {
        	Fac_com.this.m_socket.close();
        	return;
        }
        catch (IOException localIOException) {}
 		}
 		}).start();
 	 }
  
 	 public void flashBox()
 	 {
 		 this.m_Thread.write("p".getBytes());
 	 }
  
 	 public void flashCcg()
 	 {
 		 this.m_Thread.write("q".getBytes());
 	 }
  
 	 public void getSoundCreatorVersion()
 	 {
 		 this.m_Thread.write("v".getBytes());
 	 }
  
 	 public void mute()
 	 {
 		 this.m_Thread.write("m".getBytes());
 	 }
  
 	 public void sendBoxCmd()
 	 {
 		 this.m_Thread.write("b".getBytes());
 	 }
  
 	 public void sendBoxData(byte[] paramArrayOfByte)
 	 {
 		 this.m_Thread.write(paramArrayOfByte);
 	 }
  
 	 public void sendCcgCmd()
 	 {
 		 this.m_Thread.write("c".getBytes());
 	 }
  
 	 public void sendCcgData(byte[] paramArrayOfByte)
 	 {
 		 this.m_Thread.write(paramArrayOfByte);
 	 }
  
 	 public void unmute()
 	 {
 		 this.m_Thread.write("u".getBytes());
 	 }
}

