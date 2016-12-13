package com.example.mql.shoppingcardemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mql.shoppingcardemo.bean.ShoppingCarProduct;
import com.example.mql.shoppingcardemo.bean.ShoppingCarStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements ShopcartExpandableListViewAdapter.CheckInterface, ShopcartExpandableListViewAdapter.DeleteInterface, View.OnClickListener,
        ShopcartExpandableListViewAdapter.ModifyCountInterface {
    private DelSlideExpandableListView expandableListView;

    private CheckBox cb_check_all;
    private TextView tv_total_price;
    private TextView tv_delete;
    private TextView tv_go_to_pay;
    private double totalPrice = 0.00;// 购买的商品总价
    private int totalCount = 0;// 购买的商品总数量
    private LinearLayout ll_shoppingCar_bottom;
    private ShopcartExpandableListViewAdapter selva;
    private List<ShoppingCarStore> groups = new ArrayList<ShoppingCarStore>();// 组元素数据列表(店铺信息)
    private Map<String, List<ShoppingCarProduct>> children = new HashMap<String, List<ShoppingCarProduct>>();// 子元素数据列表（商品信息）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        expandableListView = (DelSlideExpandableListView) findViewById(R.id.exListView);
        cb_check_all = (CheckBox) findViewById(R.id.all_chekbox);
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_go_to_pay = (TextView) findViewById(R.id.tv_go_to_pay);
        ll_shoppingCar_bottom = (LinearLayout) findViewById(R.id.ll_shoppingCar_bottom);
        virtualData();

    }
    private void initEvents() {
        selva = new ShopcartExpandableListViewAdapter(groups, children, this);
        selva.setCheckInterface(this);// 关键步骤1,设置复选框接口
        selva.setModifyCountInterface(this);// 关键步骤2,设置数量增减接口
        selva.setDeleteInterface(this);
        expandableListView.setAdapter(selva);
        for (int i = 0; i < selva.getGroupCount(); i++) {
            expandableListView.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
        }
        cb_check_all.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        tv_go_to_pay.setOnClickListener(this);

    }

    //得到购物车列表
    private void virtualData() {

        getShoppingCarList();
    }

    public void onClick(View v) {
        AlertDialog alert;
        switch (v.getId()) {
            case R.id.all_chekbox:
                doCheckAll();
                break;
            case R.id.tv_go_to_pay:
                if (totalCount == 0) {
                    Toast.makeText(this, "请选择要支付的商品", Toast.LENGTH_LONG).show();
                    return;
                }
                alert = new AlertDialog.Builder(this).create();
                alert.setTitle("操作提示");
                alert.setMessage("总计:\n" + totalCount + "种商品\n" + totalPrice + "元");
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        return;
                    }
                });
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //将选择的列表项生成订单 seletedGroups   seletedProducts
                        select(1);
                    }
                });
                alert.show();
                break;
            case R.id.tv_delete:
                if (totalCount == 0) {
                    Toast.makeText(this, "请选择要移除的商品", Toast.LENGTH_LONG).show();
                    return;
                }
                alert = new AlertDialog.Builder(this).create();
                alert.setTitle("操作提示");
                alert.setMessage("您确定要将这些商品从购物车中移除吗？");
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        return;
                    }
                });
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select(2);
                    }
                });
                alert.show();
                break;
        }
    }

    protected void select(int flags) {
        List<ShoppingCarStore> seletedGroups = new ArrayList<ShoppingCarStore>();// 以选择的组元素列表
        Map<String, List<ShoppingCarProduct>> seleteChild = new HashMap<String, List<ShoppingCarProduct>>();
        String seletedPid = null;
        String seletedPnum = null;
        //选出需要保存的,ID，num,以逗号分隔
        for (int i = 0; i < groups.size(); i++) {
            ShoppingCarStore group = groups.get(i);
            boolean flag = false;
            List<ShoppingCarProduct> Products = new ArrayList<ShoppingCarProduct>();// 以选择的的子元素列表
            List<ShoppingCarProduct> childs = children.get(group.getSid() + "");
            for (int j = 0; j < childs.size(); j++) {
                if (childs.get(j).isChoosed()) {
                    Products.add(childs.get(j));
                    if (seletedPid == null) {
                        seletedPid = childs.get(j).getPro_id()+"";
                        seletedPnum = childs.get(j).getNum() + "";
                    } else {
                        seletedPid = seletedPid + "," + childs.get(j).getPro_id();
                        seletedPnum = seletedPnum + "," + childs.get(j).getNum() + "";
                    }

                    if (!flag) {
                        seletedGroups.add(group);
                        flag = true;
                    }


                }
            }
            seleteChild.put(group.getSid() + "", Products);
        }
//        List<Map<String, List<ShoppingCarProduct>>> list = new ArrayList<Map<String, List<ShoppingCarProduct>>>();
//        list.add(seleteChild);
        if (flags == 1) {
            //结算
//            Intent intent = new Intent(getContext(), ConfirmOrderFromCarActivity.class);
//            Bundle bundle = new Bundle();
//            ShoppingCarListToOrderList shoppingCarListToOrderList = new ShoppingCarListToOrderList();
//            shoppingCarListToOrderList.setProductsId(seletedPid);
//            shoppingCarListToOrderList.setProductsNum(seletedPnum);
//            shoppingCarListToOrderList.setPrice(totalPrice);
//            shoppingCarListToOrderList.setShoppingCarProducts(seleteChild);
//            shoppingCarListToOrderList.setShoppingCarStores(seletedGroups);
//            bundle.putSerializable("shoppingCar", shoppingCarListToOrderList);
//            intent.putExtras(bundle);
//            startActivity(intent);
        } else if (flags == 2) {
            //删除
            deleteShoppingCar(seletedPid);
        }

    }

    /**
     * 删除操作<br>
     * image_1.不要边遍历边删除，容易出现数组越界的情况<br>
     * image_2.现将要删除的对象放进相应的列表容器中，待遍历完后，以removeAll的方式进行删除
     */
    protected void doDelete() {
        List<ShoppingCarStore> toBeDeleteGroups = new ArrayList<ShoppingCarStore>();// 待删除的组元素列表
        for (int i = 0; i < groups.size(); i++) {
            ShoppingCarStore group = groups.get(i);
            if (group.isChoosed()) {
                toBeDeleteGroups.add(group);
            }
            List<ShoppingCarProduct> toBeDeleteProducts = new ArrayList<ShoppingCarProduct>();// 待删除的子元素列表
            List<ShoppingCarProduct> childs = children.get(group.getSid() + "");
            for (int j = 0; j < childs.size(); j++) {
                if (childs.get(j).isChoosed()) {
                    toBeDeleteProducts.add(childs.get(j));
                }
            }
            childs.removeAll(toBeDeleteProducts);

//			children.put(group.getId(),childs);

        }

        groups.removeAll(toBeDeleteGroups);

        selva.notifyDataSetChanged();
        expandableListView.setAdapter(selva);
        for (int i = 0; i < selva.getGroupCount(); i++) {
            expandableListView.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
        }

        calculate();
    }

    /**
     * 数量增加操作<br>
     */
    public void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {

        ShoppingCarProduct product = (ShoppingCarProduct) selva.getChild(groupPosition, childPosition);
        int currentCount = product.getNum();
        currentCount++;
        product.setNum(currentCount);
        ((TextView) showCountView).setText(currentCount + "");
        selva.notifyDataSetChanged();
        calculate();
    }

    /**
     * 数量删减操作<br>
     */
    public void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        ShoppingCarProduct product = (ShoppingCarProduct) selva.getChild(groupPosition, childPosition);
        int currentCount = product.getNum();
        if (currentCount == 1)
            return;
        currentCount--;
        product.setNum(currentCount);
        ((TextView) showCountView).setText(currentCount + "");

        selva.notifyDataSetChanged();
        calculate();
    }

    /**
     * 店铺全选
     */
    public void checkGroup(int groupPosition, boolean isChecked) {
        ShoppingCarStore group = groups.get(groupPosition);
        List<ShoppingCarProduct> childs = children.get(group.getSid() + "");
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).setChoosed(isChecked);
        }
        if (isAllCheck())
            cb_check_all.setChecked(true);
        else
            cb_check_all.setChecked(false);
        selva.notifyDataSetChanged();
        calculate();
    }

    /**
     * 商品的全选
     */
    public void checkChild(int groupPosition, int childPosiTion, boolean isChecked) {
        boolean allChildSameState = true;// 判断改组下面的所有子元素是否是同一种状态
        ShoppingCarStore group = groups.get(groupPosition);
        List<ShoppingCarProduct> childs = children.get(group.getSid() + "");
        for (int i = 0; i < childs.size(); i++) {
            if (childs.get(i).isChoosed() != isChecked) {
                allChildSameState = false;
                break;
            }
        }
        if (allChildSameState) {
            group.setChoosed(isChecked);// 如果所有子元素状态相同，那么对应的组元素被设为这种统一状态
        } else {
            group.setChoosed(false);// 否则，组元素一律设置为未选中状态
        }

        if (isAllCheck())
            cb_check_all.setChecked(true);
        else
            cb_check_all.setChecked(false);
        selva.notifyDataSetChanged();
        calculate();
    }

    /**
     * 判断是否都被未被选中
     */
    private boolean isAllCheck() {

        for (ShoppingCarStore group : groups) {
            if (!group.isChoosed())
                return false;

        }

        return true;
    }

    /**
     * 全选与反选
     */
    private void doCheckAll() {
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setChoosed(cb_check_all.isChecked());
            ShoppingCarStore group = groups.get(i);
            List<ShoppingCarProduct> childs = children.get(group.getSid() + "");
            for (int j = 0; j < childs.size(); j++) {
                childs.get(j).setChoosed(cb_check_all.isChecked());
            }
        }
        selva.notifyDataSetChanged();
        calculate();
    }

    /**
     * 统计操作<br>
     * image_1.先清空全局计数器<br>
     * image_2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作<br>
     * image_3.给底部的textView进行数据填充
     */
    private void calculate() {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < groups.size(); i++) {
            ShoppingCarStore group = groups.get(i);
            List<ShoppingCarProduct> childs = children.get(group.getSid() + "");
            for (int j = 0; j < childs.size(); j++) {
                ShoppingCarProduct product = childs.get(j);
                if (product.isChoosed()) {
                    totalCount++;
                    totalPrice += product.getSprice() * product.getNum();
                    Log.e("num", product.getNum() + "no");
                    Log.e("num", childs.get(j).getNum() + "child");
                }
            }
        }
        tv_total_price.setText("￥" + totalPrice);
        tv_go_to_pay.setText("去支付(" + totalCount + ")");
    }

    /**
     * 单例商品删除操作<br>
     */
    public void delete(int groupPosition, int childPosition) {


        if (children.get(groups.get(groupPosition).getSid() + "").size() == 1) {
            groups.remove(groupPosition);
        } else {
            children.get(groups.get(groupPosition).getSid() + "").remove(childPosition);
        }

        selva.notifyDataSetChanged();
        expandableListView.setAdapter(selva);
        for (int i = 0; i < selva.getGroupCount(); i++) {
            expandableListView.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
        }
        calculate();
        Log.e("qqq", children.toString());
    }

    /**
     * 得到购物车列表<br>
     */
    public void getShoppingCarList() {

        for (int i = 0; i < 10; i++) {

            List<ShoppingCarProduct> list = new ArrayList<ShoppingCarProduct>();
            ShoppingCarStore store = new ShoppingCarStore();
            store.setSid(i);
            store.setSname("小马家的第"+i+"座马棚");
            store.setSlogo("嘿嘿假的");
//            store.setAmount(10+i*10);
            ShoppingCarProduct product = null;
            for (int j = 0; j < 10; j++) {
                product = new ShoppingCarProduct();
                product.setPro_id(i*10+j);
                product.setPro_name("小马家的第"+i+"座马棚里的第"+j+"匹小马驹");
                product.setPic_url("123");
                product.setNum(1);
                product.setSprice(10+j*10);
//                product.setPrice(Double.valueOf(object2.getString("price")));
                list.add(product);
            }
            groups.add(store);
            children.put(store.getSid() + "", list);
            Log.e("shoppingcar", groups.toString());
        }
        initEvents();
}

    public void deleteShoppingCar(final String id) {
        doDelete();
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
    }
}
