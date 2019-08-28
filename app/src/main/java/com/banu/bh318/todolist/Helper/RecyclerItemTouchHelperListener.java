package com.banu.bh318.todolist.Helper;

import android.support.v7.widget.RecyclerView;

/**
 * Created by bh318 on 24.08.2019.
 */

public interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder,int direction, int position);

}
