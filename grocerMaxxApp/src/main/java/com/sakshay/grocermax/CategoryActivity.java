package com.sakshay.grocermax;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sakshay.grocermax.adapters.CategorySubcategoryBean;
import com.sakshay.grocermax.api.ConnectionService;
import com.sakshay.grocermax.api.MyReceiverActions;
import com.sakshay.grocermax.bean.Simple;
import com.sakshay.grocermax.exception.GrocermaxBaseException;
import com.sakshay.grocermax.utils.AppConstants;
import com.sakshay.grocermax.utils.UrlsConstants;
import com.sakshay.grocermax.utils.UtilityMethods;

import java.util.ArrayList;


public class CategoryActivity extends BaseActivity {
//    RelativeLayout rlParent,rlChild;
    int mainCatLength = 9;
    int SubCatLength = 7;
    Integer mainCatPosition = 0;
    LinearLayout llChild[];
    LinearLayout llParent;
    ScrollView scrollView;
    int selectedIndex = 0;
    ArrayList<CategorySubcategoryBean> alSubCat;
    public ArrayList<CategorySubcategoryBean> catObj;
    private LayoutInflater inflater = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        llParent = (LinearLayout) findViewById(R.id.ll_main_layout);

        addActionsInFilter(MyReceiverActions.ALL_PRODUCTS_CATEGORY);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                catObj = (ArrayList<CategorySubcategoryBean>) bundle.getSerializable("Categories");              //main category name on left side like staples
                mainCatPosition = (int) Integer.parseInt((String) bundle.getString("maincategoryposition"));
                String strCatName = bundle.getString("CategoryName");
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        alSubCat = catObj.get(mainCatPosition).getChildren();                   //under main category [right side top category e.g. dryfruits]

        for(int i=1;i<alSubCat.size();i++){
            String str = alSubCat.get(i).getCategory();
            String str1 = alSubCat.get(i).getCategory();
        }
        if(alSubCat.size() > 0){
            mainCatLength = alSubCat.size();
        }

        LayoutInflater inflater = this.getLayoutInflater();
//        ImageView imgArr[] = new ImageView[15];
//        TextView tvName[] = new TextView[15];

