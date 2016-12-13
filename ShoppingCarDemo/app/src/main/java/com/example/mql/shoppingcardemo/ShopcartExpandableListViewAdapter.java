package com.example.mql.shoppingcardemo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mql.shoppingcardemo.bean.ShoppingCarProduct;
import com.example.mql.shoppingcardemo.bean.ShoppingCarStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;





public class ShopcartExpandableListViewAdapter extends BaseExpandableListAdapter {
    private List<ShoppingCarStore> groups;
    private Map<String, List<ShoppingCarProduct>> children;
    private Context context;
    //HashMap<Integer, View> groupMap = new HashMap<Integer, View>();
    //HashMap<Integer, View> childrenMap = new HashMap<Integer, View>();
    private CheckInterface checkInterface;
    private DeleteInterface deleteInterface;
    private ModifyCountInterface modifyCountInterface;
    private int[] image={R.mipmap.pro1,R.mipmap.pro2,R.mipmap.pro3,R.mipmap.pro4,R.mipmap.pro5,R.mipmap.pro6,R.mipmap.pro7,R.mipmap.pro8};
    /**
     * 构造函数
     *
     * @param groups   组元素列表
     * @param children 子元素列表
     * @param context
     */
    public ShopcartExpandableListViewAdapter(List<ShoppingCarStore> groups, Map<String, List<ShoppingCarProduct>> children, Context context) {
        super();
        this.groups = groups;
        this.children = children;
        this.context = context;
    }

    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    public void setDeleteInterface(DeleteInterface deleteInterface) {
        this.deleteInterface = deleteInterface;
    }

    public void setModifyCountInterface(ModifyCountInterface modifyCountInterface) {
        this.modifyCountInterface = modifyCountInterface;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String groupId = groups.get(groupPosition).getSid() + "";
        return children.get(groupId).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        List<ShoppingCarProduct> child = children.get(groups.get(groupPosition).getSid() + "");

        return child.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groups.get(groupPosition).getSid();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return children.get(groups.get(groupPosition).getSid() + "").get(childPosition).getPro_id();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupHolder gholder;
        if (convertView == null) {
            gholder = new GroupHolder();
            convertView = View.inflate(context, R.layout.shopcar_group_item, null);
            gholder.cb_checkStoreAll = (CheckBox) convertView.findViewById(R.id.cb_checkStoreAll);
            gholder.tv_storeName = (TextView) convertView.findViewById(R.id.tv_storeName);
            gholder.riv_store = (ImageView) convertView.findViewById(R.id.riv_store);
            //groupMap.put(groupPosition, convertView);
            convertView.setTag(gholder);
        } else {
            // convertView = groupMap.get(groupPosition);
            gholder = (GroupHolder) convertView.getTag();
        }
        final ShoppingCarStore group = (ShoppingCarStore) getGroup(groupPosition);
        if (group != null) {
            gholder.tv_storeName.setText(group.getSname());
            gholder.cb_checkStoreAll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v)

                {
                    group.setChoosed(((CheckBox) v).isChecked());
                    checkInterface.checkGroup(groupPosition, ((CheckBox) v).isChecked());// 暴露组选接口
                }
            });
            gholder.cb_checkStoreAll.setChecked(group.isChoosed());
            gholder.riv_store.setImageResource(image[groupPosition%8]);

        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ChildHolder cholder;
        if (convertView == null) {
            cholder = new ChildHolder();
            convertView = View.inflate(context, R.layout.shopcar_child_item, null);
            cholder.cb_product = (CheckBox) convertView.findViewById(R.id.cb_product);
            cholder.iv_shopcar_product = (ImageView) convertView.findViewById(R.id.iv_shopcar_product);
            cholder.tv_delete = (TextView) convertView.findViewById(R.id.delete_action);
            cholder.tv_product_name = (TextView) convertView.findViewById(R.id.tv_product_name);
            cholder.tv_product_price = (TextView) convertView.findViewById(R.id.tv_product_price);
            cholder.iv_increase = (Button) convertView.findViewById(R.id.tv_add);
            cholder.iv_decrease = (Button) convertView.findViewById(R.id.tv_reduce);
            cholder.tv_product_number = (TextView) convertView.findViewById(R.id.tv_product_number);
            // childrenMap.put(groupPosition, convertView);
            convertView.setTag(cholder);
        } else {
            cholder = (ChildHolder) convertView.getTag();
        }
        final ShoppingCarProduct product = (ShoppingCarProduct) getChild(groupPosition, childPosition);
        Log.e("product", product.toString());
        if (product != null) {
            cholder.tv_product_name.setText(product.getPro_name());
//			cholder.tv_product_attribute.setText(product.getDesc());
            cholder.tv_product_price.setText("￥" + product.getSprice() + "");
            cholder.tv_product_number.setText(product.getNum() + "");
            cholder.cb_product.setChecked(product.isChoosed());
            cholder.iv_shopcar_product.setImageResource(image[groupPosition%8]);

            cholder.cb_product.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    product.setChoosed(((CheckBox) v).isChecked());
                    cholder.cb_product.setChecked(((CheckBox) v).isChecked());
                    checkInterface.checkChild(groupPosition, childPosition, ((CheckBox) v).isChecked());// 暴露子选接口
                }
            });
            cholder.tv_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteShoppingCar(children.get(groups.get(groupPosition).getSid() + "").get(childPosition).getPro_id()+"",groupPosition,childPosition);


                }
            });
            }


        cholder.iv_increase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyCountInterface.doIncrease(groupPosition, childPosition, cholder.tv_product_number, cholder.cb_product.isChecked());// 暴露增加接口
            }
        });
        cholder.iv_decrease.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyCountInterface.doDecrease(groupPosition, childPosition, cholder.tv_product_number, cholder.cb_product.isChecked());// 暴露删减接口
            }
        });
        cholder.iv_shopcar_product.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(context, Pro_DetailActivity.class);
