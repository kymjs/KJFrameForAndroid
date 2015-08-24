/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed import org.kymjs.blog.ui.widget.listview.PullToRefreshBase.OnRefreshListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.blog.ui.widget.listview;

import org.kymjs.blog.ui.widget.listview.PullToRefreshBase.OnRefreshListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 定义了拉动刷新的接口
 * 
 * @param <T>
 * @author kymjs (https://github.com/kymjs)
 * @since 2015-3
 */
public interface IPullToRefresh<T extends View> {

    /**
     * 设置当前下拉刷新是否可用
     * 
     * @param pullRefreshEnabled
     *            true表示可用，false表示不可用
     */
    public void setPullRefreshEnabled(boolean pullRefreshEnabled);

    /**
     * 设置当前上拉加载更多是否可用
     * 
     * @param pullLoadEnabled
     *            true表示可用，false表示不可用
     */
    public void setPullLoadEnabled(boolean pullLoadEnabled);

    /**
     * 滑动到底部是否自动加载更多数据
     * 
     * @param scrollLoadEnabled
     *            如果这个值为true的话，那么上拉加载更多的功能将会禁用
     */
    public void setScrollLoadEnabled(boolean scrollLoadEnabled);

    /**
     * 判断当前下拉刷新是否可用
     * 
     * @return true如果可用，false不可用
     */
    public boolean isPullRefreshEnabled();

    /**
     * 判断上拉加载是否可用
     * 
     * @return true可用，false不可用
     */
    public boolean isPullLoadEnabled();

    /**
     * 滑动到底部加载是否可用
     * 
     * @return true可用，否则不可用
     */
    public boolean isScrollLoadEnabled();

    /**
     * 设置刷新的监听器
     * 
     * @param refreshListener
     *            监听器对象
     */
    public void setOnRefreshListener(OnRefreshListener<T> refreshListener);

    /**
     * 结束下拉刷新
     */
    public void onPullDownRefreshComplete();

    /**
     * 结束上拉加载更多
     */
    public void onPullUpRefreshComplete();

    /**
     * 得到可刷新的View对象
     * 
     * @return 返回调用{@link #createRefreshView(Context, AttributeSet)} 方法返回的对象
     */
    public T getRefreshView();

    /**
     * 得到Header布局对象
     * 
     * @return Header布局对象
     */
    public LoadingLayout getHeaderLoadingLayout();

    /**
     * 得到Footer布局对象
     * 
     * @return Footer布局对象
     */
    public LoadingLayout getFooterLoadingLayout();

    /**
     * 设置最后更新的时间文本
     * 
     * @param label
     *            文本
     */
    public void setLastUpdatedLabel(CharSequence label);
}
