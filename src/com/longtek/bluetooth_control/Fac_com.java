package com.longtek.bluetooth_control;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class Fac_com {
	
	private BluetoothSocket m_socket;
	private proc_Server m_Thread;
	private Fac_Handler m_handler;
//	private UUID my_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private UUID my_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	public Fac_com(Fac_Handler fac_Handler)
	{
		this.m_handler = fac_Handler;
	}
	
	public boolean IsConnected()
	{
		return (this.m_socket != null) && (this.m_socket.isConnected());
		
	}

	public boolean connect(BluetoothDevice device)
	{
		disconnect();
		connectclient(device);
		  
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					Fac_com.this.m_socket.connect();
					return;
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
					try
					{
						Fac_com.this.m_socket.close();
						return;
					}
					catch (IOException e2) {}
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
				catch (IOException e) {}
			}
		}).start();
	}
	
	public boolean connectclient(BluetoothDevice device)
	{
		try
		{
			this.m_socket = device.createRfcommSocketToServiceRecord(this.my_uuid);
			return true;
		}
		catch (IOException e) {}
		return false;
	}
	
	public void getSoundCreatorVersion()
	{
		this.m_Thread.write("v".getBytes());
	}
	
}
