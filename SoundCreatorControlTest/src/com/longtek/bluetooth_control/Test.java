package com.longtek.bluetooth_control;
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import com.longtek.bluetooth_control.DeviceListActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
//import android.view.Menu;			//��ʹ�ò˵����������
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Test extends Activity {
	
	private final static int REQUEST_CONNECT_DEVICE = 1;    //�궨���ѯ�豸
	
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";			//SPP����UUID��
	
	private InputStream is;			//������������������������
	private TextView text0; 	   	//��ʾ��
    private EditText edit0;    		//�������������
    private TextView dis;       	//����������ʾ
    private ScrollView sv;     		//��ҳ
    private String smsg = "";    	//��ʾ�����ݻ���
    private String fmsg = "";    	//���������ݻ���
    
    private Button SendMsgBtn;
    public String filename=""; 			//��������洢���ļ���
    BluetoothDevice _device = null;	    //�����豸
    BluetoothSocket _socket = null; 	//����ͨ��socket
    boolean _discoveryFinished = false;    
    boolean bRun = true;
    boolean bThread = false;
	
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();		//ʵ����BluetoothAdapter����ȡ�����������������������豸

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);		 
        
        text0 = (TextView)findViewById(R.id.Text0);			
        edit0 = (EditText)findViewById(R.id.Edit0);			
        sv = (ScrollView)findViewById(R.id.ScrollView01);	
        dis = (TextView) findViewById(R.id.in);      		
        SendMsgBtn = (Button) findViewById(R.id.SendMsgBtn);
        
//       //����򿪱��������豸���ɹ�����ʾ��Ϣ����������
//        if (_bluetooth == null){
//        	Toast.makeText(this, "�޷����ֻ���������ȷ���ֻ��Ƿ����������ܣ�", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
//        
//        // �����豸���Ա�����  
//       new Thread(){
//    	   public void run(){
//    		   if(_bluetooth.isEnabled()==false){
//        		_bluetooth.enable();
//    		   }
//    	   }   	   
//       }.start();  
        ReadThread.start();
        
    }

    //���Ͱ�����Ӧ
    public void onSendButtonClicked(View v)
    {
    	
    	int i=0;
    	int n=0;
 
    		try {
				OutputStream os = _socket.getOutputStream();    
				byte[] bos = edit0.getText().toString().getBytes();
  	
				for(i=0; i<bos.length; i++){
					if(bos[i]==0x0a)
						n++;
				}
				
				byte[] bos_new = new byte[bos.length+n];
				n=0;
				
				for(i=0; i<bos.length; i++){  
					if(bos[i]==0x0a){
						bos_new[n]=0x0d;
						n++;
						bos_new[n]=0x0a;
					}else{
						bos_new[n]=bos[i];
					}
					n++;
				}
				
				os.write(bos);
			} catch (IOException e) {
				e.printStackTrace();
			}	
    	 
    }
    
    //���������߳�
    Thread ReadThread = new Thread(){
    	
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
    					num = is.read(buffer);         //��������
    					n=0;
    					
    					String s0 = new String(buffer,0,num);
    					fmsg+=s0;    //�����յ�����
    					
    					System.out.println(s0);
    					
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
    					smsg+=s;   //д����ջ���
    					if(is.available()==0)break;  //��ʱ��û�����ݲ�����������ʾ
    					System.out.println(smsg);
    				}
    				//������ʾ��Ϣ��������ʾˢ��
    					handler.sendMessage(handler.obtainMessage());       	    		
    	    		}catch(IOException e){
    	    		}
    		}
    	}
    };
    
    //��Ϣ�������
    Handler handler= new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		dis.setText(smsg);   //��ʾ���� 
    		sv.scrollTo(0,dis.getMeasuredHeight()); //�����������һҳ
    	}
    };
    
    //�رճ�����ô�����
    public void onDestroy(){
    	super.onDestroy();
    	if(_socket!=null)  //�ر�����socket
    	try{
    		_socket.close();
    	}catch(IOException e){}
    //	_bluetooth.disable();  //�ر���������
    }
}
