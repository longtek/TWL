package com.longtek.bluetooth_control;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import com.longtek.bluetoothUtil.BluetoothTools;
import com.longtek.bluetoothUtil.TransmitBean;

/*
 * 自定义pro_Server类，继承父类Thread
 * */
public class proc_Server extends Thread
{
	private Handler m_Handler = null;
	private int m_IdxEndMessage = 0;
	private int m_IdxInMessage = 0;
	private InputStream m_InStream = null;
	private int m_MaxBufferSize = 1000;
	private OutputStream m_OutStream = null;
	private BluetoothSocket m_Socket;
	private byte[] m_message;
	private boolean m_stopThread = false;
  
	public proc_Server(BluetoothSocket BtSocket, Fac_Handler fac_Handler)
	{
		this.m_Socket = BtSocket;
	 
			try {
				this.m_InStream = this.m_Socket.getInputStream();
				this.m_OutStream = this.m_Socket.getOutputStream();
 
					this.m_message = new byte[this.m_MaxBufferSize];
				this.m_Handler = fac_Handler;
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		 
	}
  
	public void Stop()
	{
		this.m_stopThread = true;
	}
  
	//
	public void copyToMessage(byte[] ArrayOfByte, int paramInt)
	{
		int i = 0;
		for (;;)
		{
			if (i >= paramInt)
			{
				this.m_IdxInMessage += paramInt % this.m_MaxBufferSize;
				return;
			}
			this.m_message[((this.m_IdxInMessage + i) % this.m_MaxBufferSize)] = ArrayOfByte[i];
			i += 1;
		}
	}
	/*提取id和信息*/
	public byte[] extractIDAndMessage()
	{
		byte[] arrayOfByte = new byte[this.m_IdxEndMessage];
		int i = 0;
		if (i >= this.m_IdxEndMessage)
		{
				if (i < this.m_message.length - this.m_IdxEndMessage - 4) {
					 
				}
				i = this.m_message.length - this.m_IdxEndMessage - 4;
		}
		for (;;)
		{
			/*if (i >= this.m_IdxEndMessage + 4)
			{
				this.m_IdxInMessage -= this.m_IdxEndMessage + 4;
				return arrayOfByte;
				arrayOfByte[i] = this.m_message[i];
				i += 1;
				break;
				label91:
					this.m_message[i] = this.m_message[(this.m_IdxEndMessage + i + 4)];
				i += 1;
				break label19;
			}
			this.m_message[i] = 0;
			i += 1;*/
			
			if(i >= this.m_IdxEndMessage + 4)
			{
				this.m_IdxEndMessage -= this.m_IdxEndMessage + 4;
				arrayOfByte[i] = this.m_message[i];
				i += 1;
				
				this.m_message[i] = this.m_message[(this.m_IdxEndMessage + i + 4)];
				i += 1;
			}
			this.m_message[i] = 0;
			i += 1;
		}
	}
	
	@Override
	public void run()
	{
		String str = new String(new byte[] { -1, -1, -1, -1 });
		for (;;)
		{
			if (this.m_stopThread) {
				return;
			}
			try
			{
				byte[] buffer = new byte[this.m_MaxBufferSize];
				copyToMessage((byte[])buffer, this.m_InStream.read((byte[])buffer));
				for (this.m_IdxEndMessage = new String(this.m_message).indexOf(str); this.m_IdxEndMessage >= 0; this.m_IdxEndMessage = new String(this.m_message).indexOf(str))
				{
					Message msg = new Message();
					Bundle bundle = new Bundle();
					((Bundle)bundle).putByteArray("data", extractIDAndMessage());
					msg.setData((Bundle)bundle);
					this.m_Handler.sendMessage(msg);
				}
			}
			catch (IOException localIOException)
			{
				byte[] arrayOfByte1 = "DISCy".getBytes();
				Object localObject1 = new Message();
				Bundle bundle = new Bundle();
				((Bundle)bundle).putByteArray("data", arrayOfByte1);
				((Message)localObject1).setData((Bundle)bundle);
				this.m_Handler.sendMessage((Message)localObject1);
				return;
			}
			catch (Exception localException)
			{
				this.m_IdxInMessage = 0;
				this.m_IdxEndMessage = -1;
				this.m_message = new byte[this.m_MaxBufferSize];
				byte[] arrayOfByte2 = "LOSTy".getBytes();
				Object localObject2 = new Message();
				Bundle localBundle = new Bundle();
				localBundle.putByteArray("data", arrayOfByte2);
				((Message)localObject2).setData(localBundle);
				this.m_Handler.sendMessage((Message)localObject2);
			}
		}
	}
  
	/**
	 * 写入流
	 * @param obj 写入数据对象
	 */
	/*public void write(Object obj) {
		try {
			TransmitBean transmit_s = (TransmitBean) obj;
			if(transmit_s.getFilename()!=null&&!"".equals(transmit_s.getFilename())){	
				Log.v("调试" , "type:"+2);
				String filename=transmit_s.getFilename();
				byte type = 2; //类型为2，即传文件 
				//读取文件长度
				FileInputStream fins=new FileInputStream(transmit_s.getFilepath());   
				long fileDataLen = fins.available(); //文件的总长度			
				int f_len=filename.getBytes("GBK").length; //文件名长度
				
				byte[] data=new byte[f_len];
				data=filename.getBytes("GBK");
				long totalLen = 4+1+1+f_len+fileDataLen;//数据的总长度
				outStream.writeLong(totalLen); //1.写入数据的总长度
				outStream.writeByte(type);//2.写入类型
				outStream.writeByte(f_len); //3.写入文件名的长度
				outStream.write(data);    //4.写入文件名的数据
				outStream.flush();								
				//读取文件并发送
				try { 	
					byte[] buffer=new byte[1024*64]; 
					downbl=0;
					int size=0;
					long sendlen=0;
					float tspeed=0;
					int i=0;
					long time1=Calendar.getInstance().getTimeInMillis();
					while((size=fins.read(buffer, 0, 1024*64))!=-1)  
					{  						
						outStream.write(buffer, 0, size);
						outStream.flush();
						sendlen+=size;
						Log.v("调试" , "fileDataLen:"+fileDataLen);
						i++;
						if(i%5==0){
							long time2=Calendar.getInstance().getTimeInMillis();
							tspeed=sendlen/(time2-time1)*1000/1024;	
						}
					//	Log.v("调试" ,"tspeed："+tspeed);
						downbl = ((sendlen * 100) / fileDataLen);
						TransmitBean up = new TransmitBean();
						up.setUppercent(String.valueOf(downbl));	
						up.setTspeed(String.valueOf(tspeed));
						if(i==1){
							up.setShowflag(true);
						}else{
							up.setShowflag(false);
						}
						Message msg = serviceHandler.obtainMessage();											
						msg.what = BluetoothTools.FILE_SEND_PERCENT;					
						msg.obj = up;
						msg.sendToTarget();		
					}    
					fins.close();    	
					Log.v("调试" , "文件发送完成");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}else{
				Log.v("调试" , "type:"+1);
				byte type = 1; //类型为1，即传文本消息
				String msg=transmit_s.getMsg();
				int f_len=msg.getBytes("GBK").length; //消息长度
				long totalLen = 4+1+1+f_len;//数据的总长度
				byte[] data=new byte[f_len];
				data=msg.getBytes("GBK");			
				outStream.writeLong(totalLen); //1.写入数据的总长度
				outStream.writeByte(type);//2.写入类型
				outStream.writeByte(f_len); //3.写入消息的长度
				outStream.write(data);    //4.写入消息数据
				outStream.flush();
			}
			
			this.read();
			
			byte[] ef = new byte[3];
			inStream.read(ef);//读取消息
			String eof = new String(ef);
			if("EOF".equals(eof)){
				Log.v("调试" ,"接收EOF");
			}
		}catch (Exception ex) {
			Log.v("调试" , "通讯中断Exception:");
			//发送通讯失败消息
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			ex.printStackTrace();
		}
		finally {
			close();
		}
	}
	*/
	
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
	/*private final BluetoothSocket mmSocket;  
    private final InputStream mmInStream;  
    private final OutputStream mmOutStream;  

    public void ConnectedThread(BluetoothSocket socket) {  
        Log.d(TAG, "create ConnectedThread");  
        mmSocket = socket;  
        InputStream tmpIn = null;  
        OutputStream tmpOut = null;  

        // Get the BluetoothSocket input and output streams  
        try {  
            tmpIn = socket.getInputStream();  
            tmpOut = socket.getOutputStream();  
        } catch (IOException e) {  
            Log.e(TAG, "temp sockets not created", e);  
        }  

        mmInStream = tmpIn;  
        mmOutStream = tmpOut;  
    }  

    public void run() {  
        Log.i(TAG, "BEGIN mConnectedThread");  
        byte[] buffer = new byte[1024];  
        int bytes;  
          
        // Keep listening to the InputStream while connected  
        receiveBuffer = "";  
        while (true) {  
            try {     
             while(true){                          
                 // Read from the InputStream                                                              
                 bytes = mmInStream.read(buffer);  
                 //Log.i(TAG, "收到串口数据: " + (new String(buffer,0,bytes)) + "(" + String.valueOf(bytes) + ")");  
                   
                 String tempString = new String(buffer,0,bytes);  
                 receiveBuffer += tempString;  
                   
                 if (receiveBuffer.endsWith("\r\n")){  
                     String strArray[] = receiveBuffer.split("\r\n");  
                     for(String stemp:strArray){                                   
                         //发送显示  
                         mHandler.obtainMessage(BluetoothSet.MESSAGE_READ, stemp.length(), -1, stemp.getBytes())  
                             .sendToTarget();                              
                         sleep(10L);  
                     }  
                     //初始化接收缓存数据  
                     receiveBuffer = "";  
                 }  
                           
                 if (mmInStream.available() == 0) break;              
             }                     
            } catch (IOException e) {  
                Log.e(TAG, "disconnected", e);  
                connectionLost();  
                break;  
            } catch (InterruptedException e) {  
             Log.e(TAG, "disconnected", e);  
                connectionLost();  
                break;  
         }  
        }  
    }  
      
    *//** 
     * Write to the connected OutStream. 
     * @param buffer  The bytes to write 
     *//*  
    public void write(byte[] buffer) {  
     try {                                     
         mmOutStream.write(buffer);  
           
         // Share the sent message back to the UI Activity  
         mHandler.obtainMessage(BluetoothSet.MESSAGE_WRITE, -1, -1, buffer)  
                 .sendToTarget();                  
           
     }catch (IOException e) {  
             Log.e(TAG, "Exception during write", e);  
         }  
    }  

    public void cancel() {  
        try {  
            mmSocket.close();  
        } catch (IOException e) {  
            Log.e(TAG, "close() of connect socket failed", e);  
        }  
    }  
}

*//** 
 * This thread runs during a connection with a remote device. 
 * It handles all incoming and outgoing transmissions. 
 *//*  
private class ConnectedThread extends Thread {  
    private final BluetoothSocket mmSocket;  
    private final InputStream mmInStream;  
    private final OutputStream mmOutStream;  

    public ConnectedThread(BluetoothSocket socket) {  
        Log.d(TAG, "create ConnectedThread");  
        mmSocket = socket;  
        InputStream tmpIn = null;  
        OutputStream tmpOut = null;  

        // Get the BluetoothSocket input and output streams  
        try {  
            tmpIn = socket.getInputStream();  
            tmpOut = socket.getOutputStream();  
        } catch (IOException e) {  
            Log.e(TAG, "temp sockets not created", e);  
        }  

        mmInStream = tmpIn;  
        mmOutStream = tmpOut;  
    }  

    public void run() {  
        Log.i(TAG, "BEGIN mConnectedThread");  
        byte[] buffer = new byte[1024];  
        int bytes;  
          
        // Keep listening to the InputStream while connected  
        receiveBuffer = "";  
        while (true) {  
            try {     
             while(true){                          
                 // Read from the InputStream                                                              
                 bytes = mmInStream.read(buffer);  
                 //Log.i(TAG, "收到串口数据: " + (new String(buffer,0,bytes)) + "(" + String.valueOf(bytes) + ")");  
                   
                 String tempString = new String(buffer,0,bytes);  
                 receiveBuffer += tempString;  
                   
                 if (receiveBuffer.endsWith("\r\n")){  
                     String strArray[] = receiveBuffer.split("\r\n");  
                     for(String stemp:strArray){                                   
                         //发送显示  
                         mHandler.obtainMessage(BluetoothSet.MESSAGE_READ, stemp.length(), -1, stemp.getBytes())  
                             .sendToTarget();                              
                         sleep(10L);  
                     }  
                     //初始化接收缓存数据  
                     receiveBuffer = "";  
                 }  
                           
                 if (mmInStream.available() == 0) break;              
             }                     
            } catch (IOException e) {  
                Log.e(TAG, "disconnected", e);  
                connectionLost();  
                break;  
            } catch (InterruptedException e) {  
             Log.e(TAG, "disconnected", e);  
                connectionLost();  
                break;  
         }  
        }  
    }  
      
    *//** 
     * Write to the connected OutStream. 
     * @param buffer  The bytes to write 
     *//*  
    public void write(byte[] buffer) {  
     try {                                     
         mmOutStream.write(buffer);  
           
         // Share the sent message back to the UI Activity  
         mHandler.obtainMessage(BluetoothSet.MESSAGE_WRITE, -1, -1, buffer)  
                 .sendToTarget();                  
           
     }catch (IOException e) {  
             Log.e(TAG, "Exception during write", e);  
         }  
    }  

    public void cancel() {  
        try {  
            mmSocket.close();  
        } catch (IOException e) {  
            Log.e(TAG, "close() of connect socket failed", e);  
        }  
    }  
}  
*/