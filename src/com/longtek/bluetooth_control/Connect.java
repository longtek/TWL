package com.longtek.bluetooth_control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.UUID;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.Menu;            //��ʹ�ò˵����������
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Connect��
 * �������ñ���������ʵ���������ӣ�����DeviceListActivity�෵�صĽ��
 * ֮����뷢�����ݺͽ������ݣ���ʾLog����
 * @author TWL
 *
 */
public class Connect extends Activity {
	
	private TextView TextConnectedTo;
	private final static int REQUEST_CONNECT_DEVICE = 1;    //������
	
	public final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP����UUID��
	protected static final int FILE_CHOOSE_CODE = 0;
	public BluetoothDevice _device;
	public BluetoothSocket btSocket;      //����ͨ��socket
	public BluetoothSocket getBtSocket() {
		return btSocket;
	}

	public void setBtSocket(BluetoothSocket btSocket) {
		this.btSocket = btSocket;
	}
	private Uri uri;
	private String path;
	private String CcgFileName;
	private String BoxFileName;
	private byte[] CcgBuffer;
	private byte[] BoxBuffer;
	private String lineStr;
	private InputStream is; 	   //������������������������
	//private TextView text0; 	   //��ʾ������
    private EditText edit0;  	  //��������������
    private TextView dis;     	  //����������ʾ���
    private ScrollView sv;     	 //��ҳ���
    private String smsg = ""; 	 //��ʾ�����ݻ���
    private String fmsg = ""; 	 //���������ݻ���
    private Button btn_ScanDevices;	
    private ToggleButton m_SpyOnOff;		//������ʾ����
    private TextView tv_CanRPM; 		//���ͻ�ת��
    private TextView tv_CanSpeed;    	//����
    private TextView tv_CanThrottle;	//���ſ���
    private TextView tv_SettingsFiles;   //�ļ���ʾ�ı�
    private TextView tv_ShowFile;
    private Button btn_SettingsFiles;     //�ļ����ð�ť
    private Fac_Manager m_Manager;
	private PC_Data m_Data;
	
    public String rmp;
    public String speed;
    public String throttle;
    public String filename=""; //��������洢���ļ���
    boolean _discoveryFinished = false;    
    boolean bRun = true;
    boolean bThread = false;
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //��ȡ�����������������������豸
	