//                intent.putExtra("pro_id",Integer.valueOf(product.getPro_id()));
//
//                context.startActivity(intent);
            }
        });
//		}
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * 组元素绑定器
     */
    private class GroupHolder {
        CheckBox cb_checkStoreAll;
        TextView tv_storeName;
        ImageView riv_store;
    }

    /**
     * 子元素绑定器
     */
    private class ChildHolder {
        CheckBox cb_product;
        ImageView iv_shopcar_product;
        TextView tv_product_name;

        TextView tv_product_price;
        Button iv_increase;
        TextView tv_product_number;
        Button iv_decrease;
        TextView tv_delete;
    }

    /**
     * 复选框接口
     */
    public interface CheckInterface {
        /**
         * 组选框状态改变触发的事件
         *
         * @param groupPosition 组元素位置
         * @param isChecked     组元素选中与否
         */
        public void checkGroup(int groupPosition, boolean isChecked);

        /**
         * 子选框状态改变时触发的事件
         *
         * @param groupPosition 组元素位置
         * @param childPosition 子元素位置
         * @param isChecked     子元素选中与否
         */
        public void checkChild(int groupPosition, int childPosition, boolean isChecked);
    }

    public interface DeleteInterface {


        /**
         * 子选框状态改变时触发的事件
         *
         * @param groupPosition 组元素位置
         * @param childPosition 子元素位置
         */
        public void delete(int groupPosition, int childPosition);
    }

    /**
     * 改变数量的接口
     */
    public interface ModifyCountInterface {
        /**
         * 增加操作
         *
         * @param groupPosition 组元素位置
         * @param childPosition 子元素位置
         * @param showCountView 用于展示变化后数量的View
         * @param isChecked     子元素选中与否
         */
        public void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked);

        /**
         * 删减操作
         *
         * @param groupPosition 组元素位置
         * @param childPosition 子元素位置
         * @param showCountView 用于展示变化后数量的View
         * @param isChecked     子元素选中与否
         */
        public void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked);
    }
    public void deleteShoppingCar(final String id, final int groupPosition, final int childPosition) {

        deleteInterface.delete(groupPosition, childPosition);
        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();

    }
}
