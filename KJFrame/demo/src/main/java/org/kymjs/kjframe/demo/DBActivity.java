package org.kymjs.kjframe.demo;

import java.util.List;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.demo.bean.User;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.StringUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DBActivity extends KJActivity {
    @BindView(id = R.id.button1, click = true)
    private Button mBtn1;
    @BindView(id = R.id.button2, click = true)
    private Button mBtn2;
    @BindView(id = R.id.button3, click = true)
    private Button mBtn3;
    @BindView(id = R.id.button4, click = true)
    private Button mBtn4;
    @BindView(id = R.id.editText1)
    private EditText mEt;
    @BindView(id = R.id.textView1)
    private TextView mTv;

    private KJDB kjdb;
    private User javaBean;

    @Override
    public void setRootView() {
        setContentView(R.layout.example_db);
    }

    @Override
    public void initData() {
        super.initData();
        kjdb = KJDB.create();
        javaBean = new User();
        javaBean.setAge(20);
        javaBean.setName("姓名一号");
        kjdb.save(javaBean);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mBtn1.setText("增");
        mBtn2.setText("删");
        mBtn3.setText("改");
        mBtn4.setText("查");
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button1:
            add();
            break;
        case R.id.button2:
            delete();
            break;
        case R.id.button3:
            update();
            break;
        case R.id.button4:
            queryAll();
            break;
        }
    }

    private void add() {
        if (StringUtils.isEmpty(mEt.getText().toString())) {
            ViewInject.toast("输入些数据再添加吧");
        } else {
            User javaBean2 = new User();
            javaBean2.setAge(21);
            javaBean2.setName(mEt.getText().toString());
            kjdb.save(javaBean2);
            ViewInject.toast("已添加，请查看");
            mEt.setText(null);
        }
    }

    private void delete() {
        // 可以直接这样删掉，这里我们用sql语句的方法
        // kjdb.delete(javaBean);
        // 删除所有age为20，name为“姓名一号”的数据
        kjdb.deleteByWhere(User.class, "age=20 and name='姓名一号'");
    }

    private void update() {
        // 可以直接将一个修改了数据的JavaBean作为参数传入（此处的修改不能是主键id）
        // kjdb.update(javaBean);
        // 修改全部age为21的数据为JavaBean的内容
        kjdb.update(javaBean, "age=21");
    }

    private void queryAll() {
        // 这里是查找全部数据的
        List<User> datas = kjdb.findAll(User.class);
        StringBuilder str = new StringBuilder();
        for (User u : datas) {
            str.append(u.getName()).append("----");
        }
        mTv.setText(str);
    }

    private void query() {
        // 查找age为21的数据
        List<User> datas = kjdb.findAllByWhere(User.class, "age=21");
        StringBuilder str = new StringBuilder();
        for (User u : datas) {
            str.append(u.getName()).append("----");
        }
        mTv.setText(str);
    }
}
