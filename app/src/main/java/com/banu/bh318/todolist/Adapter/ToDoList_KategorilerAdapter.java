package com.banu.bh318.todolist.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.banu.bh318.todolist.Activity.ToDoList_Gorevler;
import com.banu.bh318.todolist.Modal.ToDoList_Gonderiler;
import com.banu.bh318.todolist.Modal.User;
import com.banu.bh318.todolist.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bh318 on 23.08.2019.
 */

public class ToDoList_KategorilerAdapter extends RecyclerView.Adapter<ToDoList_KategorilerAdapter.MyViewHolder> {
    private LayoutInflater mInflater;
    private List<ToDoList_Gonderiler> mgorevListesi;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private String  date,userId;
    ToDoList_Gonderiler gorevler;
    private Context mContext;
    static ArrayList<User> listkullanici = new ArrayList<>();

    public ToDoList_KategorilerAdapter(Context context, List<ToDoList_Gonderiler> gorevler) {
        this.mContext=context;
        this.mgorevListesi=gorevler;
        //notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.activity_to_do_list_kategoriler_row, parent, false);
        database = FirebaseDatabase.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        myViewHolder.gorev.setText(mgorevListesi.get(i).getBaslik());
        myViewHolder.tarih.setText(mgorevListesi.get(i).getDate());


        database.getReference().child("kategoriler").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    database.getReference().child("kategoriler").removeEventListener(this);
                    if(!mgorevListesi.isEmpty()) {
                        if (ds.getKey().equals(mgorevListesi.get(i).getGorevId()) && ds.child("durum").getValue().equals("1")) {
                            myViewHolder.checkBox.setChecked(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myViewHolder.cardview_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,ToDoList_Gorevler.class);
                intent.putExtra("kategoriId",mgorevListesi.get(i).getGorevId());
                mContext.startActivity(intent);

            }
        });

        myViewHolder.checkBox.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                if(isChecked){
                    durumGuncelle(mgorevListesi.get(i).getGorevId(),"1");
                }else{
                    durumGuncelle(mgorevListesi.get(i).getGorevId(),"0");
                }

            }



        });

    }


    public void durumGuncelle(String pozisyon,String durum){
        database.getReference().child("kategoriler").child(pozisyon).child("durum").setValue(durum);
    }

    @Override
    public int getItemCount() {
        return mgorevListesi.size();
    }

    public void removeItem(final int position, final ToDoList_Gonderiler gorevler){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext,R.style.MyDialogTheme);
        alertDialog.setTitle("UYARI");
        alertDialog.setMessage("Bu kategoriyi sildiğinizde bu kategoriye bağlı görevler silinecektir.Kategoriyi silmek istediğinize emin misiniz?");
        alertDialog.setPositiveButton("HAYIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference().child("kategoriler").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for ( DataSnapshot ds : dataSnapshot.getChildren()) {
                            FirebaseDatabase.getInstance().getReference().child("gorevler").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                        if(gorevler.getGorevId().equals(ds1.child("kategoriId").getValue().toString())){
                                            FirebaseDatabase.getInstance().getReference().child("gorevler").child(ds1.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FirebaseDatabase.getInstance().getReference().child("kategoriler").child(gorevler.getGorevId()).removeValue();
                                                }
                                            });
                                        }

                                    }

                                    FirebaseDatabase.getInstance().getReference().child("gorevler").removeEventListener(this);
                                }

                                @Override
                                public void onCancelled (@NonNull DatabaseError databaseError){

                                }
                            });


                        }

                        FirebaseDatabase.getInstance().getReference().child("kategoriler").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled (@NonNull DatabaseError databaseError){

                    }
                });
                mgorevListesi.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mgorevListesi.size());
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();



    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
       TextView gorev;
        TextView tarih;
        CardView cardview_id;
        private CustomCheckBox checkBox;
        public RelativeLayout viewBackground, viewForeground;


        public MyViewHolder(View itemView) {
            super(itemView);
            gorev=itemView.findViewById(R.id.gorev);
            tarih=itemView.findViewById(R.id.dateValue);
            cardview_id=itemView.findViewById(R.id.cardview_id2);
            viewBackground=itemView.findViewById(R.id.view_background);
            viewForeground=itemView.findViewById(R.id.view_foreground);
            checkBox=itemView.findViewById(R.id.check_kategori);

        }
    }
}

