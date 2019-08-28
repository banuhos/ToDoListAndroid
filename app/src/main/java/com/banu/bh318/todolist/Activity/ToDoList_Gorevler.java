package com.banu.bh318.todolist.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.banu.bh318.todolist.Adapter.ToDoList_GorevlerAdapter;
import com.banu.bh318.todolist.Modal.ToDoList_Gonderiler;
import com.banu.bh318.todolist.Modal.User;
import com.banu.bh318.todolist.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABShape;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bh318 on 24.08.2019.
 */

public class ToDoList_Gorevler extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener, SearchView.OnQueryTextListener {

    private List<RFACLabelItem> itemtask;
    private RapidFloatingActionHelper rfabHelper;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private EditText baslik, aciklama;
    public TextView txtDate, empty;
    private FirebaseDatabase database;
    private String userId;
    public static String kategoriId;
    public RecyclerView lvItem;
    public ArrayList<ToDoList_Gonderiler> gorevler = new ArrayList<ToDoList_Gonderiler>();
    static ArrayList<User> listkullanici = new ArrayList<>();
    public ToDoList_GorevlerAdapter gorevlerAdapter;
    public android.support.v7.widget.Toolbar toolbar;
    private ToDoList_Warning uyarilar;
    public List<ToDoList_Gonderiler> kontrolList = new ArrayList<ToDoList_Gonderiler>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list_gorevler);

        toolbar = findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);

        empty = findViewById(R.id.bilgiAnasayfa2);

        kategoriId= getIntent().getExtras().getString("kategoriId");

        itemtask = new ArrayList<>();
        uyarilar = new ToDoList_Warning(this);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        rfaLayout = findViewById(R.id.label_list_sample_rfa2);
        rfaButton = findViewById(R.id.label_list_sample_rfab2);
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(ToDoList_Gorevler.this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        itemtask.add(new RFACLabelItem<Integer>()
                .setLabel("Görev Ekle")
                .setResId(R.drawable.task)
                .setIconNormalColor(0xffffffff)
                .setIconPressedColor(0xffffffff)
                .setLabelColor(Color.WHITE)
                .setLabelSizeSp(15)
                .setLabelBackgroundDrawable(RFABShape.generateCornerShapeDrawable(0xaaacacac, RFABTextUtil.dip2px(this, 4)))
                .setWrapper(1)
        );
        rfaContent
                .setItems(itemtask)
                .setIconShadowRadius(RFABTextUtil.dip2px(this, 5))
                .setIconShadowColor(0xffacacac)
                .setIconShadowDy(RFABTextUtil.dip2px(this, 5))
        ;
        rfabHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                rfaButton,
                rfaContent
        ).build();
        gorevGetir();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "." + (month + 1) + "." + year;
        txtDate.setText(date);

    }


    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        positionState(position);
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        positionState(position);
        rfabHelper.toggleContent();
    }

    public void positionState(int position) {
        if (position == 0) {
            gorevEkle();
        }

    }

    public void gorevEkle() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_to_do_list_gorevler_add_popup);
        dialog.setTitle("Görev Bilgisi Ekle");
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        baslik = dialog.findViewById(R.id.taskName2);
        aciklama = dialog.findViewById(R.id.explanationName2);

        Button button = dialog.findViewById(R.id.gorevekle2);
        txtDate = (TextView) dialog.findViewById(R.id.dateName2);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                database.getReference().child("kullanicilar").child(user)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataUser) {
                                if(baslik.getText().toString().equals("") && txtDate.getText().toString().equals("") && aciklama.getText().toString().equals("")){
                                    uyarilar.warningNull("Lütfen boş alan bırakmayınız!!");
                                    dialog.dismiss();
                                }else {
                                    veritabaniGorevEkle();
                                    dialog.dismiss();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });


            }
        });
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void veritabaniGorevEkle() {
        kategoriId = getIntent().getExtras().getString("kategoriId");
        final HashMap<String, Object> gorev = new HashMap<>();
        gorev.put("ad", baslik.getText().toString());
        gorev.put("tarih", txtDate.getText().toString());
        gorev.put("aciklama", aciklama.getText().toString());
        gorev.put("kategoriId", kategoriId);
        gorev.put("userId", userId);
        gorev.put("durum", "0");
        gorev.put("kilitDurum", "0");

        database.getReference().child("kullanicilar").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        database.getReference().child("gorevler").push().setValue(gorev)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        final DatabaseReference dbRef = database.getReference("gorevler");
                                        gorevler = new ArrayList<>();
                                        lvItem = findViewById(R.id.gorevlerItemList);
                                        database.getReference().child("kullanicilar")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        lvItem.setVisibility(View.VISIBLE);
                                                        empty.setVisibility(View.GONE);
                                                        for (final DataSnapshot ds1 : dataSnapshot.getChildren()) {

                                                            if (listkullanici != null) {

                                                                dbRef.addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                            if (ds1.getKey().equals(ds.child("userId").getValue().toString()) && ds.child("kategoriId").getValue().equals(kategoriId)) {
                                                                                final String gorev = ds.child("ad").getValue().toString();
                                                                                String tarih = ds.child("tarih").getValue().toString();
                                                                                String aciklama = ds.child("aciklama").getValue().toString();
                                                                                final String gorevId = ds.getKey();
                                                                                String vsbl = "VISIBLE";
                                                                                String gone = "GONE";
                                                                                if (gorevler.isEmpty() || ds.child("kilitDurum").getValue().equals("2")) {
                                                                                    gorevler.add(new ToDoList_Gonderiler(gorevId, gorev, aciklama, tarih, gone, vsbl));
                                                                                } else {
                                                                                    gorevler.add(new ToDoList_Gonderiler(gorevId, gorev, aciklama, tarih, vsbl, gone));
                                                                                }

                                                                            }
                                                                        }
                                                                        gorevlerAdapter = new ToDoList_GorevlerAdapter(ToDoList_Gorevler.this, gorevler);
                                                                        lvItem.setLayoutManager(new GridLayoutManager(ToDoList_Gorevler.this, 1));
                                                                        lvItem.setAdapter(gorevlerAdapter);
                                                                        dbRef.removeEventListener(this);
                                                                        gorevlerAdapter.notifyDataSetChanged();


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
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });

    }

    public void gorevGetir() {
        kategoriId = getIntent().getExtras().getString("kategoriId");
        final DatabaseReference dbRef = database.getReference("gorevler");
        gorevler = new ArrayList<>();
        lvItem = findViewById(R.id.gorevlerItemList);


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
                                                final String gorev = ds.child("ad").getValue().toString();
                                                final String aciklama = ds.child("aciklama").getValue().toString();
                                                final String tarih = ds.child("tarih").getValue().toString();
                                                String gorevId = ds.getKey();
                                                String vsbl = "VISIBLE";
                                                String gone = "GONE";
                                                if (gorevler.isEmpty() || ds.child("kilitDurum").getValue().equals("2")) {
                                                    gorevler.add(new ToDoList_Gonderiler(gorevId, gorev, aciklama, tarih, gone, vsbl));
                                                } else {
                                                    gorevler.add(new ToDoList_Gonderiler(gorevId, gorev, aciklama, tarih, vsbl, gone));
                                                }

                                            }

                                        }
                                        if (!gorevler.isEmpty()) {
                                            gorevlerAdapter = new ToDoList_GorevlerAdapter(ToDoList_Gorevler.this, gorevler);
                                            lvItem.setLayoutManager(new GridLayoutManager(ToDoList_Gorevler.this, 1));
                                            lvItem.setAdapter(gorevlerAdapter);
                                            dbRef.removeEventListener(this);
                                            gorevlerAdapter.notifyDataSetChanged();
                                        } else {
                                            lvItem.setVisibility(View.GONE);
                                            empty.setVisibility(View.VISIBLE);
                                        }
                                      //  dbRef.child("gorevler").removeEventListener(this);

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
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        database = FirebaseDatabase.getInstance();
        lvItem = findViewById(R.id.gorevlerItemList);
        final DatabaseReference dbRef = database.getReference("gorevler");
        kategoriId = getIntent().getExtras().getString("kategoriId");
        final ArrayList<ToDoList_Gonderiler> newList = new ArrayList<>();
        final String vsbl = "VISIBLE";
        final String gone = "GONE";

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userInput = newText.toLowerCase();

                if (!userInput.isEmpty()) {
                    for (int i = 0; i < gorevler.size(); i++) {
                        if (gorevler.get(i).getBaslik().toLowerCase().contains(userInput.toLowerCase()) || gorevler.get(i).getAciklama().toLowerCase().contains(userInput.toLowerCase()) || gorevler.get(i).getDate().toLowerCase().contains(userInput.toLowerCase())) {
                            if (newList.isEmpty() || dataSnapshot.child(gorevler.get(i).getGorevId()).child("kilitDurum").getValue().equals("2")) {
                                newList.add(new ToDoList_Gonderiler(gorevler.get(i).getGorevId(), gorevler.get(i).getBaslik(), gorevler.get(i).getAciklama(), gorevler.get(i).getDate(), gone, vsbl));
                            } else {
                                newList.add(new ToDoList_Gonderiler(gorevler.get(i).getGorevId(), gorevler.get(i).getBaslik(), gorevler.get(i).getAciklama(), gorevler.get(i).getDate(), vsbl, gone));
                            }


                        }
                    }
                } else {
                    gorevlerAdapter = new ToDoList_GorevlerAdapter(ToDoList_Gorevler.this, gorevler);
                    lvItem.setLayoutManager(new GridLayoutManager(ToDoList_Gorevler.this, 1));
                    lvItem.setAdapter(gorevlerAdapter);
                    dbRef.removeEventListener(this);
                    gorevlerAdapter.notifyDataSetChanged();

                }

                if (newList.size() != 0) {
                    gorevlerAdapter = new ToDoList_GorevlerAdapter(ToDoList_Gorevler.this, newList);
                    lvItem.setLayoutManager(new GridLayoutManager(ToDoList_Gorevler.this, 1));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent1 = new Intent(ToDoList_Gorevler.this, ToDoList_Tamamlananlar.class);
                intent1.putExtra("kategoriId", kategoriId);
                startActivity(intent1);
                break;
            case R.id.item2:
                Intent intent2 = new Intent(ToDoList_Gorevler.this, ToDoList_Tamamlanmayanlar.class);
                intent2.putExtra("kategoriId", kategoriId);
                startActivity(intent2);
                break;
            case R.id.item3:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ToDoList_Gorevler.this, ToDoList_Login.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
