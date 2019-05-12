package com.example.lenovo.demo;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Data> dataList;
    private Adapter adapter;
    private OnClickListener mlistener;
    private int count = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);
        dataList = new ArrayList<>();
        initView();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendTrueReqest();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusableInTouchMode(false);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
              adapter.setLoadState(adapter.LOADING);
                count += 10;
                sendTrueReqest();
            adapter.setLoadState(adapter.LOADING_END);
            }
        });
        sendTrueReqest();

    }


    void initView() {
        recyclerView = findViewById(R.id.rec);
        swipeRefreshLayout = findViewById(R.id.refrsh);

    }

    void sendTrueReqest() {
        HttpUtil.senHttpReqest("http://gank.io/api/xiandu/data/id/appinn/count/" + count + "/page/1 ", new CallBackListener() {
            @Override
            public void onFinish(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Data data = new Data();
                                Site site = new Site();
                                data.setSite(site);
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String picUrl = jsonObject1.getString("cover");
                                data.setCover(picUrl);
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("site");
                                String text1 = jsonObject2.getString("cat_cn");
                                String text2 = jsonObject2.getString("desc");
                                data.getSite().setCat_cn(text1);
                                data.getSite().setDesc(text2);
                                dataList.add(data);
                                Log.d(TAG, "run: ");
                            }
                            adapter = new Adapter(dataList, MainActivity.this);
                            Log.d(TAG, dataList.get(1).getCover());
                            recyclerView.setFocusable(false);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            adapter.SetOnItemCickListener(new OnClickListener() {
                                @Override
                                public void onClick(int position) {
                                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onLongClick(int position) {

                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "run: ", e);
                            e.printStackTrace();
                        }
                    }

                });
            }

            @Override
            public void onFail(Exception e) {

            }
        });

    }
}
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                httpUtils = new HttpUtils<Data>(dataList, "http://gank.io/api/xiandu/data/id/appinn/count/10/page/1 ", new Tools() {
//                    @Override
//                    public void onSuccess(List list) {
//                        dataList = list;
//                        adapter = new Adapter(dataList, MainActivity.this, new Listener() {
//                            @Override
//                            public void onClick(int position) {
//
//                            }
//
//                            @Override
//                            public void onLongClick(int position) {
//
//                            }
//                        }, new Loader(MainActivity.this));
//                        recyclerView.setAdapter(adapter);
//                    }
//
//                    @Override
//                    public void onFail() {
//                        //  Toast.makeText(MainActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public List handleJosnDataToList(String josnData) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(josnData);
//                            JSONArray jsonArray = jsonObject.getJSONArray("results");
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                Data data = new Data();
//                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                                String picUrl = jsonObject1.getString("cover");
//                                data.setCover(picUrl);
//                                JSONObject jsonObject2 = jsonObject1.getJSONObject("site");
//                                String text1 = jsonObject2.getString("cat_cn");
//                                String text2 = jsonObject2.getString("desc");
//                                data.getCite().setCat_cn(text1);
//                                data.getCite().setDesc(text2);
//                                dataList.add(data);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        return dataList;
//                    }
//                });
//                httpUtils.getTargetList();
//            }
//        }).start();
//