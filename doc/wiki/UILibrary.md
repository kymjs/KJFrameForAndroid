#Topology Summary
import a Activity inheritance link.Get topology all function, you can extends org.kymjs.kjframe.KJActivity(KJFragment) for your Activity(Fragment).<br>
in topology method called queue：<br>
setRootView(); <br>
@BindView <br>
initDataFromThread();（asynchronous,can do time consuming） <br>
threadDataInited();（initDataFromThread() executed just call back） <br>
initData(); <br>
initWidget(); <br>
registerBroadcast(); <br>

##Why use Topology
In the traditional wording, the view and the data is initialized are written in the onCreate () method, it will be difficult to read a few more lines of code later. You can use the topology specification code blocks, make the code easier to read.

##surprise to you
There is also a quick look binding view, and set the listener function in Topology. annotate by IOC, a line of code to bind the view and set the listener.

##
```java
		public class TabExample extends KJActivity {
			@BindView(id = R.id.bottombar_content1)
			public RadioButton mRbtn1;
			@BindView(id = R.id.bottombar_content2, click = true)
			private RadioButton mRbtn2;

			@Override
			public void setRootView() {
				setContentView(R.layout.aty_tab_example);
			}
			
			@Override
			protected void initWidget() {
				super.initWidget();
				mRbtn1.setText("widget clicked listener");
			}

			@Override
			public void widgetClick(View v) {
				super.widgetClick(v);
				switch (v.getId()) {
				case R.id.bottombar_content1:
				ViewInject.toast("clicked mRbtn1");
					break;
				case R.id.bottombar_content2:
				ViewInject.toast("clicked mRbtn2");
					break;
				}
			}
		}
```
