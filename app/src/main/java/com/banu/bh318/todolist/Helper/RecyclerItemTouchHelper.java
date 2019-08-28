package com.banu.bh318.todolist.Helper;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.banu.bh318.todolist.Adapter.ToDoList_KategorilerAdapter;
import com.banu.bh318.todolist.Modal.ToDoList_Gonderiler;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by bh318 on 24.08.2019.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;
    public DatabaseReference root= FirebaseDatabase.getInstance().getReference();
    public ArrayList<ToDoList_Gonderiler> gorevList;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs,RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener=listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        if(listener != null){
            listener.onSwiped(viewHolder,i,viewHolder.getAdapterPosition());
           /* FirebaseDatabase.getInstance().getReference().child("gorevler").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        FirebaseDatabase.getInstance().getReference().child("gorevler").child().removeValue();

                    }

                    FirebaseDatabase.getInstance().getReference().child("gorevler").removeEventListener(this);
                }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }
            });*/
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
       View foreGround=((ToDoList_KategorilerAdapter.MyViewHolder) viewHolder).viewForeground;
       getDefaultUIUtil().clearView(foreGround);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGround=((ToDoList_KategorilerAdapter.MyViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDraw(c,recyclerView,foreGround,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGround=((ToDoList_KategorilerAdapter.MyViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDrawOver(c,recyclerView,foreGround,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder != null){
            View foreGround=((ToDoList_KategorilerAdapter.MyViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onSelected(foreGround);
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
}
