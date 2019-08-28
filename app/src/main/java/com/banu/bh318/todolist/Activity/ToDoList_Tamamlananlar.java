package com.banu.bh318.todolist.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.banu.bh318.todolist.Adapter.ToDoList_GorevlerAdapter;
import com.banu.bh318.todolist.Modal.ToDoList_Gonderiler;
import com.banu.bh318.todolist.Modal.User;
import com.banu.bh318.todolist.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bh318 on 26.08.2019.
 */

public class ToDoList_Tamamlananlar extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,SearchView.OnQueryTextListener{

    private List<RFACLabelItem> itemtask;
    public TextView txtDate;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private String userId, kategoriId;
    public RecyclerView lvItem;
    public ArrayList<ToDoList_Gonderiler> gorevler = new ArrayList<ToDoList_Gonderiler>();
    static ArrayList<User> listkullanici = new ArrayList<>();
    public ToDoList_GorevlerAdapter gorevlerAdapter;
    public android.support.v7.widget.Toolbar toolbar;
    public TextView empty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list_tamamlananlar);

        toolbar=findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);

        empty= findViewById(R.id.bilgiAnasayfa2_tamamlananlar);

        itemtask = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();

        gorevGetir();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "." + (month + 1) + "." + year;
        txtDate.setText(date);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        database = FirebaseDatabase.getInstance();
        lvItem=findViewById(R.id.gorevlerItemList_tamamlananlar);
        final DatabaseReference dbRef = database.getReference("gorevler");
        kategoriId = getIntent().getExtras().getString("kategoriId");
        final ArrayList<ToDoList_Gonderiler> newList = new ArrayList<>();
        final String vsbl="VISIBLE";
        final String gone="GONE";

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userInput = newText.toLowerCase();

                if (!userInput.isEmpty()) {
                    for (int i = 0; i < gorevler.size(); i++) {
                        if (gorevler.get(i).getBaslik().toLowerCase().contains(userInput.toLowerCase()) || gorevler.get(i).getAciklama().toLowerCase().contains(userInput.toLowerCase())) {
                            if (newList.isEmpty() || dataSnapshot.child(gorevler.get(i).getGorevId()).child("kilitDurum").getValue().equals("2")) {
                                newList.add(new ToDoList_Gonderiler(gorevler.get(i).getGorevId(), gorevler.get(i).getBaslik(), gorevler.get(i).getAciklama(), gorevler.get(i).getDate(), gone, vsbl));
                            } else {
                                newList.add(new ToDoList_Gonderiler(gorevler.get(i).getGorevId(), gorevler.get(i).getBaslik(), gorevler.get(i).getAciklama(), gorevler.get(i).getDate(), vsbl, gone));
                            }
                        }
                    }
                } else {
                    gorevlerAdapter = new ToDoList_GorevlerAdapter(ToDoList_Tamamlananlar.this, gorevler);
                    lvItem.setLayoutManager(new GridLayoutManager(ToDoList_Tamamlananlar.this, 1));
                    lvItem.setAdapter(gorevlerAdapter);
                    dbRef.removeEventListener(this);
                    gorevlerAdapter.notifyDataSetChanged();
                }
                if (newList.size() != 0) {
                    gorevlerAdapter = new ToDoList_GorevlerAdapter(ToDoList_Tamamlananlar.this, newList);
                    lvItem.setLayoutManager(new GridLayoutManager(ToDoList_Tamamlananlar.this, 1));
                    lvItem.setAdapter(gorevlerAdapter);
                    dbRef.removeEventListener(this);
                    gorevlerAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return true;
    }



    public void gorevGetir() {
        kategoriId = getIntent().getExtras().getString("kategoriId");
        final DatabaseReference dbRef = database.getReference("gorevler");
        gorevler = new ArrayList<>();
        lvItem = findViewById(R.id.gorevlerItemList_tamamlananlar);

        database.getReference().child("kullanicilar")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot ds1 : dataSnapshot.getChildren()) {
                            if (listkullanici != null) {
                                dbRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            if (ds.child("kategoriId").getValue().equals(kategoriId)) {
                                                if(ds.child("durum").getValue().equals("1")) {
                                                    final String gorev = ds.child("ad").getValue().toString();
                                                    final String aciklama = ds.child("aciklama").getValue().toString();
                                                    final String tarih = ds.child("tarih").getValue().toString();
                                                    String gorevId = ds.getKey();
                                                    String vsbl = "VISIBLE";
                                                    String gone = "GONE";
                                                    gorevler.add(new ToDoList_Gonderiler(gorevId, gorev, aciklama, tarih,gone , vsbl));
                                                }
                                            }
                                        }
                                        if (!gorevler.isEmpty()) {
                                            gorevlerAdapter = new ToDoList_GorevlerAdapter(ToDoList_Tamamlananlar.this, gorevler);
                                            lvItem.setLayoutManager(new GridLayoutManager(ToDoList_Tamamlananlar.this, 1));
                                            lvItem.setAdapter(gorevlerAdapter);
                                            dbRef.removeEventListener(this);
                                            gorevlerAdapter.notifyDataSetChanged();
                                        }else{
                                            lvItem.setVisibility(View.GONE);
                                            empty.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                Intent intent1=new Intent(ToDoList_Tamamlananlar.this,ToDoList_Tamamlananlar.class);
                intent1.putExtra("kategoriId",kategoriId);
                startActivity(intent1);
                break;
            case R.id.item2:
                Intent intent2=new Intent(ToDoList_Tamamlananlar.this,ToDoList_Tamamlanmayanlar.class);
                intent2.putExtra("kategoriId",kategoriId);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
