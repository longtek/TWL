package com.longtek.bluetooth_control;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * BoxSettings�࣬��Ҫ���ڲ���SD���ϵ�BOX�ļ������ü��ص�Ӳ�����ƺе��ļ�
 * @author Administrator
 *
 */
public class BoxSettings extends Activity {

	private ArrayAdapter<String> BoxArrayAdapter;
	private Button DeleteAllBox;		//ɾ����ť
	private ListView ListBox;			//BOX�ļ��б�
	private Button LoadBox;			//ѡ��BOX�ļ���ť
	private Boolean IsBoxLoaded = true;
	private Fac_Manager m_Manager = null;
	final int PICK_BOXFILE = 0;    //������
	private String BoxFileName;
	private String path;
	private Uri pickData;       
	private String BoxBuffer;
	private String[] Box;
	
	//����Box�ļ�ѡ��ť�����¼�������
	private View.OnClickListener OnLoadBox = new View.OnClickListener()
	{
		public void onClick(View view)
	    {	
			//����Intent
			Intent intent = new Intent();
			//����Intent��Action����
			intent.setAction("android.intent.action.GET_CONTENT");
			//����intent��Type����
			intent.setType("*/*");
			intent.addCategory("android.intent.category.OPENABLE");
			//����Activity,��ϣ����ȡ��Activity�Ľ��
			BoxSettings.this.startActivityForResult(Intent.createChooser(intent, "Choose Box file"), PICK_BOXFILE);
	    }
	};
	
	//ɾ����ť��Ӧ�¼�������
	private View.OnClickListener OnDeleteAllBox = new View.OnClickListener()
	{
	    public void onClick(View view)
	    {
//	    	new DialogDelete().show(BoxSettings.this.getSupportFragmentManager(), "DialogDelete");
	    }
	};
	
	public void BoxFlashed(boolean IsBoxLoaded)
	{
		if (IsBoxLoaded)
	    {
			Toast.makeText(this, R.string.TransferComplete, 0).show();
			return;
	    }
	    Toast.makeText(this, R.string.TransferFailed, 1).show();
	}
	
	public void BoxLoaded(boolean IsBoxLoaded)
	{
	    if (!IsBoxLoaded)
	      Toast.makeText(this, R.string.TransferFailed, 1).show();
	}
	
	//��ʾ���õ�Box�ļ��б�
	public void BoxListAvailable(String[] ArrayOfString)
	{
		CleanBoxArrayAdapter();
	    int i = 0;
	    while (true)
	    {
	    	if (i >= ArrayOfString.length)
	    	{
	    		this.ListBox.setAdapter(this.BoxArrayAdapter);
	    		return;
	    	}
	    	appendBoxArrayAdapter(ArrayOfString[i]);
	    	i += 1;
	    }
	}
	
	private boolean CleanBoxArrayAdapter()
	{
	    this.BoxArrayAdapter = new ArrayAdapter<String>(this, 17367043);
	    this.ListBox.setAdapter(this.BoxArrayAdapter);
	    return true;
	}
	
	public boolean appendBoxArrayAdapter(String string)
	{
		this.BoxArrayAdapter.add(string);
		return true;
	}
	
	public void updateNBox(int Int)
	{
		
	}
	//��ʼ�������UI���
	public void init()
	{
		this.LoadBox = ((Button)findViewById(R.id.ButtonBrowseBoxBox));
		this.DeleteAllBox = ((Button)findViewById(R.id.ButtonDeleteBoxBox));
//		this.BoxArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_chooser_view_list_item);
//		this.ListBox = ((ListView)findViewById(R.id.listBoxBox));
//		this.ListBox.setChoiceMode(1);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boxsettings);
		
		init();
		this.LoadBox.setOnClickListener(this.OnLoadBox);
		Log.d("OnloadBox", "ѡ��ť������ˣ�����");
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
	
	//��������֮���໥��ת����
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

	/**
	 * ��ȡBOX�ļ�������
	 * @return  BoxBuffer 
	 */
	
	public String readBoxFile() 
	{
		try {
			FileInputStream fis = new FileInputStream(pickData.getPath());    		//�����ļ���������������BOX�ļ�·��
			//��ָ������������װ��BufferReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			StringBuffer sb = new StringBuffer();
			String line = null;
			//����ѭ����ȡ�ļ�����
			while((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			BoxBuffer = sb.toString();
			System.out.println(BoxBuffer);      //����ļ�����
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/***
	 * �ص�OnActivityResult���������ط����ֻ��洢��ѡȡ��BOX�ļ����
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)
		{
		case (PICK_BOXFILE) :
			if(requestCode == Activity.RESULT_OK)
			{
				pickData = data.getData();
				Log.d("--------------Uri----------------", pickData.toString());
				
				//�ж�ѡ���ļ��ǲ���BOX�ļ�
				if(!path.substring(path.lastIndexOf(".") + 1).equals("box")){
					
					Toast toast = Toast.makeText(this, "Error: please select a .box file", 1);
					toast.setGravity(17, 0, 0);
					toast.show();
					
				}else{
					path = pickData.getPath();
					BoxFileName = path.substring(path.lastIndexOf("/") + 1, path.length());		//��·���нػ���ѡ�ļ���
					Log.d("��ǰ��ѡ�ļ�----->>", BoxFileName);
					//����ѡ�ļ������뵽�ļ��б�
					
					Box = new String[10];     //Box�ļ��������
					int i;
					for(i = 0; i < 10; i++)
					{
						Box[i] = BoxFileName; 
					}
					this.BoxArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_chooser_view_list_item, Box);
					this.ListBox = ((ListView)findViewById(R.id.listBoxBox));
					this.ListBox.setAdapter(BoxArrayAdapter);
					this.ListBox.setChoiceMode(i);
					
					//��ȡ�ļ�
					readBoxFile();
				}
			}
		}
	}
}