        llChild = new LinearLayout[mainCatLength];
        for(int i=0 ; i < mainCatLength ; i++){
            View view = inflater.inflate(R.layout.cat_child, null);
            ImageView ivCat = (ImageView) view.findViewById(R.id.cat_icon);
            TextView tvCatName = (TextView) view.findViewById(R.id.cat_name);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
//            view.findViewById(R.id.ll_child_expandable_category);

            if (catObj.get(i).getChildren().get(0).getChildren().size() > 0) {  //sub-sub-sub category present
                tvName.setVisibility(View.VISIBLE);
            } else {
                tvName.setVisibility(View.GONE);
            }

            tvCatName.setText(alSubCat.get(i).getCategory());

            llChild[i] = (LinearLayout) view.findViewById(R.id.ll_child_expandable_category);

            if(i ==  selectedIndex){
//                llChild[i].setVisibility(View.VISIBLE);
//                updateUi(selectedIndex);
            }else{
                llChild[i].setVisibility(View.GONE);
            }

            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    (int) ViewGroup.LayoutParams.MATCH_PARENT,(int) ViewGroup.LayoutParams.WRAP_CONTENT);
            try{
                view.setTag(i);
                view.setLayoutParams(params);
                view.setOnClickListener(listener);
                llParent.addView(view);
            }catch(Exception e){
                Toast.makeText(CategoryActivity.this,"second",Toast.LENGTH_SHORT).show();
            }
        }
        initHeader(findViewById(R.id.app_bar_header), true, "Category");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        initHeader(findViewById(R.id.app_bar_header), false, "Category");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try{
                int position = (Integer) view.getTag();
                Toast.makeText(CategoryActivity.this,"clicked on"+position,Toast.LENGTH_SHORT).show();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                TextView cat_name = (TextView) view.findViewById(R.id.cat_name);                           //get view of currently selected main category
//                cat_name.setTextColor(getResources().getColor(R.color.main_cat_text_selected));              //selected text color white of main category
//                tvSelctionCat = cat_name;                         //assign currently selected view to previously selected holder

                for(int i=0 ; i < mainCatLength ; i++){
                    llChild[i].setVisibility(View.GONE);
                }
//                countSubCat = 12;
//                int childheight = 0;
//                for(int k=1;k<=countSubCat;k+=3){
//                    childheight += 80;
//                }
//                llChild[position].getLayoutParams().height = childheight;
//                llChild[position].setVisibility(View.VISIBLE);
                selectedIndex = position;

                boolean expandStatus = false;
                for (int i = 0; i < catObj.get(mainCatPosition).getChildren().get(selectedIndex).getChildren().size(); i++) {
                    if (catObj.get(mainCatPosition).getChildren().get(selectedIndex).getChildren().get(i).getChildren().size() > 0) {
                        expandStatus = true;                               //means any one has more child after expandable,so will not move on to next screen and expand itself.
                        break;
                     }
                }

                SubCatLength = catObj.get(mainCatPosition).getChildren().get(selectedIndex).getChildren().size();

                if(expandStatus && catObj.get(mainCatPosition).getChildren().get(selectedIndex).getIsActive().equals("1")) {
                    updateUi(selectedIndex);
                }else{
                    showDialog();
//                    String url = UrlsConstants.GET_ALL_PRODUCTS_OF_CATEGORY + catObj.get(position).getChildren().get(groupPosition).getCategoryId();
                    String url = UrlsConstants.GET_ALL_PRODUCTS_OF_CATEGORY + catObj.get(mainCatPosition).getChildren().get(selectedIndex).getCategoryId();
                    myApi.reqAllProductsCategory(url);
                }
            }catch(Exception e){
                new GrocermaxBaseException("HomeScreen","listener",e.getMessage(),GrocermaxBaseException.EXCEPTION,"nodetail");
            }
        }
    };

    	View.OnClickListener listenerchild = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			try {

				int pos = (Integer) view.getTag();
    			UtilityMethods.customToast(String.valueOf(pos) + "====", MyApplication.getInstance());
//
//				for (int i = 0; i < date_list.size(); i++) {
//					System.out.println("====text====" + i);
////				imgDateSlot[i].setBackgroundResource(R.drawable.delivery_slot_date_unselected_btn);
//					imgDateSlot[i].setImageResource(R.drawable.uncheck_pay);
//
//					tvDateSlot[i].setBackgroundResource(R.drawable.delivery_slot_date_unselected_btn);
//					tvDateSlot[i].setBackgroundColor(getResources().getColor(R.color.delivery_slot_unselected_color));
//					tvDateSlot[i].setTextColor(getResources().getColor(R.color.delivery_slot_text_unselected_color));
//				}
//
//				imgDateSlot[pos].setImageResource(R.drawable.check_pay);
//				tvDateSlot[pos].setTextColor(getResources().getColor(R.color.delivery_slot_text_selected_color));
//				tvDateSlot[pos].setBackgroundResource(R.drawable.delivery_slot_selected_btn);
////			((LinearLayout)view).setBackgroundColor(getResources().getColor(R.color.red));
//				date = date_list.get(pos);
//				time = "";
//				setTimeSlotting(date);
//				GridViewAdapter.selectedPosition = pos;
//
//				btn1TimeSlot.setBackgroundResource(R.drawable.uncheck_pay);
//				btn2TimeSlot.setBackgroundResource(R.drawable.uncheck_pay);
//				btn3TimeSlot.setBackgroundResource(R.drawable.uncheck_pay);
//				btn4TimeSlot.setBackgroundResource(R.drawable.uncheck_pay);
//
//				tvFirstTime.setBackgroundResource(R.color.delivery_slot_unselected_color);
//				tvSecondTime.setBackgroundResource(R.color.delivery_slot_unselected_color);
//				tvThirdTime.setBackgroundResource(R.color.delivery_slot_unselected_color);
//				tvFourthTime.setBackgroundResource(R.color.delivery_slot_unselected_color);
//
//				tvFirstTime.setTextColor(getResources().getColor(R.color.delivery_slot_text_unselected_color));
//				tvSecondTime.setTextColor(getResources().getColor(R.color.delivery_slot_text_unselected_color));
//				tvThirdTime.setTextColor(getResources().getColor(R.color.delivery_slot_text_unselected_color));
//				tvFourthTime.setTextColor(getResources().getColor(R.color.delivery_slot_text_unselected_color));
////			//scroll_view.scrollTo(0, 0);
			}catch(Exception e){
				new GrocermaxBaseException("ChooseAddress","listener",e.getMessage(),GrocermaxBaseException.EXCEPTION,"nodetail");
			}
		}
	};

    private void updateUi(int selectedIndex){
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                (int) ViewGroup.LayoutParams.MATCH_PARENT,(int) ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = this.getLayoutInflater();
        int tempInt = -1;

        ArrayList<CategorySubcategoryBean> alSubSubChild = catObj.get(mainCatPosition).getChildren().get(selectedIndex).getChildren();
//        if(i == selectedIndex){
            llChild[selectedIndex].setVisibility(View.VISIBLE);
            int childheight = 0;
            for(int k=1;k<=SubCatLength;k+=3){
                childheight += 80;
            }
            llChild[selectedIndex].getLayoutParams().height = childheight;

            LinearLayout llMain;
            if(SubCatLength == 1){
                View subView2 = inflater.inflate(R.layout.catsubchild, null);
                llMain = (LinearLayout) subView2.findViewById(R.id.ll_main);                   //main view

                TextView tv11 = (TextView) subView2.findViewById(R.id.tv_first);

                tempInt++;
//                tv11.setText("levelling");
                tv11.setText(alSubSubChild.get(tempInt).getCategory());
                tv11.setTag(tempInt);
                tv11.setVisibility(View.VISIBLE);
                tv11.setOnClickListener(listenerchild);

                llChild[selectedIndex].addView(llMain);
            }else if(SubCatLength == 2){
                View subView2 = inflater.inflate(R.layout.catsubchild, null);
                llMain = (LinearLayout) subView2.findViewById(R.id.ll_main);                   //main view

                TextView tv11 = (TextView) subView2.findViewById(R.id.tv_first);
                TextView tv22 = (TextView) subView2.findViewById(R.id.tv_second);

                tempInt++;
//                tv11.setText("level 1");
                tv11.setText(alSubSubChild.get(tempInt).getCategory());
                tv11.setTag(tempInt);
                tv11.setVisibility(View.VISIBLE);
                tv11.setOnClickListener(listenerchild);

                tempInt++;
//                tv22.setText("level 2");
                tv22.setText(alSubSubChild.get(tempInt).getCategory());
                tv22.setTag(tempInt);
                tv22.setVisibility(View.VISIBLE);
                tv22.setOnClickListener(listenerchild);

                llChild[selectedIndex].addView(llMain);
            }else if(SubCatLength >= 3){
                for(int k=1 ; k<=SubCatLength / 3 ;k++){

                    View subView = inflater.inflate(R.layout.catsubchild, null);
                    llMain = (LinearLayout) subView.findViewById(R.id.ll_main);                   //main view

                    TextView tv1 = (TextView) subView.findViewById(R.id.tv_first);
                    TextView tv2 = (TextView) subView.findViewById(R.id.tv_second);
                    TextView tv3 = (TextView) subView.findViewById(R.id.tv_third);

                    tempInt++;
//                    tv1.setText("starting");
                    tv1.setText(alSubSubChild.get(tempInt).getCategory());
                    tv1.setTag(tempInt);
                    tv1.setVisibility(View.VISIBLE);
                    tv1.setOnClickListener(listenerchild);

                    tempInt++;
//                    tv2.setText("middling");
                    tv2.setText(alSubSubChild.get(tempInt).getCategory());
                    tv2.setTag(tempInt);
                    tv2.setVisibility(View.VISIBLE);
                    tv2.setOnClickListener(listenerchild);

                    tempInt++;
//                    tv3.setText("ending");
                    tv3.setText(alSubSubChild.get(tempInt).getCategory());
                    tv3.setTag(tempInt);
                    tv3.setVisibility(View.VISIBLE);
                    tv3.setOnClickListener(listenerchild);

                    llChild[selectedIndex].addView(llMain);

                    if (SubCatLength % 3 != 0) {                    //if records are not divisible by 3.
                        if (tempInt + 2 == SubCatLength) {          //when records are of 4,7,10 etc.    //1 view in next row.
                            View subView2 = inflater.inflate(R.layout.catsubchild, null);
                            llMain = (LinearLayout) subView2.findViewById(R.id.ll_main);                   //main view

                            TextView tv11 = (TextView) subView2.findViewById(R.id.tv_first);

                            tempInt++;
//                            tv11.setText("levelling");
                            tv11.setText(alSubSubChild.get(tempInt).getCategory());
                            tv11.setTag(tempInt);
                            tv11.setVisibility(View.VISIBLE);
                            tv11.setOnClickListener(listenerchild);

                            llChild[selectedIndex].addView(llMain);

                        }else if(tempInt + 3 == SubCatLength){      //when records are of 5,8,11 etc.    //2 view in next row.
                            View subView2 = inflater.inflate(R.layout.catsubchild, null);
                            llMain = (LinearLayout) subView2.findViewById(R.id.ll_main);                   //main view

                            TextView tv11 = (TextView) subView2.findViewById(R.id.tv_first);
                            TextView tv22 = (TextView) subView2.findViewById(R.id.tv_second);

                            tempInt++;
//                            tv11.setText("level 1");
                            tv11.setText(alSubSubChild.get(tempInt).getCategory());
                            tv11.setTag(tempInt);
                            tv11.setVisibility(View.VISIBLE);
                            tv11.setOnClickListener(listenerchild);

                            tempInt++;
//                            tv22.setText("level 2");
                            tv22.setText(alSubSubChild.get(tempInt).getCategory());
                            tv22.setTag(tempInt);
                            tv22.setVisibility(View.VISIBLE);
                            tv22.setOnClickListener(listenerchild);

                            llChild[selectedIndex].addView(llMain);
                        }

                    }
                }
            }

//        }
    }

    @Override
    public void OnResponse(Bundle bundle) {
        String action = bundle.getString("ACTION");
        if (action.equals(MyReceiverActions.ALL_PRODUCTS_CATEGORY)) {
//			group_click = 0;

            Simple responseBean = (Simple) bundle.getSerializable(ConnectionService.RESPONSE);
            if (responseBean.getFlag().equalsIgnoreCase("1")) {
                Intent call = new Intent(CategoryActivity.this, CategoryTabs.class);
                Bundle call_bundle = new Bundle();
                call_bundle.putSerializable("PRODUCTDATA", responseBean);
                call.putExtras(call_bundle);
                startActivity(call);
            } else {
                UtilityMethods.customToast(AppConstants.ToastConstant.NO_RESULT_FOUND, mContext);
            }
        }

    }
}