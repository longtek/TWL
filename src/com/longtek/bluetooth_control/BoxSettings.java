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
 * BoxSettings类，主要用于操作SD卡上的BOX文件，设置加载到硬件控制盒的文件
 * @author Administrator
 *
 */
public class BoxSettings extends Activity {

	private ArrayAdapter<String> BoxArrayAdapter;
	private Button DeleteAllBox;		//删除按钮
	private ListView ListBox;			//BOX文件列表
	private Button LoadBox;			//选择BOX文件按钮
	private Boolean IsBoxLoaded = true;
	private Fac_Manager m_Manager = null;
	final int PICK_BOXFILE = 0;    //请求码
	private String BoxFileName;
	private String path;
	private Uri pickData;       
	private String BoxBuffer;
	private String[] Box;
	
	//创建Box文件选择按钮单击事件监听器
	private View.OnClickListener OnLoadBox = new View.OnClickListener()
	{
		public void onClick(View view)
	    {	
			//创建Intent
			Intent intent = new Intent();
			//设置Intent的Action属性
			intent.setAction("android.intent.action.GET_CONTENT");
			//设置intent的Type属性
			intent.setType("*/*");
			intent.addCategory("android.intent.category.OPENABLE");
			//启动Activity,并希望获取该Activity的结果
			BoxSettings.this.startActivityForResult(Intent.createChooser(intent, "Choose Box file"), PICK_BOXFILE);
	    }
	};
	
	//删除按钮响应事件监听器
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
	
	//显示可用的Box文件列表
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
	//初始化该类的UI组件
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
		Log.d("OnloadBox", "选择按钮被点击了！！！");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		//运行时，参数Menu其实就是MenuBuilder对象  
        Log.d("MainActivity", "menu--->" + menu);  
          
        /*利用反射机制调用MenuBuilder的setOptionalIconsVisible方法设置mOptionalIconsVisible为true， 
         * 给菜单设置图标时才可见 
         */  
        setIconEnable(menu, true);
          
        return super.onCreateOptionsMenu(menu);  
	}
	
	//enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效 
	private void setIconEnable(Menu menu, boolean enable)  
    {  
        try   
        {  
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
            Method method = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
            method.setAccessible(true);  
              
            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)  
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
	
	//各个子类之间相互跳转函数
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
	 * 读取BOX文件的内容
	 * @return  BoxBuffer 
	 */
	
	public String readBoxFile() 
	{
		try {
			FileInputStream fis = new FileInputStream(pickData.getPath());    		//创建文件输入流，并传入BOX文件路径
			//将指定的输入流包装成BufferReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			StringBuffer sb = new StringBuffer();
			String line = null;
			//按行循环读取文件内容
			while((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			BoxBuffer = sb.toString();
			System.out.println(BoxBuffer);      //输出文件内容
			
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
	 * 回调OnActivityResult方法，返回访问手机存储后，选取的BOX文件结果
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
				
				//判断选择文件是不是BOX文件
				if(!path.substring(path.lastIndexOf(".") + 1).equals("box")){
					
					Toast toast = Toast.makeText(this, "Error: please select a .box file", 1);
					toast.setGravity(17, 0, 0);
					toast.show();
					
				}else{
					path = pickData.getPath();
					BoxFileName = path.substring(path.lastIndexOf("/") + 1, path.length());		//从路径中截获所选文件名
					Log.d("当前所选文件----->>", BoxFileName);
					//将所选文件名加入到文件列表
					
					Box = new String[10];     //Box文件存放数组
					int i;
					for(i = 0; i < 10; i++)
					{
						Box[i] = BoxFileName; 
					}
					this.BoxArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_chooser_view_list_item, Box);
					this.ListBox = ((ListView)findViewById(R.id.listBoxBox));
					this.ListBox.setAdapter(BoxArrayAdapter);
					this.ListBox.setChoiceMode(i);
					
					//读取文件
					readBoxFile();
				}
			}
		}
	}
}
