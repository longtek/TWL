package com.longtek.bluetooth_control;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Fac_Handler extends Handler
{
	private Fac_Manager m_manager = null;
  
	public Fac_Handler(Fac_Manager fac_Manager)
	{
		this.m_manager = fac_Manager;
	}
  
	private String getID(byte[] ArrayOfByte)
	{
		String id = new String(ArrayOfByte);
		if (id.length() >= 4) {
			return id.substring(0, 4);
		}
		return "WRONG ID";
	}
  
	//提取信息
	public byte[] extractMessage(byte[] ArrayOfByte)
	{
		byte[] msg;
		if (ArrayOfByte.length < 4)
		{
			msg = null;
			return msg;
		}
		byte[] arrayOfByte = new byte[ArrayOfByte.length - 4];
		int i = 0;
		
		for (;;)
		{
			msg = arrayOfByte;
			if (i >= arrayOfByte.length) {
				break;
			}
			arrayOfByte[i] = ArrayOfByte[(i + 4)];
			i += 1;
		}
		return msg;
	}
	
	public void handleMessage(Message message)
	{
		 byte[] arrayOfByte = message.getData().getByteArray("data");
		 String id = getID(arrayOfByte);
		 arrayOfByte = extractMessage(arrayOfByte);
		 
		 switch (id)      //Java 7如何实现的字符串switch:使用了hashCode()来进行switch，然后通过equals方法进行验证
		 {
		 	case "LOG":
		 		this.m_manager.AddDebugMessage(id);
		 		break;
		 	case "CAN":
		 		this.m_manager.ReceivedCAN(arrayOfByte);
		 		break;
		 	case "CONN":
		 		this.m_manager.ConnectionDone(arrayOfByte);
		 		break;
		 	case "DEBU":
		 		this.m_manager.Debug(arrayOfByte);
		 		break;
		 	case "DEL":
		 		this.m_manager.BoxDeleted(arrayOfByte);
		 		break;	
		 	case "DIR":
		 		this.m_manager.Dir();
		 		break;
		 	case "DISC":
		 		this.m_manager.disconnect();
		 		break;
		 	case "FBOX":
		 		this.m_manager.BoxFlashed(arrayOfByte);
		 		break;
		 	case "FCCG":
		 		this.m_manager.CcgFlashed(arrayOfByte);
		 		break;
		 	case "LBOX":
		 		this.m_manager.extractNBox(arrayOfByte);
		 		break;
		 	case "LOST":
		 		this.m_manager.extractBoxList(arrayOfByte);
		 		break;
		 	case "MUTE":
		 		this.m_manager.BoxMuted(arrayOfByte);
		 		break;
		 	case "RBOX":
		 		this.m_manager.LoadBox(null);
		 		break;
		 	case "RCCG":
		 		this.m_manager.CcgLoaded(arrayOfByte);
		 		break;
		 	case "SCAN":
		 		this.m_manager.ScanDevice();
		 		break;
		 	case "SPY":
		 		this.m_manager.SpyOnOff(arrayOfByte);
		 		break;
		 	case "VERS":
		 		this.m_manager.SetSoundCreatorVersion(arrayOfByte);
		 		break;
		 	case "VOL":
		 		this.m_manager.extractVolume(arrayOfByte);
		 		break;
		 	case "WAIT":
		 		this.m_manager.BoxReadyToReceiveData(arrayOfByte);
		 		break;
		 	case "err>":
		 		this.m_manager.Error(arrayOfByte);
		 		break;
		 	default:
		 		break;
		 }
	}
}
