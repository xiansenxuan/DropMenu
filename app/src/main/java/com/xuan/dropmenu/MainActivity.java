package com.xuan.dropmenu;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String[] tabItems={"类型","品牌","价格","更多"};
    private DropMenuAdapter dropMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DropMenu drop_menu=findViewById(R.id.drop_menu);


        dropMenuAdapter = new DropMenuAdapter();
        drop_menu.setAdapter(dropMenuAdapter);
    }

    public void contentClick(View view) {
        Toast.makeText(this,"Hello World! onClick",Toast.LENGTH_SHORT).show();
    }

    private class DropMenuAdapter extends BaseDropMenuAdapter{

        @Override
        public int getCount() {
            return tabItems.length;
        }

        @Override
        public View getTabView(final int position, final ViewGroup parent) {
            TextView view=new TextView(parent.getContext());
            view.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.colorAccent));
            view.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            view.setText(tabItems[position]);

            return view;
        }

        @Override
        public View getContentMenuView(final int position, final ViewGroup parent) {
            TextView view=new TextView(parent.getContext());
            view.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.colorAccent));
            view.setText(tabItems[position]);
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setGravity(Gravity.CENTER);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropMenuAdapter.closeMenu();
                    Toast.makeText(parent.getContext(),"ContentMenuView onClick" +tabItems[position],Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }

        @Override
        public void defaultTabView(ViewGroup parent,View tabView) {
            ((TextView)tabView).setTextColor(ContextCompat.getColor(parent.getContext(),R.color.colorAccent));
        }

        @Override
        public void selectTabView(ViewGroup parent,View tabView) {
            ((TextView)tabView).setTextColor(ContextCompat.getColor(parent.getContext(),R.color.colorPrimary));
        }


    }
}
