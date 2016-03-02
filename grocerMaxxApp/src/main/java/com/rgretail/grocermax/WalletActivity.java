package com.rgretail.grocermax;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dq.rocq.RocqAnalytics;
import com.flurry.android.FlurryAgent;
import com.rgretail.grocermax.api.ConnectionService;
import com.rgretail.grocermax.api.MyReceiverActions;
import com.rgretail.grocermax.exception.GrocermaxBaseException;
import com.rgretail.grocermax.preference.MySharedPrefs;
import com.rgretail.grocermax.utils.UrlsConstants;

import org.json.JSONObject;


public class WalletActivity extends BaseActivity {

    TextView tv_walletAmount,tv_transactions,tv_share,tv_msg;
    ImageView icon_header_back;
    double amount=0.00;
    String shareSubject="";
    String shareBody="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

       // initHeader(findViewById(R.id.header), true, null);
        addActionsInFilter(MyReceiverActions.WALLET_INFO);
        initView();
        icon_header_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(WalletActivity.this,WalletTransaction.class);
                i.putExtra("wallet_amount",amount);
                startActivity(i);

             /*  CitrusFlowManager.initCitrusConfig("q4qz4sa1wq-signup",
                       "915112088057be17d992162f88eeb7f9", "q4qz4sa1wq-signin",
                       "dca4f2179ade2454aaee0194be186774",
                       getResources().getColor(R.color.white), WalletActivity.this,
                       Environment.SANDBOX, "q4qz4sa1wq", "http://staging.grocermax.com/citrus.php", "http://staging.grocermax.com/returncitrus.php");*/


                /*CitrusFlowManager.initCitrusConfig("8x5hn2kbpc-signup","2b591f683aa3cf1426fd2a1103c5d845","8x5hn2kbpc-signin","2996366165262aeb051533c6f7a78230",
                                                   getResources().getColor(R.color.citrus_white), WalletActivity.this, Environment.SANDBOX, "8x5hn2kbpc", "https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php", "https://salty-plateau-1529.herokuapp.com/redirectURL.sandbox.php");
*/
                /*CitrusFlowManager.startShoppingFlow(WalletActivity.this,"grocermaxtesting@gmail.com","8888888888","1.00");*/



            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                shareSubject = MySharedPrefs.INSTANCE.getFirstName()+" Sent You Rs. 100 GrocerMax Coupon";
//                shareBody="Hi,<br/><br/>I'm delighted with honest quality products and deals that GrocerMax.com offers. I believe you'll love them too. " +
//                        "Here's a Rs. 100 coupon just for you that you should use in your 1st purchase on GrocerMax.<br/><br/>Coupon Code - <b>APP100</b><br/><br/>"+
//                        "GrocerMax bundles groceries the way you need it, at the prices you always look for. " +
//                        "<a href='https://grocermax.com/noida/hotoffer?ref=hotoffer'>Please sample their hot offers</a> ,choose your pick, and do redeem the coupon.<br/><br/>Lovingly,<br/>"+MySharedPrefs.INSTANCE.getFirstName();

                if(!shareBody.equals("")){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(shareBody));
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }else{
                    Toast.makeText(WalletActivity.this,"Nothing to share",Toast.LENGTH_SHORT).show();
                }

            }
        });




        try {
            showDialog();
            myApi.reqWallet(UrlsConstants.WALLET_INFO_URL + MySharedPrefs.INSTANCE.getUserId());
        } catch (Exception e) {
            new GrocermaxBaseException("WalletActivity","onCreate",e.getMessage(), GrocermaxBaseException.EXCEPTION, "error in getting wallet amount");
        }
    }

    public void initView(){
        tv_walletAmount=(TextView)findViewById(R.id.tv_walletAmount);
        tv_transactions=(TextView)findViewById(R.id.tv_transactions);
        icon_header_back=(ImageView)findViewById(R.id.icon_header_back);
        tv_share=(TextView)findViewById(R.id.tv_share);
        tv_msg=(TextView)findViewById(R.id.tv_msg);
    }

    @Override
    public void OnResponse(Bundle bundle) {

        dismissDialog();
        if (bundle.getString("ACTION").equals(MyReceiverActions.WALLET_INFO)) {
            try
            {
                String walletResponse = (String) bundle.getSerializable(ConnectionService.RESPONSE);
                //System.out.println("reorder Response = "+quoteResponse);
                JSONObject reOrderJSON=new JSONObject(walletResponse);
                tv_msg.setText(Html.fromHtml(reOrderJSON.getString("Massege").replace("Rs. ",getResources().getString(R.string.Rs))));

                if(!reOrderJSON.optString("Mail_Subject").equals("") && !reOrderJSON.optString("Mail_Body").equals("")){
                shareSubject=MySharedPrefs.INSTANCE.getFirstName()+" "+reOrderJSON.optString("Mail_Subject");
                shareBody=reOrderJSON.optString("Mail_Body")+""+MySharedPrefs.INSTANCE.getFirstName();
                }

                if(reOrderJSON.getInt("flag")==1){
                    amount=reOrderJSON.getDouble("Balance");
                    if(amount==0)
                     tv_walletAmount.setText("0.00");
                    else
                     tv_walletAmount.setText(String.format("%.2f",amount));
                }else{
                    tv_walletAmount.setText("0.00");
                }
            }catch(Exception e)
            {
                new GrocermaxBaseException("WalletActivity","OnResponse",e.getMessage(),GrocermaxBaseException.EXCEPTION,"error in wallet");
            }
        }

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        try{
//			EasyTracker.getInstance(this).activityStart(this);
            FlurryAgent.onStartSession(this, getResources().getString(R.string.flurry_api_key));
            FlurryAgent.onPageView();         //Use onPageView to report page view count.
        }catch(Exception e){
        }
        /*screen tracking using rocq*/
        try {
            RocqAnalytics.initialize(this);
            RocqAnalytics.startScreen(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
       /*------------------------------*/
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        try{
//			EasyTracker.getInstance(this).activityStop(this);
            FlurryAgent.onEndSession(this);
        }catch(Exception e){}
        try {
            RocqAnalytics.stopScreen(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("request code="+requestCode);
        System.out.println("result code="+resultCode);

    }
}
