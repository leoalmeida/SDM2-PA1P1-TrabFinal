package br.edu.ifspsaocarlos.sdm.trabalhofinal.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.TimerTask;

import br.edu.ifspsaocarlos.sdm.trabalhofinal.R;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.components.OnSwipeTouchListener;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.model.GameControl;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class JogoXadrezActivity extends AppCompatActivity {


    private final   static String TAG = "JogoXadrezActivity";
    private static  final boolean AUTO_HIDE = true;
    private static  final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static  final int UI_ANIMATION_DELAY = 300;
    private View    mContentView;
    private View    mControlsView;
    private boolean mVisible;


    private View    mControlsButton;
    private TextView txtTempoJogo;
    private TextView txtTempoJogadorEspera;
    private TextView txtTempoJogadorAtual;
    private TextView txtNomeJogadorEspera;
    private TextView txtNomeJogadorAtual;

    private GameControl gamePlay;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Seleciona Layout do jogo  e Inicia variáveis");
        setContentView(R.layout.activity_crono_xadrez);

        mVisible = true;
        mControlsView = findViewById(R.id.frame_controls);
        mContentView = findViewById(R.id.frame_preview);
        mControlsButton = findViewById(R.id.floating_shape);
        txtTempoJogo = (TextView) findViewById(R.id.txtTempoJogo);
        txtTempoJogadorAtual = (TextView) findViewById(R.id.tempoJogadorAtual);
        txtTempoJogadorEspera = (TextView) findViewById(R.id.tempoJogadorEspera);
        txtNomeJogadorAtual = (TextView) findViewById(R.id.nomeJogadorAtual);
        txtNomeJogadorEspera = (TextView) findViewById(R.id.nomeJogadorEspera);

        Log.d(TAG, "Cria um listener para responder ao fechamento da tela de configuração");
        mControlsView.setOnTouchListener(new OnSwipeTouchListener(this) {

            @Override
            public void onSwipeTop() {
                Log.d(TAG, "Swipe Up on view.");
                toggle();
            }

        });

        Log.d(TAG, "Cria um listener para responder a abertura da tela de configuração");
        mContentView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeBottom() {
                Log.d(TAG, "Swipe Bottom on view.");
                toggle();
            }

        });

        Log.d(TAG, "Cria um listener para responder aos eventos do Controle de jogo");
        mControlsButton.setOnTouchListener(new OnSwipeTouchListener(this) {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean inicioJogo = gamePlay.tick();
                if (!inicioJogo) {
                    String NomejogAtual = txtNomeJogadorAtual.getText().toString();
                    txtNomeJogadorAtual.setText(txtNomeJogadorEspera.getText());
                    txtNomeJogadorEspera.setText(NomejogAtual);
                    if (view.getTranslationZ() > 0) view.setTranslationZ(0);
                    else view.setTranslationZ(120);
                }

                return true ;
            }
        });

        Log.d(TAG, "Desabilita Controle de jogo ate o inicio");
        mControlsButton.setEnabled(false);

    }

    public void startGame(View v) {
        Log.d(TAG, "Botão de inicio do jogo clicado");

        Log.d(TAG, "Configura novo Game Play");
        TextView txtConfJogadorAtual = (TextView) findViewById(R.id.txtJogador1);
        TextView txtConfJogadorEspera = (TextView) findViewById(R.id.txtJogador2);
        txtNomeJogadorAtual.setText(txtConfJogadorAtual.getText());
        txtNomeJogadorEspera.setText(txtConfJogadorEspera.getText());
        int qtd_players = 2;
        gamePlay = new GameControl(qtd_players, new Long(txtTempoJogo.getText().toString()), txtTempoJogadorAtual, txtTempoJogadorEspera);

        Log.d(TAG, "Volta o contexto para a tela de jogo");
        toggle();

        Log.d(TAG, "Habilita Controle de jogo");
        mControlsButton.setEnabled(true);
    }



    //Código relacionado à troca de contexto entre as telas
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mContentView.setVisibility(View.VISIBLE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
               mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
