package br.com.aaribeiro.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import br.com.aaribeiro.whatsapp.R;
import br.com.aaribeiro.whatsapp.config.ConfiguracaoFirebase;
import br.com.aaribeiro.whatsapp.fragment.ContatosFragment;
import br.com.aaribeiro.whatsapp.fragment.ConversasFragment;
import br.com.aaribeiro.whatsapp.helper.Permissao;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private MaterialSearchView searchView;
    private FragmentPagerItemAdapter fragmentPagerItemAdapter;
    private ViewPager viewPager = null;
    private static final int TAB_CONVERSAS = 0;
    private static final int TAB_CONTATOS = 1;
    private String [] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        configurarToolBar();
        configurarTabs();
        configurarSearchView();
        Permissao.validarPermissoes(this, permissoes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        configurarBotaoPesquisa(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair :
                deslogarUsuario();
                finish();
                break;
            case R.id.menuConfiguracoes :
                Intent intent = new Intent(MainActivity.this, ConfiguracoesActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaPermissaoNegada();
            }
        }
    }

    private void configurarSearchView(){
        searchView = findViewById(R.id.materialSearchPrincipal);
        searchView.setOnQueryTextListener(searchViewQueryListener);
        searchView.setOnSearchViewListener(searchViewListener);
    }

    private void configurarBotaoPesquisa(Menu menu){
        MenuItem menuItem = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(menuItem);
    }

    private void alertaPermissaoNegada(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissoes Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar todas as permissções");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void configurarTabs(){
        fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add(R.string.tab_conversas, ConversasFragment.class)
                        .add(R.string.tab_contatos, ContatosFragment.class)
                        .create());

        viewPager = findViewById(R.id.viewPager);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);

        viewPager.setAdapter(fragmentPagerItemAdapter);
        viewPagerTab.setViewPager(viewPager);
    }

    private void configurarToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);
    }

    private void deslogarUsuario(){
        try {
            firebaseAuth.signOut();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private MaterialSearchView.OnQueryTextListener searchViewQueryListener = new MaterialSearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {

            if (viewPager.getCurrentItem() == TAB_CONVERSAS){
                ConversasFragment conversasFragment = (ConversasFragment) fragmentPagerItemAdapter.getPage(TAB_CONVERSAS);
                if (newText != null && !newText.isEmpty()){
                    conversasFragment.pesquisarConversas(newText);
                }

            } else if (viewPager.getCurrentItem() == TAB_CONTATOS){
                ContatosFragment contatosFragment = (ContatosFragment) fragmentPagerItemAdapter.getPage(TAB_CONTATOS);
                if (newText != null && !newText.isEmpty()){
                    contatosFragment.pesquisarContatos(newText);
                }
            }
            return true;
        }
    };

    private MaterialSearchView.SearchViewListener searchViewListener = new MaterialSearchView.SearchViewListener() {
        @Override
        public void onSearchViewShown() {

        }

        @Override
        public void onSearchViewClosed() {
            if (viewPager.getCurrentItem() == TAB_CONVERSAS) {
                ConversasFragment conversasFragment = (ConversasFragment) fragmentPagerItemAdapter.getPage(TAB_CONVERSAS);
                conversasFragment.recarregarConversas();

            } else if (viewPager.getCurrentItem() == TAB_CONTATOS){
                ContatosFragment contatosFragment = (ContatosFragment) fragmentPagerItemAdapter.getPage(TAB_CONTATOS);
                contatosFragment.recarregarContatos();
            }
        }
    };
}
