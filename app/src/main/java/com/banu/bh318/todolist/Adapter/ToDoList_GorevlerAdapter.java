package com.banu.bh318.todolist.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.banu.bh318.todolist.Modal.ToDoList_Gonderiler;
import com.banu.bh318.todolist.Modal.User;
import com.banu.bh318.todolist.R;
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
 * Created by bh318 on 24.08.2019.
 */

public class ToDoList_GorevlerAdapter extends RecyclerView.Adapter<ToDoList_GorevlerAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private List<ToDoList_Gonderiler> mgorevListesi;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private String date, userId;
    ToDoList_Gonderiler gorevler;
    private static Context mContext;
    static ArrayList<User> listkullanici = new ArrayList<>();

    public ToDoList_GorevlerAdapter(Context context, List<ToDoList_Gonderiler> gorevler) {
        this.mContext = context;
        this.mgorevListesi = gorevler;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.activity_to_do_list_gorevler_row, parent, false);
        database = FirebaseDatabase.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        return new ToDoList_GorevlerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        myViewHolder.gorev.setText(mgorevListesi.get(i).getBaslik());
        myViewHolder.aciklama.setText(mgorevListesi.get(i).getAciklama());
        myViewHolder.tarih.setText(mgorevListesi.get(i).getDate());


        if (!mgorevListesi.get(i).getGorunumCheckbox().equals("VISIBLE")) {
            myViewHolder.checkBox.setVisibility(View.VISIBLE);
            myViewHolder.lock.setVisibility(View.GONE);
        }

        database.getReference().child("gorevler").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    database.getReference().child("gorevler").removeEventListener(this);
                    if (ds.getKey().equals(mgorevListesi.get(i).getGorevId()) && ds.child("durum").getValue().equals("1")) {
                        myViewHolder.checkBox.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gorevler = mgorevListesi.get(i);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
                alertDialog.setTitle("UYARI");
                alertDialog.setMessage("Görevi silmek istediğinize emin misiniz?");
                alertDialog.setPositiveButton("HAYIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("EVET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.getReference().child("gorevler").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                FirebaseDatabase.getInstance().getReference().child("gorevler").child(gorevler.getGorevId()).removeValue();
                                database.getReference().child("gorevler").removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        deleteItem(i);
                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();


            }
        });


        myViewHolder.checkBox.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    durumGuncelle(mgorevListesi.get(i).getGorevId(), "1");
                    if (i + 1 < mgorevListesi.size()) {
                        kilitDurumGuncelle(mgorevListesi.get(i + 1).getGorevId(), "2");
                    }
                } else {
                    durumGuncelle(mgorevListesi.get(i).getGorevId(), "0");
                }

            }


        });


    }


    public void durumGuncelle(String pozisyon, String durum) {
        database.getReference().child("gorevler").child(pozisyon).child("durum").setValue(durum);
    }

    public void kilitDurumGuncelle(String pozisyon, String durum) {
        database.getReference().child("gorevler").child(pozisyon).child("kilitDurum").setValue(durum);
    }

    public void deleteItem(int position) {
        mgorevListesi.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mgorevListesi.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView gorev;
        TextView aciklama;
        TextView tarih;
        CardView cardview_id;
        ImageView delete, lock;
        private CustomCheckBox checkBox;


        public MyViewHolder(View itemView) {
            super(itemView);
            gorev = itemView.findViewById(R.id.gorevItem);
            aciklama = itemView.findViewById(R.id.gorevAciklamaItem);
            tarih = itemView.findViewById(R.id.dateValueItem);
            cardview_id = itemView.findViewById(R.id.cardview_id3);
            checkBox = itemView.findViewById(R.id.check_gorev_durum);
            delete = itemView.findViewById(R.id.delete_red_button);
            lock = itemView.findViewById(R.id.lock);

        }
    }

}
