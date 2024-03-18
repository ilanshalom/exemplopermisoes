package com.example.exemplo6_permissoes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.AlarmClock;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    final int PERMISSIONS_CALL_PHONE_ID = 1;
    boolean permissions_call_phone_value = false;
    ActivityResultLauncher<String> mGetContent1 = null;
    ActivityResultLauncher<Intent> mGetContent2 = null;
    TextView txtMsg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMsg = findViewById(R.id.textView);

        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_CALL_PHONE_ID);
        } else {
            permissions_call_phone_value = true;
        }

        //'Pegar foto', colocamos esta lógica no evento onCreate, para atender quando retorne do click no botão 'Pegar foto':
        mGetContent1 = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        // Handle the returned Uri
                        TextView txt = (TextView) findViewById(R.id.textView);
                        txt.setText("Atendendo em onActivityResult, mostramos a foto selecionada (existente no aparelho).");
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ImageView img1 = findViewById(R.id.imageView);
                        img1.setImageBitmap(bitmap);
                    }
                });

        //'Tirar foto', colocamos esta lógica no evento onCreate, para atender quando retorne do click no botão 'Tirar foto':
        mGetContent2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult intent) {
                        TextView txt = (TextView) findViewById(R.id.textView);
                        txt.setText("Atendendo em onActivityResult, mostramos a foto que foi tirada com o app padrão de câmera do aparelho.");                        try {
                            Bundle bundle = intent.getData().getExtras();
                            Bitmap bitmap = (Bitmap) (bundle.get("data"));
                            ImageView img = (ImageView) findViewById(R.id.imageView);
                            img.setImageBitmap(bitmap);
                        } catch (Exception emaps) {
                            txtMsg.setText(emaps.getMessage());
                        }
                    }
                });
    }

    public void tirarFoto(View v) {
        try {
            //utilizaremos o atendente mGetContent2 que foi registrado no onCreate:
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mGetContent2.launch(intent);
        } catch (Exception emaps) {
            txtMsg.setText("Permissão ACTION_IMAGE_CAPTURE negada.");
        }
    }

    public void abrirFoto(View v) {
        //utilizaremos o atendente mGetContent1 que foi registrado no onCreate:
        mGetContent1.launch("image/*");
    }

    public void abrirSite(View v) {
        Uri uri = Uri.parse("http://www.android.com");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    public void navegarParaAF(View v) {
        Toast.makeText(this, "Entramos em navegarParaAF", Toast.LENGTH_LONG).show();
        try {
            Uri uri = Uri.parse("https://goo.gl/maps/BV8qALfaQZaKar9r6");
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        } catch (Exception emaps) {
            Toast.makeText(this, emaps.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void fazerChamada(View v) {
        Intent intentcall = new Intent();
        intentcall.setAction(Intent.ACTION_CALL);
        intentcall.setData(Uri.parse("tel:123456789"));
        if (permissions_call_phone_value) {
            startActivity(intentcall);
        } else {
            Toast.makeText(this, "\nAs ligações não foram autorizadas neste aparelho.\n", Toast.LENGTH_LONG).show();
        }
    }

    public void abrirContato(View v) {
        Uri uri = Uri.parse("content://contacts/people");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    public void enviarEmail(View v) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{"cruzeirodosul@cruzeirodosul.edu.br"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Assunto da mensagem");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Texto da mensagem");
        startActivity(Intent.createChooser(emailIntent, "Enviando e-mail..."));
    }

    public void enviarSms(View v) {
        String numeroFone = "99999-9999";
        String textoMensagem = "Vou chegar em 20 minutos...";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + numeroFone));
        intent.putExtra("sms_body", textoMensagem);
        startActivity(intent);
    }

    public void compartilhar(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "What a wonderful app!");
        startActivity(i);
    }

    public void criarAlarme(View v) {
            int segundos = 10; //esperar 10 segundos até ativar o alarme

            //preparamos o "broadcast receiver" (a classe que implementa o "receptor de transmissão")
            Intent intent = new Intent(this, MyBroadcastReceiver.class);

            //além da Intent anterior, também especificamos um código privado deste sender (2000 como exemplo)
            //e flags (usamos a tag PendingIntent.FLAG_IMMUTABLE obrigatória, para ignorar qualquer parâmetro adicional):
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this.getApplicationContext(), 2000, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis()   //RTC, RTC_WAKEUP liga o celular,
                    + (segundos * 1000), pendingIntent);

            Toast.makeText(this, "O alarme tocará em " + segundos + " segundos", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_CALL_PHONE_ID:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissions_call_phone_value = true;
                } else {
                    permissions_call_phone_value = false;
                }
                return;
        }
    }

}
