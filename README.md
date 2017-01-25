# HuanHybridPlugin(HHP)（欢网 Hybrid Plugin）-

关注 [欢视网](http://www.tvhuan.com/).


## 产品定位及功能介绍

	HHP 是一种全新的开发框架，基于Android平台建立的一套以插件为内容的框架；支持动态UI布局、更加好用的自定义View、支持js控制UI。

## app-config 核心配置文件
	
	一个HHP项目都会有一个最顶级的配置文件 app_config.xml(名称可自定义，结构必须遵守规范，参见：hhp.dtd)

## 配置文件

```
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE app-config SYSTEM "http://com.huan.hhp">
<app-config package="com.huan.appstore" cacheable="true">
    <persistence-resources>
        <resource name="project" value="http://172.10.10.211:8080/testhhp" />
		<resource name="main" value="/main.xml" ver="1.1" keepMilliseconds="2000" />
    </persistence-resources>
    <resources>
        <resource name="def-application" value="com.huan.hhp.PluginApplication" />
        <resource name="def-activity" value="com.huan.hhp.SimplePluginActivity" />
    </resources>
    <plugin-list>
        <plugin id="app-1" name="appName-1" project="{project}" file="{?}" isMain="true">
            <application class="{def-application}">
                <activity name="main" class="{def-activity}" layout="{main}" isMain="true">
			<intent key1="value1" key2="value2" />
		</activity>
            </application>
        </plugin>
    </plugin-list>
    <clear ids=""></clear>
</app-config>
```

* `package` 包名，全局唯一 用来区分工作空间。因为一个项目中可能有多个同样的配置文件。
* `cacheable` 是否将配置文件缓存到本地。

-----------------------------------------------------------
* `<persistence-resources>` 持久化资源池。
* `<resources>` 非持久化资源池。
* `<resource>` 资源。键值的形式出现，`name`是资源的引用标签，`value`是资源的内容。`ver`为资源的版本`<persistence-resources>`时生效。
`keepMilliseconds="2000"` 表示该资源会在退出2000毫秒后删除本地缓存。

-----------------------------------------------------------
* `<plugin-list>` 表示插件池，其中可能会出现多个插件即`<plugin>`
* `<plugin>` 插件，其中 `id="app-1"` app-1是该插件的唯一id，`name="appName-1"` appName-1 是插件的名称，也就是插件的key 也可以写成 `name="{appName-1}"`
这样的话 appName-1 就是资源(resource)的name。

* `project` 项目更目录
* `file` 插件压缩文件所在目录 `file="{?}"` 表示没有压缩文件。
* `isMain="true"` 表示第一次运行的插件。 	

-----------------------------------------------------------
* `<application class="{def-application}">` 插件运行时。如果是自定义类需要继承：`com.huan.hhp.PluginApplication` , 否则直接用 `com.huan.hhp.PluginApplication` 。

-------------------------------------
* `<activity name="main" class="{def-activity}" layout="{main}" isMain="true">` `class` 是activity的类名需继承`com.huan.hhp.PluginActivity`或`com.huan.hhp.SimplePluginActivity`
`layout` 是布局文件 如：/main.xml

* `<intent key1="value1" key2="value2" />` 打开activity时带的一些参数.

```
获取方式为：

Intent intent = getIntent();
String value1 = intent.getStringExtra("key1");
String value2 = intent.getStringExtra("key2");

```

## 布局文件

```
<?xml version="1.0" encoding="utf-8"?>
<ScrollView>
	<LinearLayout id="aaaa" background="#454545" orientation="vertical">

		<script>
			function onKeyDown(keyCode){
				if(keyCode == 21 || keyCode == H.getEventCode("menu")){
					H.startActivity("com.huan.appstore.MainActivity");
					return true;
				}
				return false;
			}
		</script>


		<RelativeLayout width="fill_parent" height="wrap_content">
			<Button id="button1" text="hello world" background="#cc0000" />
			<Button id="button2" text="hello world1" layout_below="button1" background="#cccccc" />

			<TextView text="hello world" layout_centerInParent="true" background="#cccccc" />
		</RelativeLayout>

	</LinearLayout>
</ScrollView>
```
布局文件和普通的android布局是几乎一样的，除了可以省略 `android:` 和支持了一些扩展属性之外还支持了 `<script>` 标签。

## 让Activity具备js能力

* `context` 是上下文， 可以通过 `context.findViewById('button1'); ` 来获取View
* `H` 上工具类

## Script介绍

* 第一种

```
<script>
	// 初始化方法
	function onReady(){
		ver button1 = context.findViewById('button1')；
		button1.setText("HHP-Button1")；
	}

	// 界面被唤醒
	function onResume(){
	
	}

	// 界面被暂停
	function onPause(){
	
	}

	function onKeyDown(keyCode){
		if(keyCode == H.getEventCode("left")){
			H.alert("按键为左键")
		}
	}

	function onKeyUp(keyCode){
	}
</script>
```


`H 工具类提供的键值有:`
```

private HashMap<String, Integer> keyMappings = new HashMap<String, Integer>();
{
	keyMappings.put("left", KeyEvent.KEYCODE_DPAD_LEFT);
	keyMappings.put("up", KeyEvent.KEYCODE_DPAD_UP);
	keyMappings.put("right", KeyEvent.KEYCODE_DPAD_RIGHT);
	keyMappings.put("down", KeyEvent.KEYCODE_DPAD_DOWN);
	keyMappings.put("center", KeyEvent.KEYCODE_DPAD_CENTER);
	keyMappings.put("enter", KeyEvent.KEYCODE_ENTER);
	keyMappings.put("back", KeyEvent.KEYCODE_BACK);
	keyMappings.put("menu", KeyEvent.KEYCODE_MENU);
}
```
	

* 第二种

```
<script src="/index.js" />
```
		

## ajax

```
H.ajax({
	method:'get',
	url:'http://com.huan.tv',
	body:function(){
		return "body";
	},
	contentType:"application/json"
	
}, 

{
	success:function（data）{

	},

	error:function(data){

	}
});

```

## 异步任务

```
H.asyncTask({
	run:function(arg1, arg2){
		// 一大堆代码

		return arg1+" "+arg2;
	}
}, 

{"hello world", "HHP"}, 

{
	run:function(obj){
		H.alter("执行完返回的结果是："+obj);
	}
});
```

## 循环调用之 loop

```
function timer(){

}

H.loop("timer", 1000, -1); // 无限次循环
```

```
H.loop("timer", 1000, 3); // 3次循环
```

```
function timer(arg1, arg2 ...){

}

H.loop("timer", 1000, -1, arg1, arg2 ...)
```


## 停止循环调用

```
H.stopLoop("timer");
```

## 获取分辨率显示单位

```
var width = H.rso(1024);
H.alter("width="+width);
```

## 弹框 alert

```
H.alert("hello world");
```

## 弹层 toast

```
H.toast("hello world");
```

## Log 记录行为

var TAG = "LOG_TAG-Main";

* H.logI(TAG, "log ...")；
* H.logD(TAG, "log ...")；
* H.logE(TAG, "log ...")；

## 输出到控制台

* H.print("hello world");
* H.println("hello world");

## 动画缩放

```
var view1 = context.findViewById("view1");

// 放大动画
H.scale(view1, 1.1f, 200, {
	begin: function(){
		// 开始
	},

	end: function（）{
		// 结束
	}
}

// 缩小动画
H.shrink(view1, 1.1f, 200, {
	begin: function(){
		// 开始
	},

	end: function（）{
		// 结束
	}
}
```

## 自定义动画

```
var view1 = context.findViewById("view1");
var animUtil = H.createAnimUtil(view1);
animUtil.layout(1, 10, 100, 100);

animUtil.animate(1, 10, 100, 100, {
	end: function(){
		H.alert("over!");

		animUtil.animate(1, 10, 200, 200);
	}
});

```

## 获取文件MD5

```
var md5 = H.getFileMD5("/data/data/com.example.TestHHP/cache/a.txt");
```

## 启动Activity

```
H.startActivity("main_layout") main_layout是配置文件中的name

H.startActivity("file:///res/layout/main_layout.xml") // 打开本地workspacke中的文件

H.startActivity("/main_layout.xml") // 打开服务器上的文件

H.startActivity("com.example.aaa.ui.MainActivity") // 跳转到系统Activity

H.startActivity("com_huan_action") // 跳转到action
```

## 获取插件

```
var plugin = H.getPluginInfoByName("plugin1");
H.alert(plugin.getName());
H.runPlugin(plugin); // 在新窗口中运行插件
```

## 获取插件的workspace

```
var workspace = H.getPluginWorkspace("plugin1");
```

## 获取资源

```
var resource = H.getResource("resource1");
var name = resource.getName();
var value = resource.getValue();
```


## 关于自动化配置

HHPlugin.jar 根目录有一个文件 `hhp.dtd` 如果你是用IDEA或Eclipse开发，请先安装dtd文件。

比如说域名为 http://com.huan.hhp 那么使用的时候就如下：

```
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE app-config SYSTEM "http://com.huan.hhp">
<app-config>
</app-config>
```
工具都会带有自动提示功能。
