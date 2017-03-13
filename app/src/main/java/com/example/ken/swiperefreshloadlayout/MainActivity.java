package com.example.ken.swiperefreshloadlayout;

import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLoadLayout swipeRefreshLoadLayout;
    //    private ListView listView;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    private ArrayList<Integer> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLoadLayout = (SwipeRefreshLoadLayout) findViewById(R.id.srll);
//        listView = (ListView) findViewById(R.id.listView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        for (int i = 0; i < 10; i++) {
            data.add(i);
        }
        mAdapter = new MyAdapter(this, data);
//        listView.setAdapter(mAdapter);
        mRecyclerViewAdapter = new RecyclerViewAdapter(this, data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mRecyclerViewAdapter);
        swipeRefreshLoadLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mAdapter.loadMore(5);
                mRecyclerViewAdapter.loadMore(5);
                swipeRefreshLoadLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "had loaded 5 more items.", Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLoadLayout.setOnLoadListener(new SwipeRefreshLoadLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //mAdapter.loadMore(5);
                        mRecyclerViewAdapter.loadMore(5);

                        Toast.makeText(MainActivity.this, "had loaded 5 more items.", Toast.LENGTH_SHORT).show();

                        swipeRefreshLoadLayout.setLoading(false);
                    }
                }, 1500);
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        LayoutInflater inflater;

        ArrayList<Integer> listData;

        public MyAdapter(Context context, ArrayList<Integer> listData) {
            inflater = LayoutInflater.from(context);
            this.listData = listData;
        }

        public void loadMore(int addNum) {
            int fromIndex = listData.size();
            System.out.println("fromIndex: " + fromIndex);
            for (int i = fromIndex; i < fromIndex + addNum; i++)
                listData.add(i);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            String text;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_ls, parent, false);
                holder = new ViewHolder(convertView);
//                text = String.format(holder.textView.getText().toString(), listData.get(position).toString());
//                holder.textView.setTag(text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            text = String.format("我是会更加厉害的%s号！", listData.get(position).toString());
            holder.textView.setText(text);
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView textView;

        public ViewHolder(View layout) {
            textView = (TextView) layout.findViewById(R.id.text);
        }
    }
}