    private View.OnClickListener OnScanDevices = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			//��δ�����豸���DeviceListActivity�����豸����
	    	Button btn = (Button) findViewById(R.id.btn_scandevices);
	    	if(btSocket==null){
	    		Intent serverIntent = new Intent(Connect.this, DeviceListActivity.class); //��ת��������
	    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //���÷��غ궨��
	    	}
	    	else{
	    		 //�ر�����socket
	    	    try{
	    	    	
	    	    	is.close();
	    	    	btSocket.close();
	    	    	btSocket = null;
	    	    	bRun = false;
	    	    	btn.setText("����");
	    	    }catch(IOException e){}   
	    	}
	    	return;
		}
	};
	
	private View.OnClickListener OnFilesSettings = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//���������ֻ��洢��ͼ
			Intent intent = new Intent("android.intent.action.GET_CONTENT");
			intent.setType("*/*");   
			intent.addCategory("android.intent.category.OPENABLE");
			Connect.this.startActivityForResult(Intent.createChooser(intent, "choose a file"), FILE_CHOOSE_CODE);
		}
	};
	
	//����������ʾ���ص����Ӧ�¼�������
	  private View.OnClickListener OnSpyOnOff = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(!IsConnected())
			{
				PleaseDoConnection();
				Connect.this.m_SpyOnOff.setChecked(false);
				return ;
			}
			else if (Connect.this.m_SpyOnOff.isChecked())
			{
				Connect.this.SpyOn();
				return ;
			}
			Connect.this.SpyOff();
		}
	};
	
	private void SpyOn()
	{
		Message msg = handler.obtainMessage();
		handler.sendMessage(msg);
	}
	
	private void SpyOff()
	{
		tv_CanRPM.setText("");
		tv_CanSpeed.setText("");
		tv_CanThrottle.setText("");
	}
	
	//�Ի�����ʾ�û����������豸
	public void PleaseDoConnection()
	{
		Toast toast = Toast.makeText(this, R.string.PleaseDoConnection, 1);
		toast.setGravity(17, 0, 0);
		toast.show();
	}
	
	//��ʼ����Activity��ȫ��UI���
	public void init()
	{
		this.TextConnectedTo = (TextView) findViewById(R.id.TextConnectedToConn);
		this.btn_ScanDevices = (Button) findViewById(R.id.btn_scandevices);
		this.tv_CanRPM = (TextView) findViewById(R.id.textViewRPMCan);
		this.tv_CanSpeed = (TextView) findViewById(R.id.textViewSpeedCan);
		this.tv_CanThrottle = (TextView) findViewById(R.id.textViewThrottleCan);
		this.m_SpyOnOff = (ToggleButton) findViewById(R.id.buttonSpyOnOffCan);
		this.m_SpyOnOff.setTextOff(getResources().getString(R.string.SpyOff));
		this.m_SpyOnOff.setTextOn(getResources().getString(R.string.SpyOn));
		this.m_SpyOnOff.setChecked(false);
		this.tv_ShowFile = (TextView) findViewById(R.id.tv_showfile);
		this.tv_SettingsFiles = (TextView) findViewById(R.id.btn_settingsfiles);
		this.btn_SettingsFiles = (Button) findViewById(R.id.btn_settingsfiles);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection); 		  
       
        init();       //���ظ����е�UI�ؼ�
        
        this.btn_ScanDevices.setOnClickListener(this.OnScanDevices);
        this.btn_SettingsFiles.setOnClickListener(OnFilesSettings);
        this.m_SpyOnOff.setOnClickListener(this.OnSpyOnOff);				 
        
       //����򿪱��������豸���ɹ�����ʾ��Ϣ����������
       if (_bluetooth == null){
        	Toast.makeText(this, "�޷����ֻ���������ȷ���ֻ��Ƿ����������ܣ�", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // �����豸���Ա�����  
       new Thread(){
    	   public void run(){
    		   if(_bluetooth.isEnabled()==false){
        		_bluetooth.enable();
    		   }
    	   }   	   
       }.start();   
       
      
//       if (this.IsConnected()) {
//           this.TextConnectedTo.setText(getString(R.string.ConnectedTo) + this.m_Manager.getM_Data().getM_Device().getName());
//       }
    }

    public boolean IsConnected()
    {
    	return (this.btSocket != null) && (this.btSocket.isConnected());
    }
    
    public PC_Data getM_Data()
    {
  	  return this.m_Data;
    }
    
    public BluetoothDevice getM_Device()
    {
		  return this._device;
    }
    //���Ͱ�����Ӧ
    public void onSendButtonClicked(View v){
    	int i=0;
    	int n=0;
    	try{
    		OutputStream os = btSocket.getOutputStream();   //�������������
    		byte[] bos = edit0.getText().toString().getBytes();
    		for(i=0;i<bos.length;i++){
    			if(bos[i]==0x0a)n++;
    		}
    		byte[] bos_new = new byte[bos.length+n];
    		n=0;
    		for(i=0;i<bos.length;i++){ //�ֻ��л���Ϊ0a,�����Ϊ0d 0a���ٷ���
    			if(bos[i]==0x0a){
    				bos_new[n]=0x0d;
    				n++;
    				bos_new[n]=0x0a;
    			}else{
    				bos_new[n]=bos[i];
    			}
    			n++;
    		}
    		
    		os.write(bos_new);	
    	}catch(IOException e){  		
    	}  	
    }
    
    public void connectToDevice()
    {
        // �÷���ŵõ�socket
        try{
        	btSocket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        }catch(IOException e){
        	Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
        }
        //����socket
    	Button btn = (Button) findViewById(R.id.btn_scandevices);
    	TextView tv = (TextView) findViewById(R.id.TextConnectedToConn);
        try{
        	btSocket.connect();
        	Toast.makeText(this, "����"+_device.getName()+"�ɹ���", Toast.LENGTH_SHORT).show();
        	btn.setText("�Ͽ�");
        	tv.setText("connected to BT-06");
//        	tv.setText(getString(R.string.ConnectedTo) + this.m_Manager.getM_Data().getM_Device().getName());
        }catch(IOException e){
        	try{
        		Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
        		btSocket.close();
        		btSocket = null;
        	}catch(IOException ee){
        		Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
        	}
        	
        	return;	
        }
    }
    
    //���ջ�������ӦstartActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode){
    	case REQUEST_CONNECT_DEVICE:     //���ӽ������DeviceListActivity���÷���
    		// ��Ӧ���ؽ��
            if (resultCode == Activity.RESULT_OK) {   //���ӳɹ�����DeviceListActivity���÷���
                // MAC��ַ����DeviceListActivity���÷���
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // �õ������豸���      
                _device = _bluetooth.getRemoteDevice(address);
//                connectToDevice();
                // �÷���ŵõ�socket
                try{
                	btSocket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                }catch(IOException e){
                	Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                }
                //����socket
            	Button btn = (Button) findViewById(R.id.btn_scandevices);
            	TextView tv = (TextView) findViewById(R.id.TextConnectedToConn);
                try{
                	btSocket.connect();
                	Toast.makeText(this, "����"+_device.getName()+"�ɹ���", Toast.LENGTH_SHORT).show();
                	btn.setText("�Ͽ�");
                	tv.setText("connected to BT-06");
//                	tv.setText(getString(R.string.ConnectedTo) + this.m_Manager.getM_Data().getM_Device().getName());
                }catch(IOException e){
                	try{
                		Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                		btSocket.close();
                		btSocket = null;
                	}catch(IOException ee){
                		Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                	}
                	
                	return;
                }
                
                //����Ӳ��ͨ�������ϴ�������
                try{
            		is = btSocket.getInputStream();   //�õ���������������
            		}catch(IOException e){
            			Toast.makeText(this, "��������ʧ�ܣ�", Toast.LENGTH_SHORT).show();
            			return;
            		}
                	try {
						if(bThread == false){
							//��ʱ
							Thread.sleep(200);
							//������ȡ���ݵĹ����߳�
							ReadThread.start();                //�ر����ݽ����̺߳�������
							bThread = true;
						}else{
							bRun = true;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//                	receiveData();    //��������ģ��BT��06�ϴ�������
            		/*if(bThread==false){
            			
            			//��ʱ
            			try {
    						ReadThread.sleep(200);
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
            			//������ȡ���ݵĹ����߳�
            			ReadThread.start();
//            			thread.start();
            			bThread=true;
            		}else{
            			bRun = true;
            		}*///------>>�������������쳣
            }
    		break;
    	case FILE_CHOOSE_CODE:
    		if(resultCode==RESULT_OK){
      			uri = data.getData();
      			Log.i("uri", uri.toString());
      			path = uri.getPath();			//��ȡ�ļ���·��
      			Log.d("path", path);
      			
    			if (!path.substring(path.lastIndexOf(".") + 1).equals("ccg") && !path.substring(path.lastIndexOf(".") + 1).equals("box"))
    			{
    				Toast toast = Toast.makeText(this, "Error: Please select a corrent file format!", 1);
    				toast.setGravity(17, 0, 0);
    				toast.show();
    			}else if(path.substring(path.lastIndexOf(".") + 1).equals("ccg")){
    				
    				CcgFileName = path.substring(path.lastIndexOf("/") + 1, path.length());
    				tv_ShowFile.setText(CcgFileName);			//��ʾ��ѡCcg�ļ���
    				
    				Log.d("------------------------------", "׼����ȡ�ļ�....");
    				readCcgFile();			//��ȡCcg�ļ�����
    				
    				try {
    					Log.i("----------------", "׼�������ļ�...");
    					sendCcgFile();
    					Toast.makeText(this, R.string.TransferComplete, 0).show();       //��ʾ���سɹ�
    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    					Toast.makeText(this, R.string.TransferFailed, 1).show();			//��ʾ����ʧ��
    				}
    			} else {
    				BoxFileName = path.substring(path.lastIndexOf("/") + 1, path.length());
    				tv_ShowFile.setText(BoxFileName);
    				
    				readBoxFile();      //��ȡBOX�ļ�����
    				
    				try {
						sendBoxFile();     //����BOX�ļ�����
						Toast.makeText(this, R.string.TransferComplete, 0).show();
					} catch (NotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(this, R.string.TransferFailed, 1).show();			//��ʾ����ʧ��
					}    				
    			}
      		}
    	default:break;
    	}
    }
   
    public String readBoxFile()
    {
    	try {
			//����ֻ�������SD��������Ӧ�ó�����з���SD����Ȩ��
			if(Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED))
			{
				File sdCardDir = Environment.getExternalStorageDirectory();
				//��ȡSD����Ӧ�Ĵ洢Ŀ¼
				System.out.println("---------------------" + "��ʼ��ȡ�ļ�����...");
				//��ȡָ���ļ��Ķ�Ӧ������
//				FileInputStream fis = new FileInputStream( 
//						sdCardDir.getCanonicalPath() + SEND_FILE_NAME);
				
				FileInputStream fis = new FileInputStream(uri.getPath());			//����Box�ļ�·������������
				//��ָ����������װ��BufferedReader
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				StringBuffer sb = new StringBuffer("");
				String lineStr = null;
				int lineNum = 1;
				//ѭ����ȡ�ļ�����
				while((lineStr = br.readLine()) != null)
				{
 					
					System.out.println(lineStr);
					String newlineStr = lineStr + "\r\n";
					sb.append(newlineStr);
					lineNum ++;
				}
				BoxBuffer = sb.toString().getBytes();
				System.out.println("��ѡBox�ļ�����Ϊ:  " +  sb.toString());
				//�ر���Դ
				br.close();
				return sb.toString();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    public static String bytesToHexString(byte[] bytes)
    {	
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }
    
    public void sendBoxFile()
    {
		System.out.println("��ʼ�������");
			try {
				if(!IsConnected())
				{ 
					System.out.println("����������������");
					PleaseDoConnection();
				}else{
//					Connect m_Connect = new Connect();
//					OutputStream os = m_Connect.getBtSocket().getOutputStream();    
//					OutputStream os = this.getBtSocket().getOutputStream();    
					OutputStream os = btSocket.getOutputStream();
					//��ָ����������װ��BufferedReader
					os.write(BoxBuffer);
					os.flush();
					System.out.println("���ڷ���BOX�ļ�.....");
					//�ر���������ر�Socket
					os.close();
//					this.getBtSocket().close();
					btSocket.close();
					System.out.println("�ѷ���BOX�ļ����ݣ�" + BoxBuffer);
				}
			} catch (Exception e) {
				e.printStackTrace();
				 System.out.println("��Ŷ��������ʧ���ˣ�");
			}
    }
    
    /**
  	 * ��ȡ�����������ѡ���Ccg�ļ�
  	 */
  	public void sendCcgFile()  
  	{
//  		byte[] ccg = CcgBuffer.getBytes();
  		System.out.println("��ʼ�������");
  			try {
					if(!IsConnected())
					{ 
						System.out.println("����������������");
						PleaseDoConnection();
					}else{
//						Connect m_Connect = new Connect();
//						OutputStream os = m_Connect.getBtSocket().getOutputStream();    
//						OutputStream os = this.getBtSocket().getOutputStream();    
						OutputStream os = btSocket.getOutputStream();
						//��ָ����������װ��BufferedReader
 
				 
						os.write(CcgBuffer);
						os.flush();
						System.out.println("���ڷ����ļ�.....");
						//�ر���������ر�Socket
//						os.close();
//						this.getBtSocket().close();
//						btSocket.close();
						System.out.println("�ѷ����ļ����ݣ�" + CcgBuffer);
					}
				} catch (Exception e) {
					e.printStackTrace();
					 System.out.println("��Ŷ��������ʧ���ˣ�");
				}
				
//    		 while (true)
//             {
//                 BufferedReader reader = new BufferedReader(
//                         new InputStreamReader(System.in));
//  
//                 String line = reader.readLine();
//  
//                 os.write(line.getBytes());
//             }
//         }
//         catch (IOException e)
//         {
//             e.printStackTrace();
//         }
//    		
  	}
  	
    /**
	 * ��ȡSD���ļ�·������ȡCcg�ļ�����
	 * @return  �ļ�����  sb.toString() 
	 */
	public String readCcgFile()
	{
		
		try {
			//����ֻ�������SD��������Ӧ�ó�����з���SD����Ȩ��
			if(Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED))
			{
				File sdCardDir = Environment.getExternalStorageDirectory();
				//��ȡSD����Ӧ�Ĵ洢Ŀ¼
				System.out.println("---------------------" + "��ʼ��ȡ�ļ�����...");
				//��ȡָ���ļ��Ķ�Ӧ������
//				FileInputStream fis = new FileInputStream( 
//						sdCardDir.getCanonicalPath() + SEND_FILE_NAME);
				
				FileInputStream fis = new FileInputStream(uri.getPath());			//�����ļ�·������������
				//��ָ����������װ��BufferedReader
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				StringBuffer sb = new StringBuffer("");
				String lineStr = null;
				int lineNum = 1;
				//ѭ����ȡ�ļ�����
				while((lineStr = br.readLine()) != null)
				{
 					
					System.out.println(lineStr);
					String newlineStr = lineStr + "\r\n";
					sb.append(newlineStr);
					lineNum ++;
				}
				CcgBuffer = sb.toString().getBytes();
				System.out.println("��ѡCCG�ļ�����Ϊ:  " +  sb.toString());
				//�ر���Դ
				br.close();
				return sb.toString();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
    public void receiveData()
    {
    	 //����Ӳ��ͨ�������ϴ�������
        try{
    		is = btSocket.getInputStream();   //�õ���������������
    		}catch(IOException e){
    			Toast.makeText(this, "��������ʧ�ܣ�", Toast.LENGTH_SHORT).show();
    			return;
    		}
        	try {
				if(bThread == false){
					//��ʱ
					Thread.sleep(200);
					//������ȡ���ݵĹ����߳�
					ReadThread.start();
					bThread = true;
				}else{
					bRun = true;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
    }
    //���������߳�
    Thread ReadThread=new Thread(){
    	
    	public void run(){
    		int num = 0;
    		byte[] buffer = new byte[1024];
    		byte[] buffer_new = new byte[1024];
    		int i = 0;
    		int n = 0;
    		bRun = true;
    		
    		//�����߳�
    		while(true){
    			
    			try{
    				while(is.available()==0){
    					while(bRun == false){}
    				}
    				while(true){
    					//�������ݻ���ʱ��������ʱ
    					Thread.sleep(200);
    					
    					num = is.read(buffer);         //��������
    					n=0;
     					smsg = "";
    					String s0 = new String(buffer,0,num);
     					fmsg = s0;
//    					fmsg +=s0;    //�����յ�����
    					for(i=0;i<num;i++){
    						if((buffer[i] == 0x0d)&&(buffer[i+1]==0x0a)){
    							buffer_new[n] = 0x0a;
    							i++;
    						}else{
    							buffer_new[n] = buffer[i];
    						}
    						n++;
    					}
    					String s = new String(buffer_new,0,n);
    					smsg +=s;   //д����ջ���
    					
    					if(is.available()==0)break;  //��ʱ��û�����ݲ�����������ʾ
    				}
    				
    				System.out.println(smsg);
    				//����һ�����߳�����Ӧ����ģ���ϴ�������	
    			 /*	new Thread()
    				{
    					@Override
    					public void run()
    					{
    						try {
    							 
								Message msg = handler.obtainMessage();
								handler.sendMessage(msg);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    					}
    				}.start();*/
    			 
            		//ʹ��Handler������Ϣ,������ʾˢ��
//            		try {
//            			Message message = handler.obtainMessage();
//            			handler.sendMessage(message);
//            		} catch (Exception e) {
//            			// TODO Auto-generated catch block
//            			e.printStackTrace();
//            		} 
    				
    				//ʹ��Handler������Ϣ,������ʾˢ��
//    				Message message = handler.obtainMessage();
//    				handler.sendMessage(message);    
    					
    	    		}catch(IOException | InterruptedException e){
    	    			System.out.println("δ�յ�����");
    	    			e.printStackTrace();
    	    		}
    		}
    	}
    };
    
    //��Ϣ�������
    Handler handler= new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		 
    		//�������ݣ���ȡת�٣����٣����ſ���
    		
    		rmp = smsg.substring(smsg.indexOf('R')+1, smsg.indexOf('S'));
    		tv_CanRPM.setText(rmp); 	  	//��ʾת��
    			
    		speed = smsg.substring(smsg.indexOf('S')+1, smsg.indexOf('T'));
    		tv_CanSpeed.setText(speed);	//��ʾ����
    		
    		throttle = smsg.substring(smsg.indexOf('T')+1, smsg.indexOf('E'));
     		tv_CanThrottle.setText(throttle);//��ʾ���ſ���
    		
    	}
    };
    
   //�½�һ���߳�ʵʱ�ķ�������
    Thread thread = new Thread()
    {
    	public void run()
    	{
    		String [] Buffer = {"R1000S15T10E", "R1500S20T15E", "R2000S25T20E", "R2500S30T25E", "R3000S35T30E", "R3500S40T35E", "R4000S4550T40E", "R4500S60T45E", "R5000S80T50E", "R6000S100T60E"};
    		int i;
    		for(i=0; i < 10; i++)
    		{
    			smsg = Buffer[i];
    			System.out.println(smsg);
    		
    			try {
    				Thread.sleep(2000);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        		//������Ϣ
        		Message m = handler.obtainMessage();
        		handler.sendMessage(m);
        		
    		}
    	}
    };
    
    //�رճ�����ô�����
    public void onDestroy(){
    	super.onDestroy();
//    	if(_socket!=null)  //�ر�����socket
//    	try{
//    		_socket.close();
//    	}catch(IOException e){}
    //	_bluetooth.disable();  //�ر���������
    }
  
    //���Ӱ�����Ӧ����
    public void onConnectButtonClicked(View v){ 
    	if(_bluetooth.isEnabled()==false){  //����������񲻿�������ʾ
    		Toast.makeText(this, " ��������...", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	
        //��δ�����豸���DeviceListActivity�����豸����
    	Button btn = (Button) findViewById(R.id.btn_scandevices);
    	if(btSocket==null){
    		Intent serverIntent = new Intent(this, DeviceListActivity.class); //��ת��������
    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //���÷��غ궨��
    	}
    	else{
    		 //�ر�����socket
    	    try{
    	    	
    	    	is.close();
    	    	btSocket.close();
    	    	btSocket = null;
    	    	bRun = false;
    	    	btn.setText("����");
    	    }catch(IOException e){}   
    	}
    	return;
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		//����ʱ������Menu��ʵ����MenuBuilder����  
        Log.d("MainActivity", "menu--->" + menu);  
          
        /*���÷�����Ƶ���MenuBuilder��setOptionalIconsVisible��������mOptionalIconsVisibleΪtrue�� 
         * ���˵�����ͼ��ʱ�ſɼ� 
         */  
        setIconEnable(menu, true);
          
        return super.onCreateOptionsMenu(menu);  
	}
	
	//enableΪtrueʱ���˵����ͼ����Ч��enableΪfalseʱ��Ч��4.0ϵͳĬ����Ч 
	private void setIconEnable(Menu menu, boolean enable)  
    {  
        try   
        {  
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
            Method method = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
            method.setAccessible(true);  
              
            //MenuBuilderʵ��Menu�ӿڣ������˵�ʱ����������menu��ʵ����MenuBuilder����(java�Ķ�̬����)  
            method.invoke(menu, enable);  
              
        } catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
    }  
	/*	
	//���ѡ��˵���Ӧ����
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
		case R.id.menu_home:
			GoHome();
			break;
		case R.id.menu_connnection:
			Launch_Connection();
			break;
		case R.id.menu_cansettings:
			Launch_CanSettings();
			break;
		case R.id.menu_boxsettings:
			Launch_BoxSettings();
			break;
		case R.id.menu_help:
			Launch_Help();
			break;
		case R.id.menu_logs:
			Launch_Logs();
			break;
		case R.id.menu_about:
			Launch_About();
			break;
		case R.id.menu_demo:
			Launch_Demo();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		
		return true;
	}
*/
	public void ActivityFinish()
	{
		this.m_Manager.deleteBoxSettings();
		finish();
	}
	
	public void GoHome()
	{
		ActivityFinish();
	}
	
	public void Launch_Connection()
	{
		startActivity(new Intent(this, Connect.class));
		ActivityFinish();
	}
	
	public void Launch_CanSettings()
	{
		startActivity(new Intent(this, CanSettings.class));
		ActivityFinish();
	}
	
	public void Launch_BoxSettings()
	{
		
	}
	
	public void Launch_Help()
	{
		startActivity(new Intent(this, Help.class));
		ActivityFinish();
	}
	
	public void Launch_Logs()
	{
		startActivity(new Intent(this, Logs.class));
		ActivityFinish();
	}
	
	public void Launch_About()
	{
		startActivity(new Intent(this, About.class));
		ActivityFinish();
	}
	
	public void Launch_Demo()
	{
		startActivity(new Intent(this, Demo.class));
		ActivityFinish();
	}

	public String getRmp() {
		return rmp;
	}

	public void setRmp(String rmp) {
		this.rmp = rmp;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getThrottle() {
		return throttle;
	}

	public void setThrottle(String throttle) {
		this.throttle = throttle;
	}
	
}