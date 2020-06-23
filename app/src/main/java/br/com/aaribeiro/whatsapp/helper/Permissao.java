package br.com.aaribeiro.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static void validarPermissoes(Activity activity, String[] permissoes){

        if (Build.VERSION.SDK_INT >= 23){
            List<String> permissoesPendentes = new ArrayList<>();

            for (String permissao : permissoes){
                if (ContextCompat.checkSelfPermission(activity, permissao) != PackageManager.PERMISSION_GRANTED){
                    permissoesPendentes.add(permissao);
                }
            }

            if (!permissoesPendentes.isEmpty()){
                String[] novasPermissoes = new String[permissoesPendentes.size()];
                permissoesPendentes.toArray(novasPermissoes);
                ActivityCompat.requestPermissions(activity, novasPermissoes, 1);
            }
        }
    }
}
