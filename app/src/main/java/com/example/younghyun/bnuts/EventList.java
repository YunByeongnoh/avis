package com.example.younghyun.bnuts;

import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;


import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListAdapter;
import android.widget.Toast;

/**
 * Created by user on 16. 5. 17..
 */
public class EventList extends Activity{
    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private ArrayList<String> mChildListContent = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventlist);

        setLayout();

        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();
        mChildListContent = new ArrayList<String>();

        mGroupList.add("1월");
        mGroupList.add("2월");
        mGroupList.add("3월");
        mGroupList.add("4월");
        mGroupList.add("5월");
        mGroupList.add("6월");
        mGroupList.add("7월");
        mGroupList.add("8월");
        mGroupList.add("9월");
        mGroupList.add("10월");
        mGroupList.add("11월");
        mGroupList.add("12월");




        mChildListContent.add("1");
        mChildListContent.add("2");
        mChildListContent.add("3");

        mChildList.add(mChildListContent);
        mChildList.add(mChildListContent);
        mChildList.add(mChildListContent);

        mListView.setAdapter( new BaseExpandableAdapter(this, mGroupList, mChildList));

        // 그룹 클릭 했을 경우 이벤트
        mListView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Toast.makeText(getApplicationContext(), "g click = " + groupPosition,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // 차일드 클릭 했을 경우 이벤트
        mListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), "c click = " + childPosition,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // 그룹이 닫힐 경우 이벤트
        mListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(), "g Collapse = " + groupPosition,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 그룹이 열릴 경우 이벤트
        mListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(), "g Expand = " + groupPosition,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * Layout
     */
    private ExpandableListView mListView;

    private void setLayout(){
        mListView = (ExpandableListView) findViewById(R.id.elv_list);
    }
}




