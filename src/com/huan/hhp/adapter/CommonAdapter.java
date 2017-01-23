package com.huan.hhp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @ClassName: CommonAdapter
 * @Description: adapter
 * @author tjy
 */
public abstract class CommonAdapter<V> extends BaseAdapter {

	private static final String TAG = CommonAdapter.class.getSimpleName();

	protected List<V> list = new ArrayList<V>(){
		@Override
		public boolean add(V object) {
			boolean flag =  super.add(object);
			if(onDataChangedListener != null){
				onDataChangedListener.onDataChanged(CommonAdapter.this);
			}
			return flag;
		}

		@Override
		public boolean addAll(Collection<? extends V> collection) {
			if(collection==null){
				return false;
			}
			boolean flag = super.addAll(collection);
			if(onDataChangedListener != null){
				onDataChangedListener.onDataChanged(CommonAdapter.this);
			}
			return flag;
		}

		@Override
		public V remove(int index) {
			V result = super.remove(index);
			if(onDataChangedListener != null){
				onDataChangedListener.onDataChanged(CommonAdapter.this);
			}
			return result;
		}

		@Override
		public boolean remove(Object object) {
			boolean result = super.remove(object);
			if(onDataChangedListener != null){
				onDataChangedListener.onDataChanged(CommonAdapter.this);
			}
			return result;
		}

		@Override
		public boolean removeAll(Collection<?> collection) {
			boolean result = super.removeAll(collection);
			if(onDataChangedListener != null){
				onDataChangedListener.onDataChanged(CommonAdapter.this);
			}
			return result;
		}

		@Override
		public void clear() {
			super.clear();
			if(onDataChangedListener != null){
				onDataChangedListener.onDataChanged(CommonAdapter.this);
			}
		}
	};
	// 绑定数据源
	protected Context context;
	public CommonAdapter(Context context) {
		this.context = context;
	}

	/**
	 * 得到显示的条目数量
	 */
	public int getCount() {
		return list.size();
	}

	/**
	 * 得到下标对应的元素
	 */
	public V getItem(int position) {
		if (position < list.size() && position >= 0)
			return list.get(position);
		return null;
	}

	/**
	 * 第一次绑定数据，或者数据源变化时使用
	 * 
	 * @param list
	 */
	public void setData(List<V> list) {
		this.list.clear();
		this.list.addAll(list);
	}
	
	/**
	 * 重新指向一个数据地址
	 * 
	 * @param list
	 */
	public void newData(List<V> list) {
		this.list = list;
	}

	/**
	 * 获取绑定数据
	 * 
	 * @param
	 */
	public List<V> getData() {
		return list;
	}

	/**
	 * 移除某个下标的数据
	 * 
	 * @param position
	 */
	public V remove(int position) {
		if (list != null && list.size() > 0) {
			return list.remove(position);
		}
		return null;
	}

	public void remove(V v) {
		if (list != null && list.size() > 0)
			list.remove(v);
	}

	/**
	 * 移除所有
	 */
	public void removeAll() {
		if (list != null)
			list.clear();
	}

	/**
	 * 移除指定所有
	 * 
	 * @param collection
	 */
	public void removeAll(Collection<?> collection) {
		if (list != null)
			list.removeAll(collection);
	}

	/**
	 * 数据最佳到最后
	 * 
	 * @param list
	 */
	public void append(List<V> list) {
		if (list != null && this.list != null)
			this.list.addAll(list);
	}

	/**
	 * 数据最佳到最后
	 * 
	 */
	public void append(V v) {
		if (v != null && this.list != null)
			this.list.add(v);
	}

	public long getItemId(int position) {
		return 0;
	}

	/**
	 * 必须重写getView方法
	 */
	public abstract View getView(int position, View convertView,
			ViewGroup parent);

	public int getPosition(Object object){
		for(int i = 0; i < list.size(); i++){
			if(object.equals(getItem(i)))
				return i;
		}
		return -1;
	}

	public interface OnDataChangedListener{
		void onDataChanged(CommonAdapter<?> adapter);
	}

	private OnDataChangedListener onDataChangedListener;

	public void setOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
		this.onDataChangedListener = onDataChangedListener;
	}
}
