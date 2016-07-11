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
//import android.view.Menu;			//如使用菜单加入此三包
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Test extends Activity {
	
	private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备
	
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";			//SPP服务UUID号
	
	private InputStream is;			//输入流，用来接收蓝牙数据
	private TextView text0; 	   	//提示栏
    private EditText edit0;    		//发送数据输入框
    private TextView dis;       	//接收数据显示
    private ScrollView sv;     		//翻页
    private String smsg = "";    	//显示用数据缓存
    private String fmsg = "";    	//保存用数据缓存
    
    private Button SendMsgBtn;
    public String filename=""; 			//用来保存存储的文件名
    BluetoothDevice _device = null;	    //蓝牙设备
    BluetoothSocket _socket = null; 	//蓝牙通信socket
    boolean _discoveryFinished = false;    
    boolean bRun = true;
    boolean bThread = false;
	
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();		//实例化BluetoothAdapter，获取本地蓝牙适配器，即蓝牙设备

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);		 
        
        text0 = (TextView)findViewById(R.id.Text0);			
        edit0 = (EditText)findViewById(R.id.Edit0);			
        sv = (ScrollView)findViewById(R.id.ScrollView01);	
        dis = (TextView) findViewById(R.id.in);      		
        SendMsgBtn = (Button) findViewById(R.id.SendMsgBtn);
        
//       //如果打开本地蓝牙设备不成功，提示信息，结束程序
//        if (_bluetooth == null){
//        	Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
//        
//        // 设置设备可以被搜索  
//       new Thread(){
//    	   public void run(){
//    		   if(_bluetooth.isEnabled()==false){
//        		_bluetooth.enable();
//    		   }
//    	   }   	   
//       }.start();  
        ReadThread.start();
        
    }

    //发送按键响应
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
//				
//				byte[] bos_new = new byte[bos.length+n];
//				n=0;
//				
//				for(i=0; i<bos.length; i++){  
//					if(bos[i]==0x0a){
//						bos_new[n]=0x0d;
//						n++;
//						bos_new[n]=0x0a;
//					}else{
//						bos_new[n]=bos[i];
//					}
//					n++;
//				}
//				
				os.write(bos);
			} catch (IOException e) {
				e.printStackTrace();
			}	
    	 
    }
    
    //接收数据线程
    Thread ReadThread = new Thread(){
    	
    	public void run(){
    		int num = 0;
    		byte[] buffer = new byte[1024];
    		byte[] buffer_new = new byte[1024];
    		int i = 0;
    		int n = 0;
    		bRun = true;
    		//接收线程
    		while(true){
    			try{
    				while(is.available()==0){
    					while(bRun == false){}
    				}
    				while(true){
    					num = is.read(buffer);         //读入数据
    					n=0;
    					
    					String s0 = new String(buffer,0,num);
    					fmsg+=s0;    //保存收到数据
    					
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
    					smsg+=s;   //写入接收缓存
    					if(is.available()==0)break;  //短时间没有数据才跳出进行显示
    					System.out.println(smsg);
    				}
    				//发送显示消息，进行显示刷新
    					handler.sendMessage(handler.obtainMessage());       	    		
    	    		}catch(IOException e){
    	    		}
    		}
    	}
    };
    
    //消息处理队列
    Handler handler= new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		dis.setText(smsg);   //显示数据 
    		sv.scrollTo(0,dis.getMeasuredHeight()); //跳至数据最后一页
    	}
    };
    
    //关闭程序调用处理部分
    public void onDestroy(){
    	super.onDestroy();
    	if(_socket!=null)  //关闭连接socket
    	try{
    		_socket.close();
    	}catch(IOException e){}
    //	_bluetooth.disable();  //关闭蓝牙服务
    }
}
