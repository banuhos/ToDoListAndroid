package com.banu.bh318.todolist.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.banu.bh318.todolist.R;

/**
 * Created by bh318 on 23.08.2019.
 */

public class ToDoList_Warning {

    private AlertDialog.Builder builder;
    private Context context;

    public ToDoList_Warning(Context context) {
        this.context = context;
    }


    public void registrationSuccesful() {
        builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle("Başarılı!");
        builder.setMessage("İşleminiz başarıyla gerçekleşti mailinizi kontrol ediniz...");
        builder.setIcon(R.drawable.tamam);
        builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void mailControl() {
        builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle("Hata!");
        builder.setMessage("Lütfen mailinizi doğrulayınız...");
        builder.setIcon(R.drawable.hata);
        builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void registrationFailed() {
        builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle("Başarısız!");
        builder.setMessage("İşleminiz gerçekleşirken bir problem oluştu. Bilgilerinizi kontrol edin ve tekrar deneyin.");
        builder.setIcon(R.drawable.hata);
        builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void warning(String mesaj) {
        builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle("Başarısız!");
        builder.setMessage(mesaj);
        builder.setIcon(R.drawable.hata);
        builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void warningNull(String mesaj) {
        builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle("Başarısız!");
        builder.setMessage(mesaj);
        builder.setIcon(R.drawable.hata);
        builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void resetBasarili() {
        builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle("Başarılı!");
        builder.setMessage("Şifrenizi sıfırlamanız için talimatlar gönderdik. Lütfen mailinizi kontrol ediniz...");
        builder.setIcon(R.drawable.tamam);
        builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void resetBasarisiz() {
        builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle("Başarısız!");
        builder.setMessage("Sıfırlama e-postası gönderilemedi. Bilgilerinizi kontrol edin ve tekrar deneyin.");
        builder.setIcon(R.drawable.hata);
        builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void hata(String mesaj) {
        builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle("Başarısız!");
        builder.setMessage(mesaj);
        builder.setIcon(R.drawable.hata);
        builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
